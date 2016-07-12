class AuditControl < Struct.new(:audit_id)
  def perform
    audit = Audit.find(audit_id)
    if(audit.result_id == AuditResult.pending.id)
      audit.result_id = AuditResult.error.id
      audit.save
    end
  end
end