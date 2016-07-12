class User < ActiveRecord::Base
	has_many :generated_tasks
  has_many :audits
  before_save :set_level
  after_save :set_level
  # Include default devise modules. Others available are:
  # :token_authenticatable, :confirmable,
  # :lockable, :timeoutable and :omniauthable
devise :database_authenticatable, :registerable,
       :recoverable, :rememberable, :trackable, :validatable,
       :confirmable, :token_authenticatable
       
 before_save :ensure_authentication_token
 attr_accessible :name, :email, :password, :password_confirmation, :remember_me, :level
 
  def skip_confirmation!
    self.confirmed_at = Time.now
  end

  def take_tasks(tasks, max_count)
    #Double Checking ( 1# == controller)
    completed_and_cancels_sum = 0
    for gr in self.generated_tasks do
      if(gr.wifi_status.completes_task || gr.wifi_status.cancels_task)
        completed_and_cancels_sum +=1
      end
    end
    if(completed_and_cancels_sum != self.generated_tasks.length)
      return
    end

    for g in self.generated_tasks do
      g.s
    end

    GeneratedTask.destroy_all(:user_id => id)
    self.generated_tasks = []
    for task in tasks do
      if (self.generated_tasks.length >= max_count)
        break
      end
      take_task(task)
    end
  end

  def take_task(task)
  #  if(!task.has_free_quota)
  #    return
  #  end
  #  if(!task.wifi_spot.active)
  #    return
  #  end
  #  if(task.wifi_request.count <= 0)
  #    return
  #  end
    ti = task.id
    is_taken = GeneratedTask.where(user_id:id).where(wifi_task_id:ti).length > 0

    user_task = GeneratedTask.where(user_id:id)
    wifi_spot_is_in_route = false
    for t in user_task do
      if(t.wifi_task.wifi_spot_id == task.wifi_spot_id)
        wifi_spot_is_in_route = true
        break
      end
    end

    if(!is_taken && !wifi_spot_is_in_route)
      generated_task = GeneratedTask.new
      generated_task.user_id = id
      generated_task.wifi_task_id = task.id
      uncheck = WifiStatus.select{|x| x.completes_task == false && x.cancels_task == false}.first
      generated_task.wifi_status_id = uncheck.id
      generated_task.position_in_path = self.generated_tasks.length
      generated_task.save

      generated_task.request

      self.generated_tasks.push(generated_task)
    end
  end
  private
 def set_level
  self.level = 0 if self.level.nil?
  if !self.level.nil? 
    self.level = 0 if (self.level.class.to_s != "Integer" && self.level.class.to_s != "Fixnum")
  end
 end
end
