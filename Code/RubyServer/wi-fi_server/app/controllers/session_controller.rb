class SessionController < Devise::SessionsController

	 skip_before_filter :verify_authenticity_token,
                     :if => Proc.new { |c| c.request.format == 'application/json' }

  respond_to :json

  def create
    language = params[:language]
    audit = Audit.new
    audit.ip = params[:ip]
    audit.controller = "Session"
    audit.action ="Create"
    audit.user_id = 0
    audit.result_id = AuditResult.pending.id
    audit.save
    audit.transaction do
      warden.authenticate!(:scope => resource_name, :recall => :failure)
      audit.user_id = current_user.id
      audit.result_id = AuditResult.success.id
      audit.save
      render :status => 200,
             :json => { :success => true,
                        :info => Message.get(language, "LogIn").text,
                        :data => { :auth_token => current_user.authentication_token,
                                   :name => current_user.name,
                                   :level => current_user.level } 
                                 }
                                 #comment
   end
  end
  
  def destroy
    language = params[:language]
    if(current_user == nil)
      audit = Audit.new
      audit.ip = params[:ip]
      audit.controller = "Session"
      audit.action ="Destroy"
      audit.user_id = 0
      audit.result_id = AuditResult.error.id
      audit.message = "Unable to logout. The token has expired."
      audit.save

      render :status => 200,
           :json => { :success => false,
                      :info => Message.get(language, "TokenExpired").text,
                      :data => {} }
      return
    end
    audit = Audit.new
    audit.ip = params[:ip]
    audit.controller = "Session"
    audit.action ="Destroy"
    audit.user_id = current_user.id
    audit.result_id = AuditResult.pending.id
    
    audit.transaction do
      warden.authenticate!(:scope => resource_name, :recall => :failure)

      audit.result_id = AuditResult.success.id
      audit.save
      current_user.update_column(:authentication_token, nil)
      render :status => 200,
             :json => { :success => true,
                        :info => Message.get(language, "LogOut").text,
                        :data => {} }
    end
    audit.save
  end

  def failure
    language = params[:language]
    render :json => {:success => false, :errors => ["Login Failed"]}

    last = Audit.last
    if(last.controller == "Session" && last.result_id == AuditResult.pending.id)
      last.result_id = AuditResult.error.id
    end
    last.message ="#{last.action} failed"
    last.save
    render :status => 401,
           :json => { :success => false,
                      :info => Message.get(language, "InvalidLogin").text,
                      :data => {} }
  end

  def exception
    language = params[:language]
    if(current_user == nil)
      audit = Audit.new
      audit.ip = params[:ip]
      audit.controller = "Session"
      audit.action ="Exception"
      audit.user_id = 0
      audit.result_id = AuditResult.error.id
      audit.message = "Unable to logout. The token has expired."
      audit.save

      render :status => 200,
           :json => { :success => false,
                      :info => Message.get(language, "TokenExpired").text,
                      :data => {} }
      return
    end

    for error in params[:errors].keys do
      register_new_error(params[:ip], params[:errors][error], current_user)
    end

    audit = Audit.new
    audit.ip = params[:ip]
    audit.controller = "Session"
    audit.action ="Exception"
    audit.user_id = current_user.id
    audit.result_id = AuditResult.success.id
    audit.message = "#{params[:errors].length} errors found"
    
    render :json => { :success => true,
                      :info => "",
                      :data => {} }
    audit.save
  end

  private
  def register_new_error(ip, error, current_user)
    time = error[:time]
    stack = error[:stack]
    class_invoker = error[:class]
    type = AndroidExceptionType.where(name:error[:type]).first
    if(type.nil?)
      type = AndroidExceptionType.create(name:error[:type])
    end

    exc = AndroidException.new()
    exc.ip = ip
    exc.user_id = current_user.id
    exc.time = DateTime.new(time[:year].to_i, time[:month].to_i, time[:day].to_i, time[:hour].to_i, time[:minute].to_i, time[:second].to_i)
    exc.stacktrace = stack
    exc.class_invoker = class_invoker
    exc.android_exception_type = type
    exc.save
  end
end

class AuditControl < Struct.new(:audit_id)
  def perform
    audit = Audit.find(audit_id)
    if(audit.result_id == AuditResult.pending.id)
      audit.result_id = AuditResult.error.id
      audit.save
    end
  end
end