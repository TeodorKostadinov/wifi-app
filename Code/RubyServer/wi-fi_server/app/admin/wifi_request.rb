ActiveAdmin.register WifiRequest do
  menu parent:"Tasks", label:"Requests"

  form do |f|      
    f.inputs "Request details" do
      if(f.object.new_record?)
        constant = Constant.quota
        daily = constant.values["daily"]
        weekly = constant.values["weekly"]
        monthly = constant.values["monthly"]
      else
        daily = f.object.quota_daily
        weekly = f.object.quota_weekly
        monthly = f.object.quota_monthly
      end

      f.input :keyword
      f.input :website 
      f.input :count
      f.input :days_to_complete

      f.input :quota_daily, :input_html => { :value => daily }
      f.input :quota_weekly, :input_html => { :value => weekly }
      f.input :quota_monthly, :input_html => { :value => monthly }
    end
    f.actions
  end
end
