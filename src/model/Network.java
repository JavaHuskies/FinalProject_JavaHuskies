package model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.time.LocalDateTime;

@DatabaseTable(tableName = "network")
public class Network {

    @DatabaseField(id = true, columnName = "network_id", canBeNull = false)
    private String networkId;

    @DatabaseField(canBeNull = false)
    private String name;

    @DatabaseField
    private String headquarters;

    @DatabaseField
    private String founded;

    @DatabaseField(columnName = "created_at", canBeNull = false)
    private String createdAt = LocalDateTime.now().toString();

    public Network() {}

    public Network(String networkId, String name, String headquarters, String founded) {
        this.networkId = networkId;
        this.name = name;
        this.headquarters = headquarters;
        this.founded = founded;
    }
}
