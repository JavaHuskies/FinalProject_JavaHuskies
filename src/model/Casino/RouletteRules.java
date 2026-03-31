package model.Casino;

import java.util.Set;
import java.util.HashSet;

public class RouletteRules {

    private static final Set<Integer> RED = Set.of(
        1,3,5,7,9,12,14,16,18,19,21,23,25,27,30,32,34,36
    );

    public enum BetType {
        RED, BLACK,
        EVEN, ODD,
        HIGH, LOW,
        SINGLE_NUMBER,
        DOZEN_1, DOZEN_2, DOZEN_3
    }

    /** Returns payout amount (positive or negative). */
    public static int calculatePayout(Bet bet, int result) {

        int amount = bet.amount();
        BetType type = bet.type();

        // Single number
        if (type == BetType.SINGLE_NUMBER) {
            return (bet.targetNumber() == result) ? amount * 35 : -amount;
        }

        // Zero always loses except single-number
        if (result == 0) return -amount;

        // Red / Black
        if (type == BetType.RED)   return RED.contains(result) ? amount : -amount;
        if (type == BetType.BLACK) return !RED.contains(result) ? amount : -amount;

        // Even / Odd
        if (type == BetType.EVEN) return (result % 2 == 0) ? amount : -amount;
        if (type == BetType.ODD)  return (result % 2 == 1) ? amount : -amount;

        // High / Low
        if (type == BetType.HIGH) return (result >= 19) ? amount : -amount;
        if (type == BetType.LOW)  return (result >= 1 && result <= 18) ? amount : -amount;

        // Dozens
        if (type == BetType.DOZEN_1) return (result >= 1 && result <= 12) ? amount * 2 : -amount;
        if (type == BetType.DOZEN_2) return (result >= 13 && result <= 24) ? amount * 2 : -amount;
        if (type == BetType.DOZEN_3) return (result >= 25 && result <= 36) ? amount * 2 : -amount;

        return -amount;
    }

    /** Simple record for a bet. */
    public record Bet(BetType type, int amount, int targetNumber) {}
}
