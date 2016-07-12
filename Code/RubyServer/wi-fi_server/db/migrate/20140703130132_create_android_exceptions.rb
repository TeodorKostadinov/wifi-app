class CreateAndroidExceptions < ActiveRecord::Migration
  def change
    create_table :android_exceptions do |t|
      t.string :ip
      t.references :user
      t.datetime :time
      t.text :stacktrace
      t.string :class_invoker
      t.references :android_exception_type

      t.timestamps
    end
    add_index :android_exceptions, :user_id
    add_index :android_exceptions, :android_exception_type_id
  end
end
