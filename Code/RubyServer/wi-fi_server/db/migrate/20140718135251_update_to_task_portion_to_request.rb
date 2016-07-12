class UpdateToTaskPortionToRequest < ActiveRecord::Migration
  def change
    remove_column :wifi_requests, :last_update_day
    remove_column :wifi_requests, :last_update_week
    remove_column :wifi_requests, :last_update_month

    add_column :wifi_tasks, :last_update_day, :datetime
    add_column :wifi_tasks, :last_update_week, :datetime
    add_column :wifi_tasks, :last_update_month, :datetime

    remove_column :wifi_requests, :quota_daily_current
    remove_column :wifi_requests, :quota_weekly_current
    remove_column :wifi_requests, :quota_monthly_current

    add_column :wifi_requests, :days_to_complete, :integer
    add_column :wifi_requests, :portion, :integer
    add_column :wifi_requests, :portion_start, :integer
    add_column :wifi_requests, :last_update_portion, :datetime
  end
end
