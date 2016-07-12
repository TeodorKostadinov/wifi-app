class StatusController < ApplicationController
  skip_before_filter :verify_authenticity_token,
                     :if => Proc.new { |c| c.request.format == 'application/json' }

  # Just skip the authentication for now
  # before_filter :authenticate_user!

  respond_to :json

  def get
    language = params[:language]
    if(current_user.nil?)
      audit = Audit.new
      audit.ip = params[:ip]
      audit.controller = "Status"
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
    audit.controller = "Status"
    audit.action ="Get"
    audit.transaction do

      return_statuses = []
      for s in WifiStatus.all do
        return_statuses.push(:Name => s.name)
      end

       render :json => {:success => true,
                        :info => Message.get(language, "StatusGet").text,
                        :data => return_statuses }
      audit.result_id = AuditResult.success.id
      audit.save
    end
  end

  def status_update
    language = params[:language]
    if(current_user.nil?)
      audit = Audit.new
      audit.ip = params[:ip]
      audit.controller = "Spots"
      audit.action ="Status Update"
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
    audit.controller = "Status"
    audit.action ="Update"
    audit.transaction do
      status = params[:status]
      ip = status[:ip]
      value = status[:value]
      task = Integer(status[:id])

      values = WifiStatus.where(:name => value)
      if(values.length == 1)
        gt = current_user.generated_tasks.where(:id => task)

        if(gt.length == 1)
          index = -1 
          for i in 0...current_user.generated_tasks.length do
            if(current_user.generated_tasks[i].id == gt[0].id)
              index = i
              break
            end
          end

          ut = current_user.generated_tasks[index]

          if(ut.wifi_status.completes_task)
            puts "Task is already completed"
            render :json => {:success => false,
                        :info => Message.get(language, "TaskAlreadyCompleted", ["#{ut.wifi_status.name}"]).text,
                        :data => {} }
            audit.message = "Task #{ut.id} on #{current_user.id} is already completed when it was set to:#{ut.wifi_status.name}"
            audit.result_id = AuditResult.error.id
            audit.save
            return
          end
          
          if(values[0].completes_task)
            if(ut.wifi_task.wifi_spot.ip.to_s == ip)
              ut.complete
              current_user.level += 1
              current_user.save
            else
              puts "#{ip} does not match wifi spot's ip!:{#{ut.wifi_task.wifi_spot.ip}}"
              render :json => {:success => false,
                          :info => Message.get(language, "MissMatchIPForUpdate", ["#{ip}", "#{ut.wifi_task.wifi_spot.ip}"]).text,
                          :data => {} }
              audit.message = "#{ip} does not match wifi spot's ip!:{#{ut.wifi_task.wifi_spot.ip}}"
              audit.result_id = AuditResult.error.id
              audit.save
              return
            end
          else
            ut.cancel
          end

          render :json => {:success => true,
                      :info => Message.get(language, "StatusUpdate").text,
                      :data => {:level => current_user.level} 
                    }

          audit.result_id = AuditResult.success.id
          audit.save
        else
          puts "#{task} for task in #{current_user.name}{#{current_user.id}} not found."
          render :json => {:success => false,
                        :info => Message.get(language, "TaskNotFoundInUser", ["#{task.id}", "#{current_user.name}", "#{current_user.id}"]).text,
                        :data => {} }
          audit.message = "#{task.id} for task in #{current_user.name}{#{current_user.id}} not found."
          audit.result_id = AuditResult.error.id
          audit.save
        end
      else
        puts "#{value} not found as a status value! {#{values.length}}"
        render :json => {:success => false,
                        :info => Message.get(language, "InvalidStatus", ["#{value}", "#{values.length}"]).text,
                        :data => {} }
        audit.message = "#{value} not found as a status value! {#{values.length}}"
        audit.result_id = AuditResult.error.id
        audit.save
      end
    end
  end
end