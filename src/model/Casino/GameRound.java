package model.Casino;

import java.time.LocalDateTime;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import model.GameOutcome;

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

    private int roundNumber;
    private RouletteRules.Bet bet;
    private int spinResult;

    public GameRound() {}

    public GameRound(int roundNumber, RouletteRules.Bet bet, int spinResult, int payout) {
        this.roundNumber = roundNumber;
        this.bet = bet;
        this.spinResult = spinResult;
        this.payout = payout;
    }

    public int getRoundNumber() { return roundNumber; }
    public RouletteRules.Bet getBet() { return bet; }
    public int getSpinResult() { return spinResult; }
    public int getPayout() { return payout; }
    public LocalDateTime getTimestamp() { return LocalDateTime.parse(playedAt); }
}
