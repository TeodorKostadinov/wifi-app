class CreateWifiRequests < ActiveRecord::Migration
  def change
    create_table :wifi_requests do |t|
    	t.string :keyword
    	t.string :website
      t.integer :lifespan
      t.timestamps
    end
  end
end
