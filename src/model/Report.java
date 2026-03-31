package model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.time.LocalDateTime;

// scope_id is a polymorphic reference — value depends on the scope field.
@DatabaseTable(tableName = "report")
public class Report {

    @DatabaseField(id = true, columnName = "report_id", canBeNull = false)
    private String reportId;

    @DatabaseField(canBeNull = false)
    private String title;

    @DatabaseField(canBeNull = false, dataType = DataType.ENUM_NAME)
    private ReportScope scope;

    @DatabaseField(columnName = "scope_id", canBeNull = false, indexName = "idx_report_scope")
    private String scopeId;

    @DatabaseField(foreign = true, columnName = "generated_by", canBeNull = false)
    private User generatedBy;

    @DatabaseField(columnName = "generated_at", canBeNull = false)
    private String generatedAt = LocalDateTime.now().toString();

    @DatabaseField(canBeNull = false)
    private String format = "json";

    @DatabaseField(columnName = "data_json")
    private String dataJson;

    public Report() {}
}
