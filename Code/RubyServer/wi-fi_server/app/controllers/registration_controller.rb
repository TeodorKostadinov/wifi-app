class RegistrationController < Devise::RegistrationsController
	skip_before_filter :verify_authenticity_token,
                     :if => Proc.new { |c| c.request.format == 'application/json' }

  respond_to :json

  def create
    language = params[:language]
    audit = Audit.new
    audit.ip = params[:ip]
    audit.user_id = 0
    audit.controller = "Registration"
    audit.action = "Create"
    audit.transaction do
      build_resource(params[:user]) 
      resource.skip_confirmation!
      
      if resource.save
        sign_in resource
        audit.user_id = current_user.id
        audit.result_id = AuditResult.success.id
        audit.save
        render :status => 200,
             :json => { :success => true,
                        :info => Message.get(language, "Register").text,
                        :data => { :user => resource,
                                   :auth_token => current_user.authentication_token } }

      else
        lines = 0
        errors = ""
        for e in resource.errors do
          error = ""
          for er in resource.errors[e] do
            error += er
          end

          errors += "#{e.capitalize} #{error}."
          errors += "\n" unless(lines + 1 == resource.errors.keys.length)

          lines += 1
        end

          render :json => { :success => false,
                           :info => errors,# TODO -------------------------------------------
                          :data => {} }
        audit.message ="Unable to create user.\n#{errors}"
        audit.result_id = AuditResult.error.id
        audit.save
      end
    end
  end

  def change
    language = params[:language]
    audit = Audit.new
    audit.ip = params[:ip]
    audit.user_id = current_user.id
    audit.controller = "Registration"
    audit.action ="Change"

    audit.transaction do

      #TODO : validate email
      data = params[:user]

      oldPassword = data[:oldPassword]
      newPassword = data[:newPassword]
      newPasswordConfirmation = data[:passwordConfirmation]

      if(newPassword != newPasswordConfirmation)
        audit.message = "Passwords do not match"
        audit.result_id = AuditResult.error.id
        audit.save
        render :json => {
                          :success => false,
                          :info => Message.get(language, "ChangeInvalidPassword").text,
                          :data => {
                                   :auth_token => current_user.authentication_token  }
                        }
      
      else
        newMail = data[:email]
        newUsername = data[:username]

        user = current_user
        users = User.where(:email => newMail)
        emailIsInUse = false
        if(users.length > 0)
          if(users[0].id != user.id)
            puts users[0].id 
            puts user.id
            puts users[0].id != user.id
             emailIsInUse = true;
          end
        end
        if(emailIsInUse)
            render :json =>  {

                          :success => false,
                          :info => Message.get(language, "EmailInUse").text,
                         :data => {
                                   :auth_token => current_user.authentication_token  }
                        }
        elsif( !user.valid_password?(params[:user][:oldPassword]))
           audit.message = "User did not enter user's current password correctly."
           audit.result_id = AuditResult.error.id
           audit.save
           render :json => {
                          :success => false,
                          :info => Message.get(language, "ChangeIncorrectPassword").text,
                         :data => {
                                   :auth_token => current_user.authentication_token  }
                        }
           
        else
          user.password = newPassword
          user.password_confirmation = newPasswordConfirmation
          user.name = newUsername
          user.email = newMail

          userData = {}
          userData[:name => newUsername, :email => newMail,
           :password => newPassword, :password_confirmation =>
            newPasswordConfirmation]
          current_user.update_attributes(userData)
          audit.result_id = AuditResult.success.id
          audit.save
          render :json => {
                            :success => true,
                            :info => Message.get(language, "ChangeOK").text,
                            :data => {
                                   :auth_token => current_user.authentication_token,
                                   :name => current_user.name  }
                          }
         
        end
      end
    end
  end
end
