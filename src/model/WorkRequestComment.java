package model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "work_request_comment")
public class WorkRequestComment {

    @DatabaseField(id = true, columnName = "comment_id", canBeNull = false)
    private String commentId;

    @DatabaseField(foreign = true, columnName = "request_id", canBeNull = false, indexName = "idx_wrc_request")
    private WorkRequest request;

    @DatabaseField(foreign = true, columnName = "author_id", canBeNull = false)
    private User author;

    @DatabaseField(canBeNull = false)
    private String body;

    @DatabaseField(columnName = "created_at", canBeNull = false)
    private String createdAt;

    public WorkRequestComment() {}
}
