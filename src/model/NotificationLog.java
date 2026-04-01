package model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.time.LocalDateTime;

@DatabaseTable(tableName = "notification_log")
public class NotificationLog {

    @DatabaseField(id = true, columnName = "log_id", canBeNull = false)
    private String logId;

    @DatabaseField(canBeNull = false)
    private String recipient;

    @DatabaseField(canBeNull = false, dataType = DataType.ENUM_NAME)
    private NotificationChannel channel;

    @DatabaseField
    private String subject;

    @DatabaseField
    private String body;

    @DatabaseField(canBeNull = false, dataType = DataType.ENUM_NAME)
    private NotificationStatus status = NotificationStatus.sent;

    @DatabaseField(columnName = "sent_at", canBeNull = false)
    private String sentAt = LocalDateTime.now().toString();

    @DatabaseField(columnName = "related_id", indexName = "idx_notif_related")
    private String relatedId;

    public NotificationLog() {}
}
