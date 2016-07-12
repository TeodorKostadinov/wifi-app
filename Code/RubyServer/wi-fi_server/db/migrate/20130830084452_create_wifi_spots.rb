class CreateWifiSpots < ActiveRecord::Migration
  def change
    create_table :wifi_spots do |t|
    	t.string :name
      t.string :address
    	t.string :password
    	t.string :ip
      t.float :latitude
      t.float :longitude
      t.boolean :gmaps
      t.string :wifi_tasks
      t.boolean :active, :default => true
      t.timestamps
    end
  end
end
