class CreateWifiTasks < ActiveRecord::Migration
  def change
    create_table :wifi_tasks do |t|
      t.integer :wifi_spot_id
      t.integer :wifi_request_id
      t.integer :qoute_daily, :default => -1
      t.integer :qoute_weekly, :default => -1
      t.integer :qoute_monthly, :default => -1
      t.timestamps
    end
  end
end
