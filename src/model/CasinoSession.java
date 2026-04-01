package model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.time.LocalDateTime;

@DatabaseTable(tableName = "casino_session")
public class CasinoSession {

    @DatabaseField(id = true, columnName = "session_id", canBeNull = false)
    private String sessionId;

    @DatabaseField(foreign = true, columnName = "guest_id", canBeNull = false, indexName = "idx_casino_guest")
    private Guest guest;

    @DatabaseField(columnName = "started_at", canBeNull = false)
    private String startedAt = LocalDateTime.now().toString();

    @DatabaseField(columnName = "ended_at")
    private String endedAt;

    @DatabaseField(columnName = "credits_start", canBeNull = false)
    private int creditsStart;

    @DatabaseField(columnName = "credits_end")
    private Integer creditsEnd;

    public CasinoSession() {}
}
