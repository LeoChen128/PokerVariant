import java.util.ArrayList;

public class Player {
    private String name;
    private ArrayList<Card> hand;
    private Balance balance;
    private int currentBet;
    private boolean folded;
    private boolean isUser;

    public Player(String name, boolean isUser) {
        this.name = name;
        this.hand = new ArrayList<Card>();
        this.balance = new Balance();
        this.currentBet = 0;
        this.folded = false;
        this.isUser = isUser;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public Balance getBalance() {
        return balance;
    }

    public int getCurrentBet() {
        return currentBet;
    }

    public boolean isFolded() {
        return folded;
    }

    public boolean isUser() {
        return isUser;
    }

    public void addCard(Card card) {
        hand.add(card);
    }

    public void clearHand() {
        hand.clear();
    }

    public boolean bet(int amount) {
        if (balance.canDeduct(amount)) {
            balance.deduct(amount);
            currentBet += amount;
            return true;
        }
        return false;
    }

    public void fold() {
        folded = true;
    }

    public void resetForNewHand() {
        clearHand();
        currentBet = 0;
        folded = false;
        //when new hand, go back to false
    }

    public void resetCurrentBet() {
        currentBet = 0;
    }
}