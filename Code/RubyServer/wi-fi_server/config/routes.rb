WiFiServer::Application.routes.draw do
devise_for :users, :controller => {:session => 'session'}
devise_for :admin_users, ActiveAdmin::Devise.config
 ActiveAdmin.routes(self)

  devise_scope :user do
    post 'registration' => 'registration#create', :as => 'register'
    post 'session' => 'session#create', :as => 'login'
    post 'add_spot' => 'spots#add', :as => 'add_spot'
  
    post 'update_spot' => 'spots#change', :as => 'update_spot'
    post 'update_user' => 'registration#change', :as => 'update_user'
    post 'status_update' => 'status#status_update', :as => 'status_update'

    post 'take_task' => 'tasks#take', :as => 'take_task'
    post 'logout' => 'session#destroy', :as => 'logout'

    #post 'fail' => 'session#failure', :as => 'fail'
    post 'tasks' => 'tasks#get', :as => 'tasks'
    post 'spots' => 'spots#get', :as => 'spots'
    post 'all_spots' => 'spots#get_all', :as => 'all_spots'
    post 'statuses' => 'status#get', :as =>'statuses'

    post 'miss_all' => 'tasks#miss_all', :as =>'miss_all'

    post 'has_active' => 'tasks#has_active', :as =>'has_active'
    post 'exception' => 'session#exception', :as =>'exception'
  end
end
