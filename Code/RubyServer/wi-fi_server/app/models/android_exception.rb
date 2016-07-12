class AndroidException < ActiveRecord::Base
  belongs_to :user
  belongs_to :android_exception_type
  attr_accessible :android_exception_type, :class_invoker, :ip, :stacktrace, :time
end
