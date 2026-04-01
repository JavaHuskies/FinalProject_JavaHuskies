package model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.time.LocalDateTime;

// Valid status values defined in ComplaintStatus enum.
@DatabaseTable(tableName = "complaint")
public class Complaint {

    @DatabaseField(id = true, columnName = "complaint_id", canBeNull = false)
    private String complaintId;

    @DatabaseField(foreign = true, columnName = "guest_id", canBeNull = false, indexName = "idx_complaint_guest")
    private Guest guest;

    @DatabaseField(foreign = true, columnName = "org_id", canBeNull = false)
    private Organization org;

    @DatabaseField
    private String subject;

    @DatabaseField
    private String body;

    @DatabaseField(canBeNull = false, dataType = DataType.ENUM_NAME)
    private ComplaintStatus status = ComplaintStatus.open;

    @DatabaseField(columnName = "created_at", canBeNull = false)
    private String createdAt = LocalDateTime.now().toString();

    public Complaint() {}
}
