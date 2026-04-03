package model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.time.LocalDateTime;

@DatabaseTable(tableName = "enterprise")
public class Enterprise {

    @DatabaseField(id = true, columnName = "enterprise_id", canBeNull = false)
    private String enterpriseId;

    @DatabaseField(foreign = true, columnName = "network_id", canBeNull = false, indexName = "idx_enterprise_network")
    private Network network;

    @DatabaseField(canBeNull = false)
    private String name;

    @DatabaseField
    private String type;

    @DatabaseField(columnName = "created_at", canBeNull = false)
    private String createdAt = LocalDateTime.now().toString();

    public Enterprise() {}

    public Enterprise(String enterpriseId, Network network, String name, String type) {
        this.enterpriseId = enterpriseId;
        this.network = network;
        this.name = name;
        this.type = type;
    }

    public String getEnterpriseId() { return enterpriseId; }
}
