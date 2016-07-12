class CreateMessages < ActiveRecord::Migration
  def change
    create_table :messages do |t|
    	t.integer :language_id
    	t.integer :message_type_id
    	t.string :text
      t.timestamps
    end
  end
end
