package model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.time.LocalDateTime;

// Audit trail for work request status transitions.
// Rollback records are appended with isRollback = true — prior records are
// never deleted, preserving the full history.
@DatabaseTable(tableName = "status_change")
public class StatusChange {

    @DatabaseField(id = true, columnName = "status_change_id", canBeNull = false)
    private String statusChangeId;

    @DatabaseField(foreign = true, columnName = "request_id", canBeNull = false, indexName = "idx_sc_request")
    private WorkRequest request;

    @DatabaseField(columnName = "previous_status", canBeNull = false)
    private String previousStatus;

    @DatabaseField(columnName = "new_status", canBeNull = false)
    private String newStatus;

    @DatabaseField(foreign = true, columnName = "changed_by_id", canBeNull = false, indexName = "idx_sc_changed_by")
    private User changedBy;

    @DatabaseField(columnName = "changed_at", canBeNull = false)
    private String changedAt = LocalDateTime.now().toString();

    @DatabaseField
    private String reason;

    @DatabaseField(columnName = "is_rollback", canBeNull = false)
    private boolean isRollback;

    public StatusChange() {}
}
