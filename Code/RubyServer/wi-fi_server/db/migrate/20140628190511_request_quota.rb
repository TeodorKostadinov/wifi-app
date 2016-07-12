class RequestQuota < ActiveRecord::Migration
  def change
    rename_column :wifi_tasks, :qouta_daily_current, :quota_daily_current
    rename_column :wifi_tasks, :qouta_weekly_current, :quota_weekly_current
    rename_column :wifi_tasks, :qouta_monthly_current, :quota_monthly_current

    remove_column :wifi_tasks, :qouta_daily
    remove_column :wifi_tasks, :qouta_weekly
    remove_column :wifi_tasks, :qouta_monthly

    add_column :wifi_tasks, :last_update_day, :datetime
    add_column :wifi_tasks, :last_update_week, :datetime
    add_column :wifi_tasks, :last_update_month, :datetime

    add_column :wifi_requests, :quota_daily, :integer
    add_column :wifi_requests, :quota_weekly, :integer
    add_column :wifi_requests, :quota_monthly, :integer
  end
end
