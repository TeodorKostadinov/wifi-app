class WifiTask < ActiveRecord::Base
  belongs_to :wifi_spot
  belongs_to :wifi_request

  attr_accessible :wifi_spot_id, :wifi_request_id,
   :quota_daily_current, :quota_weekly_current, :quota_monthly_current,
   :last_update_day, :last_update_week, :last_update_month

  validates_presence_of :wifi_spot_id, :wifi_request_id

  def request
    return wifi_request
  end

  def name
    begin
      return "send #{self.wifi_request.name} from #{self.wifi_spot.name}"
    rescue
      return "Name of task could not be got."
    end
  end

  def on_create
    update_quota(Time.now, self.request, true)
  end

  def create
    on_create
    super
  end

#passed from wifi_request check_quota
#req == request itself ( self.request )
#const == Constant.quota
  def update_quota(now, req, new_record = false)
    nowDT = get_Datetime(now)
    days = get_days(last_update_day, now)
    week_days = get_days(last_update_week, now)
    month_days = get_days(last_update_month, now)
    
    has_change = false
    
    if(days > 0) #( 1+ days passed )
      self.last_update_day = nowDT
      self.update_daily(req, false)
      
      has_change = true
    end
    if(week_days > 6) #( 7+ days passed )
      self.last_update_week = nowDT
      self.update_weekly(req, false)

      has_change = true
    end
    if(month_days > 29) #( 30+ days passed )
      self.last_update_month = nowDT
      self.update_monthly(req, false)

      has_change = true
    end

    if(has_change && !new_record)
      self.save
    end
  end

  def update_daily(req, save_after_change = true)
    self.quota_daily_current = req.quota_daily

    if(save_after_change)
      self.save
    end
  end

  def update_weekly(req, save_after_change = true)
    self.quota_weekly_current = req.quota_weekly

    if(save_after_change)
      self.save
    end
  end

  def update_monthly(req, save_after_change = true)
    self.quota_monthly_current = req.quota_monthly

    if(save_after_change)
      self.save
    end
  end

  def has_free_quota
    return quota_daily_current != 0 && quota_weekly_current != 0 && quota_monthly_current != 0
  end

      private
  def get_days(aa, bb)
    a = aa
    b = bb
    if(a.nil? || b.nil?)
      return 365
    end

    if(a.class != Time && a.class == DateTime)
      a = get_Time(a)
    end
    if(b.class != Time && b.class == DateTime)
      b = get_Time(b)
    end

    result = (a - b).to_i / 1.day
    return result.abs
  end

  def get_Datetime(time)
    if(time.class != Time)
      return nil
    end
    return DateTime.new(
     time.year, time.month, time.day,
     time.hour, time.min, time.sec)
  end

  def get_Time(datetime)
    if(datetime.class != DateTime)
      return nil
    end
    return Time.new(
     datetime.year, datetime.month, datetime.day,
     datetime.hour, datetime.minute, datetime.second)
  end
end
