ActiveAdmin.register TaskResult do
  menu label:"Results", parent:"Tasks", priority:1

 show do
   attributes_table do
     row :id
     row :wifi_task_id do
      link_to "#{generated_task.wifi_task_id}", "/admin/wifi_tasks/#{generated_task.wifi_task_id}"
     end
     row :user_id do
      link_to "#{generated_task.user.name}", "/admin/users/#{generated_task.user_id}"
     end
     row :status do
      link_to "#{generated_task.wifi_status.name}", "/admin/wifi_statuses/#{generated_task.wifi_status.id}"
     end
     row :created_at
     row :finished_at
   end
 end
end