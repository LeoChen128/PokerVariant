import java.util.ArrayList;

public class HandEvaluator {

    public static int evaluateHand(Player player, ArrayList<Card> communityCards) {
        ArrayList<Card> allCards = new ArrayList<Card>(player.getHand());
        allCards.addAll(communityCards);

        if (isRoyalFlush(allCards)) return 10;
        if (isStraightFlush(allCards)) return 9;
        if (isFourOfAKind(allCards)) return 8;
        if (isFullHouse(allCards)) return 7;
        if (isFlush(allCards)) return 6;
        if (isStraight(allCards)) return 5;
        if (isThreeOfAKind(allCards)) return 4;
        if (isTwoPair(allCards)) return 3;
        if (isPair(allCards)) return 2;
        return 1;
    }

    public static boolean isRoyalFlush(ArrayList<Card> cards) {
        return isStraightFlush(cards) && hasValue(cards, 14) && hasValue(cards, 13) &&
                hasValue(cards, 12) && hasValue(cards, 11) && hasValue(cards, 10);
    }

    public static boolean isStraightFlush(ArrayList<Card> cards) {
        return isFlush(cards) && isStraight(cards);
    }

    public static boolean isFourOfAKind(ArrayList<Card> cards) {
        int[] counts = new int[15];
        for (Card card : cards) {
            counts[card.getNumericalValue()]++;
        }
        for (int count : counts) {
            if (count >= 4) return true;
        }
        return false;
    }

    public static boolean isFullHouse(ArrayList<Card> cards) {
        int[] counts = new int[15];
        for (Card card : cards) {
            counts[card.getNumericalValue()]++;
        }
        boolean hasThree = false;
        boolean hasPair = false;
        for (int count : counts) {
            if (count >= 3) {
                hasThree = true;
            } else if (count >= 2) {
                hasPair = true;
            }
        }
        return hasThree && hasPair;
    }

    public static boolean isFlush(ArrayList<Card> cards) {
        int[] suitCounts = new int[4];
        String[] suits = {"clubs", "diamonds", "hearts", "spades"};

        for (Card card : cards) {
            for (int i = 0; i < suits.length; i++) {
                if (card.getSuit().equals(suits[i])) {
                    suitCounts[i]++;
                    break;
                }
            }
        }

        for (int count : suitCounts) {
            if (count >= 5) return true;
        }
        return false;
    }

    public static boolean isStraight(ArrayList<Card> cards) {
        boolean[] values = new boolean[15];
        for (Card card : cards) {
            values[card.getNumericalValue()] = true;
        }

        int consecutive = 0;
        for (int i = 2; i <= 14; i++) {
            if (values[i]) {
                consecutive++;
                if (consecutive >= 5) return true;
            } else {
                consecutive = 0;
            }
        }

        if (values[14] && values[2] && values[3] && values[4] && values[5]) {
            return true;
        }

        return false;
    }

    public static boolean isThreeOfAKind(ArrayList<Card> cards) {
        int[] counts = new int[15];
        for (Card card : cards) {
            counts[card.getNumericalValue()]++;
        }
        for (int count : counts) {
            if (count >= 3) return true;
        }
        return false;
    }

    public static boolean isTwoPair(ArrayList<Card> cards) {
        int[] counts = new int[15];
        for (Card card : cards) {
            counts[card.getNumericalValue()]++;
        }
        int pairs = 0;
        for (int count : counts) {
            if (count >= 2) pairs++;
        }
        return pairs >= 2;
    }

    public static boolean isPair(ArrayList<Card> cards) {
        int[] counts = new int[15];
        for (Card card : cards) {
            counts[card.getNumericalValue()]++;
        }
        for (int count : counts) {
            if (count >= 2) return true;
        }
        return false;
    }

    public static boolean hasValue(ArrayList<Card> cards, int value) {
        for (Card card : cards) {
            if (card.getNumericalValue() == value) return true;
        }
        return false;
    }
}