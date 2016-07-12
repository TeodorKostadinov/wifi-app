class WifiRequest < ActiveRecord::Base
	attr_accessible :keyword, :website, :count,
  :completed_count, :requested_count, :cancelled_count,
  :quota_daily, :quota_weekly, :quota_monthly,
  :days_to_complete, :portion, :last_update_portion, :portion_start
	
  validates_presence_of :keyword, :website, :count,
   :quota_daily, :quota_weekly, :quota_monthly

  before_save :check

  def check
    if(self.completed_count.nil?)
      self.completed_count = 0
    elsif(self.requested_count.nil?)
      self.requested_count = 0
    elsif(self.cancelled_count.nil?)
      self.cancelled_count = 0
    end
  end

  def name
    return "#{self.keyword} @ #{self.website}"
  end

  def check_quota(now)
    if(self.count == 0)
      return
    end

    for t in WifiTask.where(wifi_request_id:self.id) do 
      t.update_quota(now, self)
    end
  end

  def get_portion(new_record = false)
    if(self.portion.nil?)
      self.portion = 0
    end

    if(self.portion > 0)
      return self.portion
    end

    now = Time.now
    nowDT = get_Datetime(now)
    days = get_days(self.last_update_portion, now)

    if(days > 0)
      self.portion = self.portion_start
      self.last_update_portion = nowDT
      if(!new_record)
        self.save
      end
    end
    return self.portion
  end

  def create
    self.completed_count = 0
    self.requested_count = 0
    self.cancelled_count = 0

    self.portion_start = count / days_to_complete

    get_portion(true)

    super

    for spot in WifiSpot.all do
      task = WifiTask.new
      task.wifi_spot_id = spot.id
      task.wifi_request_id = self.id
      task.save
    end
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
