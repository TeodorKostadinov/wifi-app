ActiveAdmin.register Constant do
 form do |f|
    f.inputs "Constant details" do
      f.input :name
      f.input :variables_string
      f.has_many :constant_values do |n|
        n.input :constant_value_type
        n.input :variable
        n.input :value
      end
    end
    f.actions
   end
end
