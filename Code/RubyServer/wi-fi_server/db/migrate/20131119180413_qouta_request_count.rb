class QoutaRequestCount < ActiveRecord::Migration
  def self.up
    add_column :wifi_requests, :completed_count, :integer
    add_column :wifi_requests, :requested_count, :integer
    add_column :wifi_requests, :cancelled_count, :integer
    rename_column :wifi_requests, :lifespan, :count

    rename_column :wifi_tasks, :qoute_daily, :qouta_daily
    rename_column :wifi_tasks, :qoute_weekly, :qouta_weekly
    rename_column :wifi_tasks, :qoute_monthly, :qouta_monthly
    add_column :wifi_tasks, :qouta_daily_current, :integer
    add_column :wifi_tasks, :qouta_weekly_current, :integer
    add_column :wifi_tasks, :qouta_monthly_current, :integer
  end 
  def self.down
  end
end
