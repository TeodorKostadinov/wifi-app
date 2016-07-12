class BusinessRuleType < ActiveRecord::Base
  attr_accessible :name

  validates_uniqueness_of :name

  def self.select_spot
    check
    return BusinessRuleType.where(:name => "Wifi Spot Select")[0]
   end

  def self.check
    if(BusinessRuleType.where(:name => "Wifi Spot Select").length == 0)
      a = BusinessRuleType.new
      a.name = "Wifi Spot Select"
      a.save
    end
  end
end
