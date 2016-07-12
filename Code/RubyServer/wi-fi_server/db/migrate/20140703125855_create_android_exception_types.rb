class CreateAndroidExceptionTypes < ActiveRecord::Migration
  def change
    create_table :android_exception_types do |t|
      t.string :name

      t.timestamps
    end
  end
end
