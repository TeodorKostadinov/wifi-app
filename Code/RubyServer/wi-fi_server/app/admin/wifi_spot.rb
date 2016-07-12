ActiveAdmin.register WifiSpot do
  menu parent:"Tasks", label:"Spots"
  form do |f|
    f.inputs "Application Form" do                     
     f.input :name
     f.input :address
     f.input :password
     f.input :ip
     f.input :latitude
     f.input :longitude
     f.input :gmaps
     f.input :active
   end
    f.actions
  end
    form :partial => "form"

 show do
   attributes_table do
     row :id
     row :name
     row :address
     row :password
     row :ip
     row :latitude
     row :longitude
     row :gmaps
     row :active
     row :created_at
     row :updated_at
   end
   panel "TASKS" do
    table_for wifi_spot.wifi_tasks do
        column "" do |task|
          link_to "#{task.id} | ", "/admin/wifi_tasks/#{task.id}"
        end
      end
   end
   panel "MAP" do
    markers = wifi_spot.to_gmaps4rails
    puts markers
    div do
       render "map", { :markers => markers }
    end
  end
 end

  index do                            
    column :id                     
    column :name     
    column :ip   
    column :latitude           
    column :longitude
    markers = WifiSpot.all.to_gmaps4rails
    
    div do
       render "map", { :markers => markers }
    end             
    default_actions                   
  end 
end