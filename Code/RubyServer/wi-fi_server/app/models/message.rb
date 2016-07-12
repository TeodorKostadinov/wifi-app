class Message < ActiveRecord::Base
	belongs_to :language
	belongs_to :message_type
  attr_accessible :language_id, :message_type_id, :text

  def name
    return "#{self.language.name}:#{self.message_type.name} -> #{self.text}"
  end

  def type
    return self.message_type
  end

  def self.add(language, type, text)
    m = get(language, type, [])
    if(m.text.downcase == "Message unhandled: #{language}:#{type}".downcase)
      m = Message.new
      m.message_type = MessageType.get(type)
      m.language = Language.get(language)
      m.text = text
      m.save
    end
  end

  def self.count_words(string)
    words = string.split(' ')
    frequency = Hash.new(0)
    words.each { |word| frequency[word.downcase] += 1 }
    return frequency
  end

  def self.get(language, type, arguments = [])
    language = language.to_s
    type = type.to_s
  	languages = Language.all.select{|x| x.name.downcase == language.downcase}
  	types = MessageType.all.select {|x| x.name.downcase == type.downcase}

  	if(languages.length > 0)
  		if(types.length > 0)
  			language = languages.first
  			type = types.first

  			messages = Message.where(message_type_id: type.id, language_id: language.id)
  			if(messages.length > 0)
          m = Message.new
          m.language = messages.first.language
          m.message_type = messages.first.type
          m.text = messages.first.text
          fr = count_words(m.text).select{|x| x.downcase.split("value").length > 1}
          
          for i in 0...arguments.length do
            m.text = m.text.gsub("value" + i.to_s, arguments[i].to_s)
          end

          return m
  			else
  				return en_ERROR("Message unhandled: #{language.name}:#{type.name}")
  			end
  		else
  			return en_ERROR("Message unknown: " + type)
  		end
  	else
  		return en_ERROR("Language unknown: " + language)
  	end
  end

  def self.en_ERROR(text)
		m = Message.new
		en = Language.all.select{|x| x.name.downcase == "en"}
		er = MessageType.all.select {|x| x.name.downcase == "message error"}

		if(en.length > 0)
			en = en.first
		else
			l = Language.new
			l.name = "EN"
			l.save
			en = l
		end

		if(er.length > 0)
			er = er.first
		else
			mt = MessageType.new
			mt.name = "Message Error"
			mt.save
			er = mt
		end
		m.language_id = en.id
		m.message_type_id = er.id
		m.text = text

    audit = Audit.new
    audit.user_id = 0
    audit.ip = 0
    audit.controller = "TranslateFail : #{m.language.name}:#{m.type.name}"
    audit.action = "TranslateFail : #{m.language.name}:#{m.type.name}"
    audit.result_id = AuditResult.error.id
    audit.message = m.text
    audit.save

		return m
  end
end