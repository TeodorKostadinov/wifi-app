class TaskResult < ActiveRecord::Base
  belongs_to :user
  belongs_to :wifi_task
  belongs_to :wifi_status
  attr_accessible :finished_at

  def self.c(g)
    create_from_generated(g)
  end

  def self.create_from_generated(generated)
    if(generated.saved)
      return
    end
    result = TaskResult.new

    result.user = generated.user
    result.wifi_status = generated.wifi_status
    result.wifi_task = generated.wifi_task
    result.finished_at = generated.updated_at
    result.created_at = generated.created_at

    result.save
    generated.saved = true
    generated.save
  end
end
