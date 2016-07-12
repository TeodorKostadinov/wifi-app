class Audit < ActiveRecord::Base
  attr_accessible :user_id, :ip, :controller, :action, :result_id, :message

  validates_presence_of :user_id, :ip, :controller, :action, :result_id
end
