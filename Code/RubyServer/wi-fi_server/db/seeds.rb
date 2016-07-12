# This file should contain all the record creation needed to seed the database with its default values.
# The data can then be loaded with the rake db:seed (or created alongside the db with db:setup).
#
# Examples:
#
#   cities = City.create([{ name: 'Chicago' }, { name: 'Copenhagen' }])
#   Mayor.create(name: 'Emanuel', city: cities.first)
WifiStatus.create!(:name => "Status?", :completes_task => false, :cancels_task => false)
WifiStatus.create!(:name => "Missed", :completes_task => false, :cancels_task => true)
WifiStatus.create!(:name => "Finished", :completes_task => true, :cancels_task => false)
AdminUser.create!(:email => 'admin@example.com', :password => 'password', :password_confirmation => 'password') if AdminUser.all.length == 0
	
Language.add("en")
Language.add("bg")

MessageType.add("TokenExpired")
Message.add("en", "TokenExpired", "Token has expired")
Message.add("bg", "TokenExpired", "------------------")

MessageType.add("CancelOrComplete")
Message.add("en", "CancelOrComplete", "Please Complete or Cancel your unfinished tasks!\n(value0 remaining)")
Message.add("bg", "CancelOrComplete", "--------------")

MessageType.add("TasksTake")
Message.add("en", "TasksTake", "Tasks taken successfully!")
Message.add("bg", "TasksTake", "--------------")

MessageType.add("TasksNone")
Message.add("en", "TasksNone", "No tasks available!")
Message.add("bg", "TasksNone", "--------------")

MessageType.add("TasksGet")
Message.add("en", "TasksGet", "Tasks received!")
Message.add("bg", "TasksGet", "------------")

MessageType.add("StatusGet")
Message.add("en", "StatusGet", "Statuses received!")
Message.add("bg", "StatusGet", "------------")

MessageType.add("StatusUpdate")
Message.add("en", "StatusUpdate", "Status updated!")
Message.add("bg", "StatusUpdate", "------------")

MessageType.add("TaskAlreadyCompleted")
Message.add("en", "TaskAlreadyCompleted", "Task is already completed when it was set to:value0")
Message.add("bg", "TaskAlreadyCompleted", "------------")

MessageType.add("MissMatchIPForUpdate")
Message.add("en", "MissMatchForIPForUpdate", "value0 does not match wifi spot's ip!:{value1}")
Message.add("bg", "MissMatchForIPForUpdate", "------------")

MessageType.add("TaskNotFoundForUser")
Message.add("en", "TaskNotFoundForUser", "value0 for task in value1{value2} not found.")
Message.add("bg", "TaskNotFoundForUser", "------------")

MessageType.add("InvalidStatus")
Message.add("en", "InvalidStatus", "value0 not found as a status value! {value1}")
Message.add("bg", "InvalidStatus", "------------")

MessageType.add("SpotAdd")
Message.add("en", "SpotAdd", "Added new spot!")
Message.add("bg", "SpotAdd", "------------")

MessageType.add("SpotGet")
Message.add("en", "SpotGet", "Spot received!")
Message.add("bg", "SpotGet", "------------")

MessageType.add("SpotGetAll")
Message.add("en", "SpotGetAll", "Spots received!")
Message.add("bg", "SpotGetAll", "------------")

MessageType.add("SpotChangeNotFound")
Message.add("en", "SpotChangeNotFound", "The record was not found in the database. {value0}")
Message.add("bg", "SpotChangeNotFound", "------------")

MessageType.add("SpotChange")
Message.add("en", "SpotChange", "Wifi spot updated!")
Message.add("bg", "SpotChange", "------------")

MessageType.add("LogIn")
Message.add("en", "LogIn", "Logged in!")
Message.add("bg", "LogIn", "------------")

MessageType.add("LogOut")
Message.add("en", "LogOut", "Logged out!")
Message.add("bg", "LogOut", "------------")

MessageType.add("InvalidLogin")
Message.add("en", "InvalidLogin", "Email and/or password are invalid. Retry!")
Message.add("bg", "InvalidLogin", "------------")

MessageType.add("TasksGet")
Message.add("en", "TasksGet", "Logged out")
Message.add("bg", "TasksGet", "------------")

MessageType.add("Register")
Message.add("en", "Register", "Registered")
Message.add("bg", "Register", "------------")

MessageType.add("ChangeInvalidPassword")
Message.add("en", "ChangeInvalidPassword", "Passwords do not match!")
Message.add("bg", "ChangeInvalidPassword", "------------")

MessageType.add("EmailInUse")
Message.add("en", "EmailInUse", "Email is in use!")
Message.add("bg", "EmailInUse", "------------")

MessageType.add("ChangeIncorrectPassword")
Message.add("en", "ChangeIncorrectPassword", "You did not enter your current password correctly.")
Message.add("bg", "ChangeIncorrectPassword", "------------")

MessageType.add("ChangeOK")
Message.add("en", "ChangeOK", "Your credentials have been updated!")
Message.add("bg", "ChangeOK", "------------")

MessageType.add("MissedAll")
Message.add("en", "MissedAll", "Successfully changed all statuses.")
Message.add("bg", "MissedAll", "------------")