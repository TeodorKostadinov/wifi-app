class CreateAuditResults < ActiveRecord::Migration
  def change
    create_table :audit_results do |t|
      t.string :name
      t.timestamps
    end
  end
end
