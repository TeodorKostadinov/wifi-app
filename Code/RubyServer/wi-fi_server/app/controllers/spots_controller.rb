class SpotsController < ApplicationController
  skip_before_filter :verify_authenticity_token,
                     :if => Proc.new { |c| c.request.format == 'application/json' }

  respond_to :json

  def add
    language = params[:language]
    if(current_user.nil?)
      audit = Audit.new
      audit.ip = params[:ip]
      audit.controller = "Spots"
      audit.action ="Add"
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
    audit.ip = params[:uip]
    audit.controller = "Spots"
    audit.action ="Add"
    audit.transaction do
      lat = params[:lati].to_f
      lon = params[:longi].to_f
      name = params[:name]
      ip = params[:ip] 
      pass = params[:password]
      addr = params[:address]

      spot = WifiSpot.new
      spot.name = name
      spot.ip = ip
      spot.password = pass
      spot.address = addr

      spot.gmaps = true
      spot.latitude = lat
      spot.longitude = lon

      if(spot.save)
        audit.result_id = AuditResult.success.id
        audit.save
        render :status => 200,
             :json => { :success => true,
                        :info => Message.get(language, "SpotAdd").text,
                        :data => { }
                      }
      else
        lines = 0
        errors = ""
        for e in spot.errors do
          error = ""
          for er in spot.errors[e] do
            error += er
          end

          errors += "#{e.capitalize} #{error}."
          errors += "\n" unless(lines + 1 == spot.errors.keys.length)

          lines += 1
        end

          render :json => { :success => false,
                           :info => errors,
                          :data => {} }
        audit.message ="Unable to wifi spot.\n#{errors}"
        audit.result_id = AuditResult.error.id
        audit.save
      end
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
      audit.controller = "Spots"
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
    audit.controller = "Spots"
    audit.action ="Get"
    audit.transaction do

      spots = WifiSpot.all
      return_spots = []
      current_count = 0
      is_on_page = true
      if has_pages
        is_on_page = false
      end
      for s in spots do
        if(!s.active)
          next
        end

        if(has_pages)
          break if current_count >= maximum_count

           if(current_count == minimum_count)
            is_on_page = true
           end
           current_count += 1
         end
        if(is_on_page)
          return_spots.push(
          :Id => s.id.to_s,
          :Name => s.name,
          :Password => s.password,
          :Ip => s.ip,
          :Address => s.address)
        end
      end

      if(has_pages)
        return_spots.push(:maxPage => spots.length / task_per_page)
      end

       render :json => {
        :success => true,
        :info => Message.get(language, "SpotGet").text,
        :data => return_spots 
      }
      audit.result_id = AuditResult.success.id
      audit.save
    end
  end

  def get_all
    language = params[:language]
    if(current_user.nil?)
      audit = Audit.new
      audit.ip = params[:ip]
      audit.controller = "Spots"
      audit.action ="Get All"
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
    audit.controller = "Spots"
    audit.action ="Get All"
    audit.transaction do
      spots = WifiSpot.all
      return_spots = []

      for s in spots do
        return_spots.push(
        :Id => s.id.to_s,
        :Name => s.name,
        :Password => s.password,
        :Ip => s.ip,
        :Address => s.address,
        :Active => s.active.to_s,
        :Lati => s.latitude,
        :Longi => s.longitude)
      end

       render :json => {
        :success => true,
        :info => Message.get(language, "SpotGetAll").text,
        :data => return_spots 
      }
      audit.result_id = AuditResult.success.id
      audit.save
    end
  end

  def change
    language = params[:language]
    if(current_user.nil?)
      audit = Audit.new
      audit.ip = params[:ip]
      audit.controller = "Spots"
      audit.action ="Change"
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
    audit.controller = "Spots"
    audit.action ="Change"
    audit.transaction do
      newSpot = params[:wifi_spot]
      id = newSpot[:id]
      newName = newSpot[:name]
      newPassword = newSpot[:pass]
      newIP = newSpot[:ip]
      newActive = false
      if( newSpot[:active].to_s == "true")
        newActive = true
      else
         newActive = false
      end
      

      spot = WifiSpot.find(id)
      if(spot.blank?)
        audit.message = "The record was not found in the database. {#{id}}"
        audit.result_id = AuditResult.error.id
        audit.save
        render :json => {
                          :success => false,
                          :info => Message.get(language, "SpotChangeNotFound", ["#{id}"]).text
                        }
       
      else
        spot.name = newName;
        spot.password = newPassword
        spot.ip = newIP
        spot.active = newActive
        spot.save
        audit.result_id = AuditResult.success.id
        audit.save
        render :json => {
                          :success => true,
                          :info => Message.get(language, "SpotChange").text
                        }
      end
    end
  end
end
