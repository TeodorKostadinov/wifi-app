ActiveAdmin.register GeneratedTask do
  menu label:"Taken by users", parent:"Tasks", priority:1
  index do
    column :id
    column "User" do |p|
      "#{p.user.name}"
    end
    column "Task" do |p|
      "#{p.wifi_task.name}"
    end
    column "Status" do |p|
      "#{p.wifi_status.name}"
    end
    column :position_in_path
    column :saved
    column :created_at
    column :updated_at
    default_actions
  end
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
     row :updated_at
   end
 end
end