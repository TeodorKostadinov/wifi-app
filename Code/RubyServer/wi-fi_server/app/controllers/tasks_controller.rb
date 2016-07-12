class TasksController < ApplicationController
	  skip_before_filter :verify_authenticity_token,
                     :if => Proc.new { |c| c.request.format == 'application/json' }

  # Just skip the authentication for now
  # before_filter :authenticate_user!

  respond_to :json

  @@queue = Containers::PriorityQueue.new

  def has_active
    language = params[:language]
    if(current_user.nil?)
      audit = Audit.new
      audit.ip = params[:ip]
      audit.controller = "Tasks"
      audit.action ="Take"
      audit.user_id = 0
      audit.result_id = AuditResult.error.id
      audit.message = "Unable to logout. The token has expired."
      audit.save

      render :status => 200,
           :json => { :success => false,
                      :info => Message.get(language, "TokenExpired").text,
                      :data => {} }
      return
    end

    audit = Audit.new
    audit.user_id = current_user.id
    audit.ip = params[:ip]
    audit.controller = "Task"
    audit.action ="HasActive"

    completed_and_cancels_sum = 0
    for gt in current_user.generated_tasks do
      if(gt.wifi_status.completes_task || gt.wifi_status.cancels_task)
        completed_and_cancels_sum +=1
      end
    end

    if(completed_and_cancels_sum != current_user.generated_tasks.length)
      render :json => {:success => false,
                      :info => Message.get(language, "CancelOrComplete", ["#{current_user.generated_tasks.length - completed_and_cancels_sum}"]).text,
                      :data => {} }
                      audit.message = "User did not complete his tasks #{completed_and_cancels_sum}/#{current_user.generated_tasks.length}"
      audit.result_id = AuditResult.success.id
      audit.save
      return
    end

    render :json => {:success => true,
                      :info => "",
                      :data => {} }

    audit.result_id = AuditResult.success.id
    audit.save
  end

  def take
    language = params[:language]
    if(current_user.nil?)
      audit = Audit.new
      audit.ip = params[:ip]
      audit.controller = "Tasks"
      audit.action ="Take"
      audit.user_id = 0
      audit.result_id = AuditResult.error.id
      audit.message = "Unable to logout. The token has expired."
      audit.save

      render :status => 200,
           :json => { :success => false,
                      :info => Message.get(language, "TokenExpired").text,
                      :data => {} }
      return
    end

    audit = Audit.new
    audit.user_id = current_user.id
    audit.ip = params[:ip]
    audit.controller = "Task"
    audit.action ="Take"
    WifiSpot.check_quota
    audit.transaction do
      info = params[:task_info]

      lat = info[:lati]
      lon = info[:longi]

      current_coordinates = WifiSpot.new
      current_coordinates.latitude = lat
      current_coordinates.longitude = lon

      sent_time = info[:time]

      hours = sent_time.split(':')[0]
      minutes = sent_time.split(':')[1]

      time = UserTime.new(hours,minutes)
      count = Integer(info[:count])

      time_so_far = 0
      time_total = time.total

      completed_and_cancels_sum = 0
      for gt in current_user.generated_tasks do
        if(gt.wifi_status.completes_task || gt.wifi_status.cancels_task)
          completed_and_cancels_sum +=1
        end
      end

      if(completed_and_cancels_sum != current_user.generated_tasks.length)
        render :json => {:success => false,
                        :info => Message.get(language, "CancelOrComplete", ["#{current_user.generated_tasks.length - completed_and_cancels_sum}"]).text,
                        :data => {} }
                        audit.message = "User did not complete his tasks #{completed_and_cancels_sum}/#{current_user.generated_tasks.length}"
        audit.result_id = AuditResult.success.id
        audit.save
        return
      end

      values = {}
      values[:user] = current_user
      values[:user_latitude] = lat
      values[:user_longitude] = lon
      available_spots = BusinessRule.for_selecting_spots(WifiSpot.all, values).select{|x|x.active}

      spots_str = available_spots.map{|x|x.id}.join(",")

      all_tasks = WifiTask.joins(:wifi_request)
      all_tasks_sql  = "wifi_tasks.quota_daily_current > 0 and "
      all_tasks_sql += "wifi_tasks.quota_weekly_current > 0 and "
      all_tasks_sql += "wifi_tasks.quota_monthly_current > 0 and "
      all_tasks_sql += "wifi_tasks.wifi_spot_id IN (#{spots_str}) and "
      all_tasks_sql += "wifi_requests.count > 0 and "
      all_tasks_sql += "wifi_requests.portion > 0 and "
      all_tasks_sql += "wifi_tasks.wifi_request_id = "

      if(@@queue.nil?)
        @@queue = Containers::PriorityQueue.new
      end

      priority_request = @@queue.next

      if(@@queue.size == 0)
        refresh_queue
      end

      tasks = []

      todo = count
      refreshed = false

      user_point = Point.new(current_coordinates)
      used_spots = []

      while(todo != 0 && @@queue.size > 0)
        if(priority_request.nil?)
          if(refreshed)
            break
          else
            refreshed = true
            refresh_queue
            priority_request = @@queue.next
            next
          end
        end

        count_to_remove = priority_request.portion < todo ? priority_request.portion : todo
        
        sql = all_tasks_sql + priority_request.id.to_s
        if(used_spots.length != 0)
          used_spots_str = used_spots.join(",")
          sql += " and wifi_tasks.wifi_spot_id NOT IN (#{used_spots_str})"
        end

        can_do = all_tasks.where(sql)

        has_to_take_next = false
        if(can_do.length < count_to_remove)
          count_to_remove = can_do.length
          has_to_take_next = true
        end

        to_take = count_to_remove

        for t in can_do.sort{|x|user_point.distance_to(Point.new(x.wifi_spot))} do
          if(to_take == 0)
            break
          end
          used_spots.push(t.wifi_spot_id)
          tasks.push(t)
          to_take -= 1
        end

        todo -= count_to_remove
        priority_request.count -= count_to_remove
        priority_request.portion -= count_to_remove

        @@queue.pop
        if(priority_request.portion == 0 || has_to_take_next)
          priority_request = @@queue.next
        else
          @@queue.push(priority_request, priority_request.portion)
        end
      end

      spots = []
      if(tasks.length != 0)
        spot_ids_str = used_spots.join(",")
        spots = WifiSpot.where("id IN (#{spot_ids_str})")
      end
      
  ########################################################################################################
      path = Algo.closest(spots, current_coordinates, time_total, count)
  ########################################################################################################
      
      t = []

      for i in path do 
        if(i.id.nil?)
          next
        end
        t.push(tasks.select{|x| x.wifi_spot_id == i.id}.first)
      end
      tasks = t

      puts "REQUEST TO TAKE TASKS"
      puts "Requested tasks count:#{count}"
      puts "Requested tasks found:#{tasks.length}"
      if(tasks.length == 0)
        render :json => {:success => true,
                          :info => Message.get(language, "TasksNone").text,
                          :available => false }
      else
        current_user.take_tasks(tasks, count)
          render :json => {:success => true,
                          :info => Message.get(language, "TasksTake").text,
                          :data => tasks,
                          :available => true }
      end
      audit.result_id = AuditResult.success.id
      audit.save
    end
  end

  def miss_all
    language = params[:language]
    if(current_user.nil?)
      audit = Audit.new
      audit.ip = params[:ip]
      audit.controller = "Tasks"
      audit.action ="Miss All"
      audit.user_id = 0
      audit.result_id = AuditResult.error.id
      audit.message = "Unable to logout. The token has expired."
      audit.save

      render :status => 200,
           :json => { :success => false,
                      :info => Message.get(language, "TokenExpired").text,
                      :data => {} }
      return
    end

    audit = Audit.new
    audit.user_id = current_user.id
    audit.ip = params[:ip]
    audit.controller = "Task"
    audit.action ="Miss All"
    audit.transaction do
      for gt in current_user.generated_tasks do
        if(!(gt.wifi_status.completes_task || gt.wifi_status.cancels_task))
          gt.cancel
        end
      end

      render :json => {:success => true,
                      :info => Message.get(language, "MissedAll").text,
                      :data => {} }
      audit.result_id = AuditResult.success.id
      audit.save
    end
  end

  def get
    has_pages = params.has_key?("per_page")

    task_per_page = 0
    current_page = 0
    minimum_count = 0
    maximum_count = 0

    if(has_pages)
      task_per_page = params[:per_page]
      current_page = params[:page]
      minimum_count = current_page * task_per_page
      maximum_count = minimum_count + task_per_page
    end

    language = params[:language]
    if(current_user.nil?)
      audit = Audit.new
      audit.ip = params[:ip]
      audit.controller = "Tasks"
      audit.action ="Get"
      audit.user_id = 0
      audit.result_id = AuditResult.error.id
      audit.message = "Unable to logout. The token has expired."
      audit.save

      render :status => 200,
           :json => { :success => false,
                      :info => Message.get(language, "TokenExpired").text,
                      :data => {} }
      return
    end

    audit = Audit.new
    audit.user_id = current_user.id
    audit.ip = params[:ip]
    audit.controller = "Task"
    audit.action ="Get"
    audit.transaction do
      tasks = []

      for task in current_user.generated_tasks.sort{|x,y| x.position_in_path <=> y.position_in_path} do 
        tasks.push(task)     
      end

      current_count = 0
      return_tasks = []
      is_on_page = true
      if has_pages
        is_on_page = false
      end

      for t in tasks do
        if(has_pages)
          break if current_count >= maximum_count

           if(current_count == minimum_count)
            is_on_page = true
           end
           current_count += 1
        end
        if(is_on_page)
          return_tasks.push(
          :Id => t.id.to_s,
          :Address => t.wifi_task.wifi_spot.address,
          :Name => t.wifi_task.wifi_spot.name,
          :Longi => t.wifi_task.wifi_spot.longitude,
          :Lati => t.wifi_task.wifi_spot.latitude,
          :Password => t.wifi_task.wifi_spot.password,
          :Keyword => t.wifi_task.wifi_request.keyword,
          :Website => t.wifi_task.wifi_request.website,
          :Status => t.wifi_status.name,
          :StatusId => t.wifi_status.id,
          :Position => t.position_in_path)
        end
      end

      if(has_pages)
        return_tasks.push(:maxPage => tasks.length / task_per_page)
      end
      #invered_request = []
      #for i in 0...return_requests.length do
      #  invered_request.push(return_requests[return_requests.length - i - 1])
      #end
      #return_requests = invered_request
        render :json => {:success => true,
        :info => Message.get(language, "TasksGet").text,
        :data => return_tasks
      }
      audit.result_id = AuditResult.success.id
      audit.save
    end
  end

  private

  def refresh_queue
    if(@@queue.size == 0)
      for r in WifiRequest.all do
        portion = r.get_portion
        if(portion == 0)
          next
        end
        @@queue.push(r, portion)
      end
    end
  end

  def generate_new_tasks
    all_requests = WifiRequest.all
    possible_requests = all_requests.select {|x| x.count > 0}
    possible_spots = WifiSpot.all.select{|x| x.active}

    for i in 0...possible_spots.length do
      spot = possible_spots[i]
      has_count = 0
      for r in requests do
        if(spot.wifi_tasks.select{|x| x.request.id == r.id}.length > 0)
          has_count += 1
        end
      end
      if(has_count == possible_requests || has_count == all_requests.length)
        next
      end

      request = possible_requests.sample
      while(spot.wifi_tasks.select{|x| x.wifi_request.id == request.id}.length > 0)
        request = possible_requests.sample
      end
      #spot.wifi_tasks . add new Task{spot, request, QUOTA...}
    end
  end
end

class UserTime
  def initialize(hours, minutes)
    if(hours.blank?)
      hours = 0
    end
    if(minutes.blank?)
      minutes = 0
    end
    h = hours.to_s.to_i
    m = minutes.to_s.to_i

    access_m = m/60
    h += access_m
    m -= 60 * access_m

    @minutes = m
    @hours = h
  end

  def hours
    @hours
  end

  def minutes
    @minutes
  end

  def total
    @hours * 60 + @minutes
  end
end

class Point
  def initialize(x, y = nil)
    if(x.class == WifiSpot)
      @x = x.latitude
      @y = x.longitude
      @spot = x
    else
      @x = x
      @y = y
      @spot = WifiSpot.new()
      @spot.latitude = x
      @spot.longitude = y
    end
  end

  def spot
    @spot
  end

  def x
    @x
  end

  def y
    @y
  end

  def distance_to(b)
    dx = self.x - b.x
    dy = self.y - b.y
    return Math.sqrt((dx * dx) + (dy * dy))
  end

  def self.distance(a, b)
    return a.distance_to(b)
  end
end

class Algo
  def self.closest(points, start_point, max_time, max_count)
    return Algo.new().apply_algorithm_closest(points, start_point, max_time, max_count)
  end

  def apply_algorithm_closest(points, start_point, max_time, max_count)
    path = []
    passed = []
    for point in points do
      passed.push(false)
    end

    path.push(start_point)

    closest_index = algorithm_get_closest_to(start_point, points, passed)

    algorithm_get_closest(points, points[closest_index], passed, closest_index, path, max_time, max_count, 0)
    return path
  end

  private 

  def algorithm_get_closest(points, start_point, passed, index, path, max_time, max_count, passed_count)
    if (index == -1) 
      return
    end

    if (passed[index])
      return
    end

    passed[index] = true
    path.push(points[index])
    passed_count += 1

    if(path.length - 1 >= max_count) # -1 to remove the start point
      return
    end

    if(passed_count == passed.length)
      return
    end

    next_index = algorithm_get_closest_to(start_point, points, passed)

    algorithm_get_closest(points, points[next_index], passed, next_index, path, max_time, max_count, passed_count)
  end

  private

  def algorithm_get_closest_to(point, points, passed)
    index = -1
    from = Point.new(point)
    shortest_distance = Float::MAX

    for i in 0...points.length do
      if (passed[i]) 
        next
      end

      distance = from.distance_to(Point.new(points[i]))

      if (shortest_distance > distance)
        shortest_distance = distance;
        index = i;
      end
    end

    return index
  end
end