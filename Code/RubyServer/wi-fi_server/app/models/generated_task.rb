class GeneratedTask < ActiveRecord::Base
  belongs_to :wifi_task
  belongs_to :user
  belongs_to :wifi_status

  attr_accessible :wifi_task_id, :user_id, :wifi_status_id, :position_in_path

  validates_presence_of :wifi_task_id, :user_id, :wifi_status_id, :position_in_path

  def s
    save_to_statistic
  end

  def save_to_statistic
    TaskResult.c(self)
  end

  def request
    wifi_task.wifi_request.requested_count += 1
    change_quota(true)
  end

  def cancel(state = nil)
    if(!self.wifi_status.nil?)
      if(self.wifi_status.completes_task || self.wifi_status.cancels_task)
        return
      end
    end
    isNotStatus = state == nil || state.class != WifiStatus
    isNotStatusForCancel = isNotStatus ? false : state.cancels_task

    if(isNotStatus || isNotStatusForCancel)
      state = WifiStatus.where(cancels_task:true).first
    end

    self.wifi_status = state
    self.save
    if(wifi_task.wifi_request.cancelled_count.nil?)
      wifi_task.wifi_request.cancelled_count = 0
    end
    wifi_task.wifi_request.cancelled_count += 1
    change_quota(false)
    save_to_statistic
  end

  def complete(state = nil)
    if(!self.wifi_status.nil?)
      if(self.wifi_status.completes_task || self.wifi_status.cancels_task)
        return
      end
    end
    isNotStatus = state == nil || state.class != WifiStatus
    isNotStatusForComplete = isNotStatus ? false : state.completes_task
    if(isNotStatus || isNotStatusForComplete)
      state = WifiStatus.where(completes_task:true).first
    end

    self.wifi_status = state
    self.save
    if(wifi_task.wifi_request.completed_count.nil?)
      wifi_task.wifi_request.completed_count = 0
    end
    wifi_task.wifi_request.completed_count += 1
    change_quota(false)
    save_to_statistic
  end

  def name
    return "#{self.user.name} #{self.wifi_task.name} -> #{self.wifi_status.name} -> #{self.position_in_path}"
  end

  private

  def change_quota(take)
    count = 1
    if(take)
      count *= -1
    end
    wifi_task.quota_daily_current += count
    wifi_task.quota_weekly_current += count
    wifi_task.quota_monthly_current += count

    wifi_task.wifi_request.count += count
    wifi_task.wifi_request.portion += count
    
    wifi_task.wifi_request.save

    wifi_task.save
  end
end
