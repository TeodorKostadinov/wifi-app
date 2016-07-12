class CreateConstantValues < ActiveRecord::Migration
  def change
    create_table :constant_values do |t|
      t.integer :constant_id
      t.string :variable
      t.string :value
      t.integer :constant_value_type_id

      t.timestamps
    end
  end
end
