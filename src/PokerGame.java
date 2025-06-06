import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import javax.swing.*;
import java.util.ArrayList;

public class PokerGame extends JPanel implements MouseListener {
    private ArrayList<Player> players;
    private ArrayList<Card> deck;
    private ArrayList<Card> communityCards;
    private int currentPlayerIndex;
    private int dealerIndex;
    private int potSize;
    private int bigBlind;
    private int smallBlind;
    private int currentBet;
    private String gamePhase;
    private boolean gameActive;
    private int raiseAmount;
    private boolean waitingForNPC;
    private boolean userHasActed;
    private String winnerName;

    private Rectangle checkButton;
    private Rectangle callButton;
    private Rectangle raiseButton;
    private Rectangle foldButton;
    private Rectangle chip20Button;
    private Rectangle chip50Button;
    private Rectangle chip100Button;

    public PokerGame() {
        initializeGame();
        setupButtons();
        addMouseListener(this);
        setPreferredSize(new Dimension(1200, 900));
    }

    public void initializeGame() {
        players = new ArrayList<Player>();
        players.add(new Player("You", true));
        players.add(new Player("NPC 1", false));
        players.add(new Player("NPC 2", false));
        players.add(new Player("NPC 3", false));
        players.add(new Player("NPC 4", false));

        communityCards = new ArrayList<Card>();
        bigBlind = 100;
        smallBlind = 50;
        currentBet = 0;
        potSize = 0;
        dealerIndex = 0;
        gamePhase = "preflop";
        gameActive = false;
        raiseAmount = 50;
        waitingForNPC = false;
        userHasActed = false;
        winnerName = "";

        startNewHand();
    }

    public void setupButtons() {
        checkButton = new Rectangle(50, 650, 100, 40);
        callButton = new Rectangle(160, 650, 100, 40);
        raiseButton = new Rectangle(270, 650, 100, 40);
        foldButton = new Rectangle(380, 650, 100, 40);

        chip20Button = new Rectangle(50, 700, 60, 40);
        chip50Button = new Rectangle(120, 700, 60, 40);
        chip100Button = new Rectangle(190, 700, 60, 40);
    }

    public void startNewHand() {
        deck = Card.buildDeck();

        for (Player p : players) {
            p.resetForNewHand();
        }

        communityCards.clear();
        potSize = 0;
        currentBet = 0;
        gamePhase = "preflop";
        waitingForNPC = false;
        userHasActed = false;
        winnerName = "";

        postBlinds();
        dealHoleCards();
        currentPlayerIndex = (dealerIndex + 3) % players.size();
        gameActive = true;

        if (!players.get(currentPlayerIndex).isUser()) {
            scheduleNPCAction();
        }
    }

    public void postBlinds() {
        int smallBlindIndex = (dealerIndex + 1) % players.size();
        int bigBlindIndex = (dealerIndex + 2) % players.size();

        players.get(smallBlindIndex).bet(smallBlind);
        players.get(bigBlindIndex).bet(bigBlind);
        potSize += smallBlind + bigBlind;
        currentBet = bigBlind;
    }

    public void dealHoleCards() {
        for (int i = 0; i < 2; i++) {
            for (Player p : players) {
                if (deck.size() > 0) {
                    p.addCard(deck.remove(0));
                }
            }
        }
    }

    public void dealCommunityCards(int count) {
        for (int i = 0; i < count && deck.size() > 0; i++) {
            communityCards.add(deck.remove(0));
        }
    }

    public void nextPhase() {
        resetBetting();

        if (gamePhase.equals("preflop")) {
            dealCommunityCards(3);
            gamePhase = "flop";
        } else if (gamePhase.equals("flop")) {
            dealCommunityCards(1);
            gamePhase = "turn";
        } else if (gamePhase.equals("turn")) {
            dealCommunityCards(1);
            gamePhase = "river";
        } else if (gamePhase.equals("river")) {
            determineWinner();
            return;
        }

        currentPlayerIndex = (dealerIndex + 1) % players.size();
        findNextValidPlayer();

        if (!players.get(currentPlayerIndex).isUser()) {
            scheduleNPCAction();
        }
    }

    public void resetBetting() {
        for (Player p : players) {
            p.resetCurrentBet();
        }
        currentBet = 0;
    }

    public void findNextValidPlayer() {
        int checked = 0;
        while (checked < players.size()) {
            if (!players.get(currentPlayerIndex).isFolded()) {
                break;
            }
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            checked++;
        }
    }

    public void nextPlayer() {
        if (!gameActive) return;

        boolean bettingRoundComplete = true;
        int activePlayers = 0;

        for (Player p : players) {
            if (!p.isFolded()) {
                activePlayers++;
                if (p.getCurrentBet() < currentBet) {
                    bettingRoundComplete = false;
                }
            }
        }

        if (activePlayers <= 1) {
            determineWinner();
            return;
        }

        if (bettingRoundComplete) {
            nextPhase();
        } else {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            while (players.get(currentPlayerIndex).isFolded()) {
                currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            }

            if (!players.get(currentPlayerIndex).isUser()) {
                scheduleNPCAction();
            }
        }
        repaint();
    }

    public void scheduleNPCAction() {
        waitingForNPC = true;
        Timer timer = new Timer(2000, e -> {
            handleNPCAction();
            waitingForNPC = false;
            nextPlayer();
        });
        timer.setRepeats(false);
        timer.start();
    }

    public void handleNPCAction() {
        Player npc = players.get(currentPlayerIndex);
        int action = (int) (Math.random() * 3);
        //random move (check,fold,call)

        if (currentBet > npc.getCurrentBet()) {
            if (action == 0 || !npc.getBalance().canDeduct(currentBet - npc.getCurrentBet())) {
                npc.fold();
            } else {
                int callAmount = currentBet - npc.getCurrentBet();
                npc.bet(callAmount);
                potSize += callAmount;
            }
        } else {
            if (action == 0) {
                //check
            } else if (action == 1 && npc.getBalance().canDeduct(bigBlind)) {
                npc.bet(bigBlind);
                potSize += bigBlind;
                currentBet = Math.max(currentBet, npc.getCurrentBet());
            }
        }
    }

    public void determineWinner() {
        ArrayList<Player> activePlayers = new ArrayList<Player>();
        for (Player p : players) {
            if (!p.isFolded()) {
                activePlayers.add(p);
            }
        }

        if (activePlayers.size() == 1) {
            Player winner = activePlayers.get(0);
            winner.getBalance().add(potSize);
            winnerName = winner.getName();
        } else {
            Player winner = activePlayers.get(0);
            int bestHand = HandEvaluator.evaluateHand(winner, communityCards);

            for (int i = 1; i < activePlayers.size(); i++) {
                int handValue = HandEvaluator.evaluateHand(activePlayers.get(i), communityCards);
                if (handValue > bestHand) {
                    bestHand = handValue;
                    winner = activePlayers.get(i);
                }
            }

            winner.getBalance().add(potSize);
            winnerName = winner.getName();
        }

        gameActive = false;
        dealerIndex = (dealerIndex + 1) % players.size();

        Timer timer = new Timer(5000, e -> {
            startNewHand();
            repaint();
        });
        timer.setRepeats(false);
        timer.start();
    }

    public String getHandStrengthName(int handValue) {
        if (handValue == 10) {
            return "Royal Flush";
        } else if (handValue == 9) {
            return "Straight Flush";
        } else if (handValue == 8) {
            return "Four of a Kind";
        } else if (handValue == 7) {
            return "Full House";
        } else if (handValue == 6) {
            return "Flush";
        } else if (handValue == 5) {
            return "Straight";
        } else if (handValue == 4) {
            return "Three of a Kind";
        } else if (handValue == 3) {
            return "Two Pair";
        } else if (handValue == 2) {
            return "Pair";
        } else {
            return "High Card";
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(0, 128, 0));
        g.fillRect(0, 0, getWidth(), getHeight());

        drawCommunityCards(g);
        drawPlayerCards(g);
        drawNPCCards(g);
        drawButtons(g);
        drawGameInfo(g);
        drawPlayerInfo(g);
    }

    public void drawCommunityCards(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Community Cards", 450, 50);

        int x = 400;
        int y = 70;
        for (int i = 0; i < 5; i++) {
            if (i < communityCards.size()) {
                Card card = communityCards.get(i);
                card.setRectangleLocation(x, y);
                g.drawImage(card.getImage(), x, y, null);
            } else {
                g.setColor(Color.GRAY);
                g.fillRect(x, y, 71, 96);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, 71, 96);
            }
            x += 80;
        }
    }

    public void drawPlayerCards(Graphics g) {
        Player user = players.get(0);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Your Cards", 50, 400);

        if (communityCards.size() >= 3) {
            int handValue = HandEvaluator.evaluateHand(user, communityCards);
            String handStrength = getHandStrengthName(handValue);
            g.drawString("Hand: " + handStrength, 50, 420);
        }

        int x = 50;
        int y = 440;
        for (Card card : user.getHand()) {
            card.setRectangleLocation(x, y);
            g.drawImage(card.getImage(), x, y, null);
            x += 80;
        }
    }

    public void drawNPCCards(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        for (int i = 1; i < players.size(); i++) {
            Player npc = players.get(i);
            int x;
            int y;
            if (i == 1) {
                x = 900;
                y = 190;
            } else if (i == 2) {
                x = 900;
                y = 340;
            } else if (i == 3) {
                x = 900;
                y = 490;
            } else {
                x = 50;
                y = 190;
            }

            g.drawString(npc.getName(), x, y - 10);

            if (communityCards.size() >= 3 && !npc.isFolded()) {
                int handValue = HandEvaluator.evaluateHand(npc, communityCards);
                String handStrength = getHandStrengthName(handValue);
                g.drawString("Hand: " + handStrength, x, y + 10);
            }

            //draw cards (face down for NPCs)
            for (int j = 0; j < npc.getHand().size(); j++) {
                Card card = npc.getHand().get(j);
                if (npc.isFolded()) {

                    g.setColor(Color.DARK_GRAY);
                    g.fillRect(x + j * 40, y + 30, 35, 48);
                    g.setColor(Color.BLACK);
                    g.drawRect(x + j * 40, y + 30, 35, 48);
                } else {
                    //show NPC cards(in real game these would be face down)
                    g.drawImage(card.getImage(), x + j * 40, y + 30, 35, 48, null);
                }
            }
        }
    }

    public void drawButtons(Graphics g) {
        //only draw buttons if it's the user's turn and game is active
        if (!gameActive || !players.get(currentPlayerIndex).isUser() || waitingForNPC) {
            return;
        }

        Player user = players.get(0);
        boolean canCheck = (currentBet == 0 || currentBet == user.getCurrentBet()); //can only check if no bet or you've matched the bet
        boolean canCall = (currentBet > user.getCurrentBet());

        //to show if the player can press check
        Color checkButtonColor = Color.GRAY;
        if (canCheck) {
            checkButtonColor = Color.GREEN;
        }
        drawButton(g, checkButton, "Check", checkButtonColor);

        Color callButtonColor = Color.GRAY;
        String callText = "Call";
        if (canCall) {
            callButtonColor = Color.YELLOW;
            callText = "Call $" + (currentBet - user.getCurrentBet());
        }
        drawButton(g, callButton, callText, callButtonColor);

        drawButton(g, raiseButton, "Raise $" + raiseAmount, Color.ORANGE);
        drawButton(g, foldButton, "Fold", Color.RED);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("Raise Amount:", 50, 690);

        Color chip20Color = Color.LIGHT_GRAY;
        if (raiseAmount == 20) {
            chip20Color = Color.CYAN;
        }
        drawButton(g, chip20Button, "$20", chip20Color);

        Color chip50Color = Color.LIGHT_GRAY;
        if (raiseAmount == 50) {
            chip50Color = Color.CYAN;
        }
        drawButton(g, chip50Button, "$50", chip50Color);

        Color chip100Color = Color.LIGHT_GRAY;
        if (raiseAmount == 100) {
            chip100Color = Color.CYAN;
        }
        drawButton(g, chip100Button, "$100", chip100Color);
    }

    public void drawButton(Graphics g, Rectangle button, String text, Color backgroundColor) {
        g.setColor(backgroundColor);
        g.fillRect(button.x, button.y, button.width, button.height);

        g.setColor(Color.BLACK);
        g.drawRect(button.x, button.y, button.width, button.height);

        g.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fm = g.getFontMetrics();
        int textX = button.x + (button.width - fm.stringWidth(text)) / 2;
        int textY = button.y + ((button.height - fm.getHeight()) / 2) + fm.getAscent();
        g.drawString(text, textX, textY);
    }

    public void drawGameInfo(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Pot: $" + potSize, 50, 30);
        g.drawString("Phase: " + gamePhase, 200, 30);
        g.drawString("Current Bet: $" + currentBet, 350, 30);

        if (gameActive) {
            String currentPlayerName = players.get(currentPlayerIndex).getName();
            g.drawString("Current Player: " + currentPlayerName, 500, 30);

            if (players.get(currentPlayerIndex).isUser() && !waitingForNPC) {
                g.setColor(Color.YELLOW);
                g.drawString("YOUR TURN - Choose an action", 50, 600);
            } else if (waitingForNPC) {
                g.setColor(Color.CYAN);
                g.drawString("Waiting for " + currentPlayerName + "...", 50, 600);
            }
        } else if (!winnerName.isEmpty()) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Winner: " + winnerName, 50, 600);
            g.setFont(new Font("Arial", Font.BOLD, 16)); // Reset font
        }
    }

    public void drawPlayerInfo(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));

        Player user = players.get(0);
        String userStatus;
        if (user.isFolded()) {
            userStatus = " (FOLDED)";
        } else {
            userStatus = "";
        }

        String userCurrentPlayerMarker;
        if (0 == currentPlayerIndex) {
            userCurrentPlayerMarker = " <<< ";
        } else {
            userCurrentPlayerMarker = "";
        }

        g.drawString(user.getName() + ": $" + user.getBalance().getBalance() +
                " (Bet: $" + user.getCurrentBet() + ")" + userStatus + userCurrentPlayerMarker, 50, 560);

        for (int i = 1; i < players.size(); i++) {
            Player p = players.get(i);
            int x, y;

            if (i == 1) {
                x = 900;
                y = 130;
            } else if (i == 2) {
                x = 900;
                y = 280;
            } else if (i == 3) {
                x = 900;
                y = 430;
            } else {
                x = 50;
                y = 130;
            }

            String status;
            if (p.isFolded()) {
                status = " (FOLDED)";
            } else {
                status = "";
            }

            String currentPlayerMarker;
            if (i == currentPlayerIndex) {
                currentPlayerMarker = " <<< ";
            } else {
                currentPlayerMarker = "";
            }

            g.drawString(p.getName() + ": $" + p.getBalance().getBalance() +
                    " (Bet: $" + p.getCurrentBet() + ")" + status + currentPlayerMarker, x, y);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //only detects clicks if it's the user's turn and game is active
        if (!gameActive || !players.get(currentPlayerIndex).isUser() || waitingForNPC) {
//            System.out.println("Click ignored - gameActive: " + gameActive +
//                    ", isUserTurn: " + (gameActive && players.get(currentPlayerIndex).isUser()) +
//                    ", waitingForNPC: " + waitingForNPC);
            return;
        }

        Player user = players.get(0);
        Point p = e.getPoint();

//        System.out.println("Mouse clicked at: " + p.x + ", " + p.y);
//        System.out.println("Check button bounds: " + checkButton);
//        System.out.println("Current bet: " + currentBet + ", User current bet: " + user.getCurrentBet());

        if (checkButton.contains(p)) {
            if (currentBet == 0 || currentBet == user.getCurrentBet()) {
                nextPlayer();
            }
        } else if (callButton.contains(p)) {
            if (currentBet > user.getCurrentBet()) {
                int callAmount = currentBet - user.getCurrentBet();
                if (user.getBalance().canDeduct(callAmount) && user.bet(callAmount)) {
                    potSize += callAmount;
                    nextPlayer();
                }
            }
        } else if (raiseButton.contains(p)) {
            int callAmount = currentBet - user.getCurrentBet();
            if (callAmount < 0) {
                callAmount = 0;
            }
            int totalBet = callAmount + raiseAmount;
            if (user.getBalance().canDeduct(totalBet) && user.bet(totalBet)) {
                potSize += totalBet;
                currentBet = user.getCurrentBet();
                nextPlayer();
            }
        } else if (foldButton.contains(p)) {
            user.fold();
            nextPlayer();
        } else if (chip20Button.contains(p)) {
            raiseAmount = 20;
        } else if (chip50Button.contains(p)) {
            raiseAmount = 50;
        } else if (chip100Button.contains(p)) {
            raiseAmount = 100;
        }

        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Texas Hold'em Poker");
        PokerGame game = new PokerGame();
        frame.add(game);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 900);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}