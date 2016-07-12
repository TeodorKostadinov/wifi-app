class CreateGeneratedTasks < ActiveRecord::Migration
  def change
    create_table :generated_tasks do |t|
      t.integer :wifi_task_id
      t.integer :user_id
      t.integer :wifi_status_id
      t.integer :position_in_path
      t.timestamps

    end
    #change_column :generated_tasks, :wifi_tasks_id, :wifi_task_id
  end
end
