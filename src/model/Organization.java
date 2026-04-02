package model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.time.LocalDateTime;

@DatabaseTable(tableName = "organization")
public class Organization {

    @DatabaseField(id = true, columnName = "org_id", canBeNull = false)
    private String orgId;

    @DatabaseField(foreign = true, columnName = "enterprise_id", canBeNull = false, indexName = "idx_org_enterprise")
    private Enterprise enterprise;

    @DatabaseField(canBeNull = false)
    private String name;

    @DatabaseField
    private String type;

    @DatabaseField(columnName = "created_at", canBeNull = false)
    private String createdAt = LocalDateTime.now().toString();

    public Organization() {}

    public Organization(String orgId, Enterprise enterprise, String name, String type) {
        this.orgId = orgId;
        this.enterprise = enterprise;
        this.name = name;
        this.type = type;
    }
}
