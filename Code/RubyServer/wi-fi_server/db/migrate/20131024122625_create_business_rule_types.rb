class CreateBusinessRuleTypes < ActiveRecord::Migration
  def change
    create_table :business_rule_types do |t|
      t.string :name
      t.timestamps
    end
  end
end
