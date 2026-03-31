package model.Casino;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CasinoSession {

    private final String sessionId;
    private final String guestId;
    private int balance;
    private final List<GameRound> rounds = new ArrayList<>();
    private final Random random = new Random();

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
