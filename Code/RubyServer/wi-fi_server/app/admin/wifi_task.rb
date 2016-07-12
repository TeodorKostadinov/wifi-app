ActiveAdmin.register WifiTask do
  menu :label => "Statistics", parent:"Tasks"

  show do
   attributes_table do
       row :id
       row :wifi_spot_id do
        link_to "#{wifi_task.wifi_spot.name} :: #{wifi_task.wifi_spot.g_a}", "/admin/wifi_spots/#{wifi_task.wifi_spot_id}"
       end
       row :wifi_request_id do
        link_to "#{wifi_task.wifi_request.id}", "/admin/wifi_request/#{wifi_task.wifi_request_id}"
       end
       row :quota_daily_current
       row :quota_weekly_current
       row :quota_monthly_current
       row :created_at
       row :updated_at
    end
  end

  index do
   column :id
   column :wifi_spot
   column :wifi_request
   column :quota_daily_current
   column :quota_weekly_current
   column :quota_monthly_current
   default_actions 
  end

  form do |f|                  
    f.inputs "Task Statistics details" do       
      f.input :wifi_spot                  
      f.input :wifi_request
    end
    f.actions                         
  end   
end
