class BusinessRule < ActiveRecord::Base
  belongs_to :business_rule_type

  attr_accessible :description, :code, :business_rule_type_id

  validates_presence_of :code, :business_rule_type_id

  def name
    return "#{self.business_rule_type.name} => #{self.description}"
  end

  def is_met_for(values)
    result = false
    begin
      result = eval(self.code)
      if(result != true && result != false)
        puts "Result is not boolean"
        puts "{Values}"
        puts values
        puts ""
        puts "{Code}"
        puts self.code
        puts "{end}"
        puts ""
        return false
      end
    rescue Exception => e
      puts "#{self.class}.is_met_for(#{values.class}) ... Rescued : #{e}"
      puts "{Values}"
      puts values
      puts ""
      puts "{Code}"
      puts self.code
      puts "{end}"
      puts ""
    end
    return result
  end

  def self.for_selecting_spots(spots, values)
    allowed_spots = []
    rule = BusinessRuleType.select_spot

    if(values.class != "Hash")
      values = {}
    end

    rules = BusinessRule.where(business_rule_type_id:rule.id)
    for spot in spots do
      values[:spot] = spot
      criteria_is_met = true
      for rule in rules do
        if(!rule.is_met_for(values))
          criteria_is_met = false
          break
        end
      end
      if(criteria_is_met)
        allowed_spots.push(spot)
      end
    end

    return allowed_spots
  end
end