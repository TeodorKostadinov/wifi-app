class CreateWifiStatuses < ActiveRecord::Migration
  def change
    create_table :wifi_statuses do |t|
   	  t.string :name
      t.boolean :completes_task, :default => false
      t.boolean :cancels_task, :default => false
      t.timestamps
    end
  end
end
