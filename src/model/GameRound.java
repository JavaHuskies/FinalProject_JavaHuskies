package model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.time.LocalDateTime;

// Valid outcome values defined in GameOutcome enum.
@DatabaseTable(tableName = "game_round")
public class GameRound {

    @DatabaseField(id = true, columnName = "round_id", canBeNull = false)
    private String roundId;

    @DatabaseField(foreign = true, columnName = "session_id", canBeNull = false, indexName = "idx_game_session")
    private CasinoSession session;

    @DatabaseField(columnName = "game_type", canBeNull = false)
    private String gameType;

    @DatabaseField(canBeNull = false)
    private int wager;

    @DatabaseField(canBeNull = false, dataType = DataType.ENUM_NAME)
    private GameOutcome outcome;

    @DatabaseField(canBeNull = false)
    private int payout;

    @DatabaseField(columnName = "played_at", canBeNull = false)
    private String playedAt = LocalDateTime.now().toString();

    public GameRound() {}
}
