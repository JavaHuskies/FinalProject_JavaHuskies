package model.Casino;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import model.Guest;

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

    private int balance;
    /** @todo remove guestId and use guest instead */
    private String guestId;
    private final List<GameRound> rounds = new ArrayList<>();
    private final Random random = new Random();

    public CasinoSession() {}

    /** @todo guestId -> guest */
    public CasinoSession(String sessionId, String guestId, int startingBalance) {
        this.sessionId = sessionId;
        this.guestId = guestId;
        this.balance = startingBalance;
    }

    public int getBalance() { return balance; }
    public List<GameRound> getRounds() { return rounds; }

    public GameRound playRoulette(RouletteRules.Bet bet) {

        int result = random.nextInt(37); // 0–36
        int payout = RouletteRules.calculatePayout(bet, result);

        balance += payout;

        GameRound round = new GameRound(
            rounds.size() + 1,
            bet,
            result,
            payout
        );

        rounds.add(round);
        return round;
    }
}
