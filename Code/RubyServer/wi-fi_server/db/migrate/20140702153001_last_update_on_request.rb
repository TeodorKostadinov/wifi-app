class LastUpdateOnRequest < ActiveRecord::Migration
  def change
    remove_column :wifi_tasks, :last_update_day
    remove_column :wifi_tasks, :last_update_week
    remove_column :wifi_tasks, :last_update_month

    add_column :wifi_requests, :last_update_day, :datetime
    add_column :wifi_requests, :last_update_week, :datetime
    add_column :wifi_requests, :last_update_month, :datetime

    add_column :wifi_requests, :quota_daily_current, :integer
    add_column :wifi_requests, :quota_weekly_current, :integer
    add_column :wifi_requests, :quota_monthly_current, :integer
  end
end
