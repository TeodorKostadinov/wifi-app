class Language < ActiveRecord::Base
  attr_accessible :name

  def self.get(name)
  	l = Language.all.select{|x|x.name.downcase == name.downcase}
  	if(l.length > 0)
  		return l.first
  	end
  	return nil
  end

  def self.add(name)
  	m = get(name)
  	if(m == nil)
  		m = Language.new
  		m.name = name
  		m.save
  	end
  end
end
