class CreateBusinessRules < ActiveRecord::Migration
  def change
    create_table :business_rules do |t|
      t.string :description
      t.text :code
      t.integer :business_rule_type_id
      t.timestamps
    end
  end
end
