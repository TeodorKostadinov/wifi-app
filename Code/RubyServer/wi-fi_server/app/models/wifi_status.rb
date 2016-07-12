class WifiStatus < ActiveRecord::Base
  attr_accessible :name, :completes_task, :cancels_task

  validate :boolean_fields

  def boolean_fields
    if(completes_task && cancels_task)
      errors.add(:completes_task, "You cannot have it complete AND cancel!!")
    end
  end

  validates_presence_of :name
end
