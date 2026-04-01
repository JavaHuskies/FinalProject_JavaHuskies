package model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.time.LocalDateTime;

@DatabaseTable(tableName = "work_request")
public class WorkRequest {

    @DatabaseField(id = true, columnName = "request_id", canBeNull = false)
    private String requestId;

    @DatabaseField(canBeNull = false)
    private String title;

    @DatabaseField
    private String description;

    @DatabaseField(columnName = "request_type", canBeNull = false, dataType = DataType.ENUM_NAME)
    private WorkRequestType requestType;

    @DatabaseField(canBeNull = false, dataType = DataType.ENUM_NAME)
    private WorkRequestStatus status = WorkRequestStatus.draft;

    @DatabaseField(foreign = true, columnName = "origin_org_id", canBeNull = false, indexName = "idx_wr_origin")
    private Organization originOrg;

    @DatabaseField(foreign = true, columnName = "target_org_id", canBeNull = false, indexName = "idx_wr_target")
    private Organization targetOrg;

    @DatabaseField(foreign = true, columnName = "submitted_by", canBeNull = false, indexName = "idx_wr_submitted_by")
    private User submittedBy;

    @DatabaseField(foreign = true, columnName = "assigned_to")
    private User assignedTo;

    @DatabaseField(columnName = "created_at", canBeNull = false)
    private String createdAt = LocalDateTime.now().toString();

    @DatabaseField(columnName = "updated_at", canBeNull = false)
    private String updatedAt = LocalDateTime.now().toString();

    public WorkRequest() {}
}
