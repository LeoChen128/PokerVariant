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

    // Individual Rectangle objects for buttons
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
        setPreferredSize(new Dimension(1000, 800));
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

        startNewHand();
    }

    public void setupButtons() {
        // Action buttons positioned better and larger for visibility
        checkButton = new Rectangle(50, 650, 100, 40);
        callButton = new Rectangle(160, 650, 100, 40);
        raiseButton = new Rectangle(270, 650, 100, 40);
        foldButton = new Rectangle(380, 650, 100, 40);

        // Chip selection buttons
        chip20Button = new Rectangle(50, 700, 60, 40);
        chip50Button = new Rectangle(120, 700, 60, 40);
        chip100Button = new Rectangle(190, 700, 60, 40);
    }

    public void startNewHand() {
        deck = Card.buildDeck(); // Using your Card.buildDeck() method

        for (Player p : players) {
            p.resetForNewHand();
        }

        communityCards.clear();
        potSize = 0;
        currentBet = 0;
        gamePhase = "preflop";

        postBlinds();
        dealHoleCards();
        currentPlayerIndex = (dealerIndex + 3) % players.size();
        gameActive = true;
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
            dealCommunityCards(3); // Flop
            gamePhase = "flop";
        } else if (gamePhase.equals("flop")) {
            dealCommunityCards(1); // Turn
            gamePhase = "turn";
        } else if (gamePhase.equals("turn")) {
            dealCommunityCards(1); // River
            gamePhase = "river";
        } else if (gamePhase.equals("river")) {
            determineWinner();
            return;
        }

        currentPlayerIndex = (dealerIndex + 1) % players.size();
        findNextValidPlayer();
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
                handleNPCAction();
            }
        }
    }

    public void handleNPCAction() {
        Player npc = players.get(currentPlayerIndex);
        int action = (int)(Math.random() * 3);

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
                // Check - do nothing
            } else if (action == 1 && npc.getBalance().canDeduct(bigBlind)) {
                npc.bet(bigBlind);
                potSize += bigBlind;
                currentBet = Math.max(currentBet, npc.getCurrentBet());
            }
        }

        Timer timer = new Timer(1500, e -> {
            nextPlayer();
            repaint();
        });
        timer.setRepeats(false);
        timer.start();
    }

    public void determineWinner() {
        ArrayList<Player> activePlayers = new ArrayList<Player>();
        for (Player p : players) {
            if (!p.isFolded()) {
                activePlayers.add(p);
            }
        }

        if (activePlayers.size() == 1) {
            activePlayers.get(0).getBalance().add(potSize);
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
        }

        gameActive = false;
        dealerIndex = (dealerIndex + 1) % players.size();

        Timer timer = new Timer(3000, e -> {
            startNewHand();
            repaint();
        });
        timer.setRepeats(false);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Green poker table background
        g.setColor(new Color(0, 128, 0));
        g.fillRect(0, 0, getWidth(), getHeight());

        drawCommunityCards(g);
        drawPlayerCards(g);
        drawButtons(g);
        drawGameInfo(g);
        drawPlayerInfo(g);
    }

    public void drawCommunityCards(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Community Cards", 350, 50);

        int x = 300;
        int y = 70;
        for (int i = 0; i < 5; i++) {
            if (i < communityCards.size()) {
                Card card = communityCards.get(i);
                card.setRectangleLocation(x, y);
                // Use the card's image rendering
                g.drawImage(card.getImage(), x, y, null);
            } else {
                // Draw empty card slot
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

        int x = 50;
        int y = 420;
        for (Card card : user.getHand()) {
            card.setRectangleLocation(x, y);
            // Use the card's image rendering
            g.drawImage(card.getImage(), x, y, null);
            x += 80;
        }
    }

    public void drawButtons(Graphics g) {
        if (!gameActive || !players.get(currentPlayerIndex).isUser()) return;

        Player user = players.get(0);
        boolean canCheck = (currentBet == user.getCurrentBet());
        boolean canCall = (currentBet > user.getCurrentBet());

        // Draw action buttons with different colors based on availability
        drawButton(g, checkButton, "Check", canCheck ? Color.GREEN : Color.GRAY);
        drawButton(g, callButton, "Call $" + (currentBet - user.getCurrentBet()),
                canCall ? Color.YELLOW : Color.GRAY);
        drawButton(g, raiseButton, "Raise $" + raiseAmount, Color.ORANGE);
        drawButton(g, foldButton, "Fold", Color.RED);

        // Draw chip selection buttons
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("Raise Amount:", 50, 690);

        drawButton(g, chip20Button, "$20", raiseAmount == 20 ? Color.CYAN : Color.LIGHT_GRAY);
        drawButton(g, chip50Button, "$50", raiseAmount == 50 ? Color.CYAN : Color.LIGHT_GRAY);
        drawButton(g, chip100Button, "$100", raiseAmount == 100 ? Color.CYAN : Color.LIGHT_GRAY);
    }

    private void drawButton(Graphics g, Rectangle button, String text, Color backgroundColor) {
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
        g.drawString("Pot: $" + potSize, 500, 30);
        g.drawString("Phase: " + gamePhase, 650, 30);
        g.drawString("Current Bet: $" + currentBet, 500, 200);
        g.drawString("Raise Amount: $" + raiseAmount, 650, 200);

        if (gameActive) {
            String currentPlayerName = players.get(currentPlayerIndex).getName();
            g.drawString("Current Player: " + currentPlayerName, 50, 30);

            if (players.get(currentPlayerIndex).isUser()) {
                g.setColor(Color.YELLOW);
                g.drawString("YOUR TURN - Choose an action", 50, 600);
            }
        }
    }

    public void drawPlayerInfo(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));

        int y = 250;
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            String status = p.isFolded() ? " (FOLDED)" : "";
            String currentPlayerMarker = (i == currentPlayerIndex) ? " <<< " : "";
            g.drawString(p.getName() + ": $" + p.getBalance().getBalance() +
                    " (Bet: $" + p.getCurrentBet() + ")" + status + currentPlayerMarker, 500, y);
            y += 20;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!gameActive || !players.get(currentPlayerIndex).isUser()) return;

        Player user = players.get(0);
        Point p = e.getPoint();

        if (checkButton.contains(p) && currentBet == user.getCurrentBet()) {
            // Check - no additional bet needed
            nextPlayer();
        } else if (callButton.contains(p) && currentBet > user.getCurrentBet()) {
            // Call - match the current bet
            int callAmount = currentBet - user.getCurrentBet();
            if (user.getBalance().canDeduct(callAmount) && user.bet(callAmount)) {
                potSize += callAmount;
                nextPlayer();
            }
        } else if (raiseButton.contains(p)) {
            // Raise - call current bet plus raise amount
            int callAmount = currentBet - user.getCurrentBet();
            int totalBet = callAmount + raiseAmount;
            if (user.getBalance().canDeduct(totalBet) && user.bet(totalBet)) {
                potSize += totalBet;
                currentBet = user.getCurrentBet();
                nextPlayer();
            }
        } else if (foldButton.contains(p)) {
            // Fold - give up hand
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

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Texas Hold'em Poker");
        PokerGame game = new PokerGame();

        frame.add(game);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}