class CreateConstantValueTypes < ActiveRecord::Migration
  def change
    create_table :constant_value_types do |t|
      t.string :name
      t.text :parse_code
      t.timestamps
    end
  end
end
