class ConstantValueType < ActiveRecord::Base
  attr_accessible :name, :parse_code

  def parse(value)#CODE NEEDS 'VALUE'
    result = nil
    begin
      result = eval(parse_code)
    rescue Exception => e
      puts "ERROR WHILE PARSING"
      puts e
    end
    return result
  end
end
