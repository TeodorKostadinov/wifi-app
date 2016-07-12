ActiveAdmin.register Message do
 index do
    column "ID" do |p|
      "#{p.id}"
    end
    column "Description" do |p|
      "#{p.name}"
    end
    column "Created on" do |p|
      "#{p.created_at}"
    end
    column "Last updated" do |p|
      "#{p.updated_at}"
    end
    default_actions
  end
end
