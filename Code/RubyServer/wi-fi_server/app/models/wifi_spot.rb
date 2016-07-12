class WifiSpot < ActiveRecord::Base
	has_many :wifi_tasks
	attr_accessible :name, :password, :ip, :address, :latitude, :longitude, :gmaps, :wifi_tasks_ids, :active

  acts_as_gmappable

  validates_presence_of :latitude, :longitude, :name, :ip, :gmaps, :address

  #validates_uniqueness_of :ip

  validates_numericality_of :latitude, :greater_than_or_equal_to => -180, :message => "The latitude must be more than -180"
  validates_numericality_of :longitude, :greater_than_or_equal_to => -180, :message => "The longitude must be more than -180"

  validates_numericality_of :latitude, :less_than_or_equal_to => 180, :message => "The latitude must be less than 180"
  validates_numericality_of :longitude, :less_than_or_equal_to => 180, :message => "The longitude must be less than 180"

  validate :validate_ip

  def validate_ip
    if(ip.blank?) 
      puts "BLANK IP ( already captured by pressense"
    else
      split = ip.split('.')
      if(split.length != 4)
        errors.add(:ip, "#{ip} is not a valid ip!")
      end
    end
  end

  def self.check_quota
    now = Time.now
    for r in WifiRequest.all do
      r.check_quota(now)
    end
  end
	
  # attr_accessible :title, :body

  def gmaps4rails_infowindow
      "
      Id:#{self.id}<br/>
      Name: #{self.name}<br/>
      IP: #{self.ip}<br/>
      Host of: #{self.wifi_tasks.length} tasks<br/>
      Geo-Location: #{self.longitude}:#{self.latitude}<br/>
      Address: #{self.address}<br/>
      Password: #{self.password}
      "
  end

  def gmaps4rails_title
      "Id: #{self.id}\nName: #{self.name}\nIP: #{self.ip}"
  end

  def gmaps4rails_address
    "#{self.latitude},#{self.longitude}" 
  end

  def gmaps_address
    gmaps4rails_address
  end

  def g_a
    gmaps_address
  end

  def ga
    g_a
  end

  def create
    self.gmaps = true

    super
    for request in WifiRequest.all do
      task = WifiTask.new
      task.wifi_spot_id = self.id
      task.wifi_request_id = request.id
      task.save
    end
    self.gmaps = true
  end
end
