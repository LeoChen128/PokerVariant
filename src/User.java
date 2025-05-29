import java.util.ArrayList;

public class User {
    private ArrayList<Card> hand;


    public User(){
        this.hand = new ArrayList<>();
    }

    public void drawCards(){

    }

    public ArrayList<Card> getHand() {
        return hand;
    }
}
