class CreateAudits < ActiveRecord::Migration
  def change
    create_table :audits do |t|
      t.integer :user_id
      t.string :ip
      t.string :controller
      t.string :action
      t.integer :result_id
      t.string :message
      t.timestamps
    end
  end
end
