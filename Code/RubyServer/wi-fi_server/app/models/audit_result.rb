class AuditResult < ActiveRecord::Base
   attr_accessible :name

   validates_uniqueness_of :name

   def self.success
    check
    return AuditResult.where(:name => "Success")[0]
   end
   def self.error
    check
    return AuditResult.where(:name => "Error")[0]
   end
   def self.pending
    check
    return AuditResult.where(:name => "Pending")[0]
  end

  def self.check
    if(AuditResult.where(:name => "Success").length == 0)
      a = AuditResult.new
      a.name = "Success"
      a.save
    end

    if(AuditResult.where(:name => "Error").length == 0)
      a = AuditResult.new
      a.name = "Error"
      a.save
    end

    if(AuditResult.where(:name => "Pending").length == 0)
      a = AuditResult.new
      a.name = "Pending"
      a.save
    end
  end
end
