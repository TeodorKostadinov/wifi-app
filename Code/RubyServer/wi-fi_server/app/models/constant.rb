class Constant < ActiveRecord::Base
  attr_accessible :name, :variables_string, :constant_values, :constant_values_ids, :constant_values_attributes

  has_many :constant_values
  accepts_nested_attributes_for :constant_values

  def variables
    return variables_string.split(',')
  end

  def values
    result = {}

    vals = ConstantValue.where(constant_id:self.id)
    if(vals.length == 0)
      puts "COULD NOT FIND VALUES FOR #{self.name}"
    else
      for v in vals do
        variable = v.variable
        val = v.value
        type = v.type
        parsed = type.parse(val)
        result[variable] = parsed
      end
    end 
    if(vals.length > variables.length)
        puts "TOO MANY VARIABLES FOUND\nEXPECTED #{variables.length}\nGOT #{vals.length}"
    elsif(vals.length < variables.length)
      puts "TOO FEW VARIABLES FOUND\nEXPECTED #{variables.length}\nGOT #{vals.length}"
    end

    return result
  end

  def self.quota_count
    name = "Quota Distribution"
    constant = Constant.where(name:name).first
    if(constant.nil?)
      int = integer

      constant = Constant.new
      constant.name = name
      constant.variables_string = "count"

      count = ConstantValue.new
      count.value = "1"
      count.variable = "count"
      count.constant_value_type = int

      constant.constant_values.push(count)
      constant.save

      constant = Constant.where(name:name).first
    end
    
    return constant
  end

  def self.integer
    name = "Integer"
    int = ConstantValueType.where(name:name).first
    if(int.nil?)
      int = ConstantValueType.new
      int.name = integer
      int.parse_code = "return value.to_i"
      int.save
      int = ConstantValueType.where(name:name).first
    end
    return int
  end

  def self.quota
    name = "Quota"
    constant = Constant.where(name:name).first
    if(constant.nil?)
      int = integer

      constant = Constant.new
      constant.name = name
      constant.variables_string = "daily, weekly, monthly"

      daily = ConstantValue.new
      daily.value = "1"
      daily.variable = "daily"
      daily.constant_value_type = int
      
      weekly = ConstantValue.new
      weekly.value = "3"
      weekly.variable = "weekly"
      weekly.constant_value_type = int

      monthly = ConstantValue.new
      monthly.value = "12"
      monthly.variable = "monthly"
      monthly.constant_value_type = int

      constant.constant_values.push(daily)
      constant.constant_values.push(weekly)
      constant.constant_values.push(monthly)
      constant.save
      constant = Constant.where(name:name).first
    end
    return constant
  end
end
