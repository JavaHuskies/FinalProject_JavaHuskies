package model.Casino;

import java.time.LocalDateTime;

public class GameRound {

    private final int roundNumber;
    private final RouletteRules.Bet bet;
    private final int spinResult;
    private final int payout;
    private final LocalDateTime timestamp;

    public GameRound(int roundNumber, RouletteRules.Bet bet, int spinResult, int payout) {
        this.roundNumber = roundNumber;
        this.bet = bet;
        this.spinResult = spinResult;
        this.payout = payout;
        this.timestamp = LocalDateTime.now();
    }

    public int getRoundNumber() { return roundNumber; }
    public RouletteRules.Bet getBet() { return bet; }
    public int getSpinResult() { return spinResult; }
    public int getPayout() { return payout; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
