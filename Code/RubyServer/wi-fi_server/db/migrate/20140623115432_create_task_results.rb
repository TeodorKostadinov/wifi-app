class CreateTaskResults < ActiveRecord::Migration
  def change
    create_table :task_results do |t|
      t.references :user
      t.references :wifi_status
      t.references :wifi_task
      t.datetime :finished_at

      t.timestamps
    end
    add_index :task_results, :wifi_status_id
    add_index :task_results, :user_id
    add_index :task_results, :wifi_task_id

    add_column :generated_tasks, :saved, :boolean, default: false
  end
end
