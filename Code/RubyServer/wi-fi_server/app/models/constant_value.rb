class ConstantValue < ActiveRecord::Base
  belongs_to :constant
  belongs_to :constant_value_type
  attr_accessible :constant_id, :constant_value_type_id, :value, :variable

  #CHECK variable is set

  def type
    return constant_value_type
  end
end
