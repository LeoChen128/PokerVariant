import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.geom.Ellipse2D;
import java.util.Arrays;
import java.util.Collections;
public class PokerGame{
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
    
    private Rectangle checkButton, callButton, raiseButton, foldButton;
    private Rectangle chip1Button, chip5Button, chip25Button, chip100Button, chip500Button;
    private int raiseAmount;

    public PokerGame() {
        initializeGame();
        setupButtons();
        addMouseListener(this);
        setPreferredSize(new Dimension(1000, 800));
    }

    public void initializeGame() {
        players = new ArrayList<>();
        players.add(new Player("You", true));
        players.add(new Player("NPC 1", false));
        players.add(new Player("NPC 2", false));
        players.add(new Player("NPC 3", false));
        players.add(new Player("NPC 4", false));
        
        communityCards = new ArrayList<>();
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
        checkButton = new Rectangle(50, 500, 80, 30);
        callButton = new Rectangle(140, 500, 80, 30);
        raiseButton = new Rectangle(230, 500, 80, 30);
        foldButton = new Rectangle(320, 500, 80, 30);
        
        chip1Button = new Rectangle(50, 550, 50, 30);
        chip5Button = new Rectangle(110, 550, 50, 30);
        chip25Button = new Rectangle(170, 550, 50, 30);
        chip100Button = new Rectangle(230, 550, 50, 30);
        chip500Button = new Rectangle(290, 550, 50, 30);
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
    }

    public void resetBetting() {
        for (Player p : players) {
            p.currentBet = 0;
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
            do {
                currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            } while (players.get(currentPlayerIndex).isFolded());
            
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
                // Check
            } else if (action == 1 && npc.getBalance().canDeduct(bigBlind)) {
                npc.bet(bigBlind);
                potSize += bigBlind;
                currentBet = Math.max(currentBet, npc.getCurrentBet());
            }
        }
        
        Timer timer = new Timer(1000, e -> {
            nextPlayer();
            repaint();
        });
        timer.setRepeats(false);
        timer.start();
    }

    public void determineWinner() {
        ArrayList<Player> activePlayers = new ArrayList<>();
        for (Player p : players) {
            if (!p.isFolded()) {
                activePlayers.add(p);
            }
        }
        
        if (activePlayers.size() == 1) {
            activePlayers.get(0).getBalance().add(potSize);
        } else {
            Player winner = activePlayers.get(0);
            int bestHand = evaluateHand(winner);
            
            for (int i = 1; i < activePlayers.size(); i++) {
                int handValue = evaluateHand(activePlayers.get(i));
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

    public int evaluateHand(Player player) {
        ArrayList<Card> allCards = new ArrayList<>(player.getHand());
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

    public boolean isRoyalFlush(ArrayList<Card> cards) {
        return isStraightFlush(cards) && hasValue(cards, 14) && hasValue(cards, 13) && 
               hasValue(cards, 12) && hasValue(cards, 11) && hasValue(cards, 10);
    }

    public boolean isStraightFlush(ArrayList<Card> cards) {
        return isFlush(cards) && isStraight(cards);
    }

    public boolean isFourOfAKind(ArrayList<Card> cards) {
        int[] counts = new int[15];
        for (Card card : cards) {
            counts[card.getNumericalValue()]++;
        }
        for (int count : counts) {
            if (count >= 4) return true;
        }
        return false;
    }

    public boolean isFullHouse(ArrayList<Card> cards) {
        int[] counts = new int[15];
        for (Card card : cards) {
            counts[card.getNumericalValue()]++;
        }
        boolean hasThree = false, hasPair = false;
        for (int count : counts) {
            if (count >= 3) hasThree = true;
            if (count >= 2 && count != 3) hasPair = true;
        }
        return hasThree && hasPair;
    }

    public boolean isFlush(ArrayList<Card> cards) {
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

    public boolean isStraight(ArrayList<Card> cards) {
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

    public boolean isThreeOfAKind(ArrayList<Card> cards) {
        int[] counts = new int[15];
        for (Card card : cards) {
            counts[card.getNumericalValue()]++;
        }
        for (int count : counts) {
            if (count >= 3) return true;
        }
        return false;
    }

    public boolean isTwoPair(ArrayList<Card> cards) {
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

    public boolean isPair(ArrayList<Card> cards) {
        int[] counts = new int[15];
        for (Card card : cards) {
            counts[card.getNumericalValue()]++;
        }
        for (int count : counts) {
            if (count >= 2) return true;
        }
        return false;
    }

    public boolean hasValue(ArrayList<Card> cards, int value) {
        for (Card card : cards) {
            if (card.getNumericalValue() == value) return true;
        }
        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
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
            g.setColor(Color.WHITE);
            g.fillRect(x, y, 71, 96);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, 71, 96);
            
            if (i < communityCards.size()) {
                Card card = communityCards.get(i);
                card.setRectangleLocation(x, y);
                g.setFont(new Font("Arial", Font.BOLD, 12));
                g.drawString(card.getValue(), x + 5, y + 20);
                g.drawString(card.getSuit(), x + 5, y + 40);
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
            g.setColor(Color.WHITE);
            g.fillRect(x, y, 71, 96);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, 71, 96);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.drawString(card.getValue(), x + 5, y + 20);
            g.drawString(card.getSuit(), x + 5, y + 40);
            x += 80;
        }
    }

    public void drawButtons(Graphics g) {
        if (!gameActive || !players.get(currentPlayerIndex).isUser()) return;
        
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(checkButton.x, checkButton.y, checkButton.width, checkButton.height);
        g.fillRect(callButton.x, callButton.y, callButton.width, callButton.height);
        g.fillRect(raiseButton.x, raiseButton.y, raiseButton.width, raiseButton.height);
        g.fillRect(foldButton.x, foldButton.y, foldButton.width, foldButton.height);
        
        g.setColor(Color.BLACK);
        g.drawRect(checkButton.x, checkButton.y, checkButton.width, checkButton.height);
        g.drawRect(callButton.x, callButton.y, callButton.width, callButton.height);
        g.drawRect(raiseButton.x, raiseButton.y, raiseButton.width, raiseButton.height);
        g.drawRect(foldButton.x, foldButton.y, foldButton.width, foldButton.height);
        
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("Check", checkButton.x + 20, checkButton.y + 20);
        g.drawString("Call", callButton.x + 25, callButton.y + 20);
        g.drawString("Raise", raiseButton.x + 20, raiseButton.y + 20);
        g.drawString("Fold", foldButton.x + 25, foldButton.y + 20);
        
        g.setColor(Color.YELLOW);
        g.fillRect(chip1Button.x, chip1Button.y, chip1Button.width, chip1Button.height);
        g.fillRect(chip5Button.x, chip5Button.y, chip5Button.width, chip5Button.height);
        g.fillRect(chip25Button.x, chip25Button.y, chip25Button.width, chip25Button.height);
        g.fillRect(chip100Button.x, chip100Button.y, chip100Button.width, chip100Button.height);
        g.fillRect(chip500Button.x, chip500Button.y, chip500Button.width, chip500Button.height);
        
        g.setColor(Color.BLACK);
        g.drawRect(chip1Button.x, chip1Button.y, chip1Button.width, chip1Button.height);
        g.drawRect(chip5Button.x, chip5Button.y, chip5Button.width, chip5Button.height);
        g.drawRect(chip25Button.x, chip25Button.y, chip25Button.width, chip25Button.height);
        g.drawRect(chip100Button.x, chip100Button.y, chip100Button.width, chip100Button.height);
        g.drawRect(chip500Button.x, chip500Button.y, chip500Button.width, chip500Button.height);
        
        g.setFont(new Font("Arial", Font.BOLD, 10));
        g.drawString("1", chip1Button.x + 20, chip1Button.y + 20);
        g.drawString("5", chip5Button.x + 20, chip5Button.y + 20);
        g.drawString("25", chip25Button.x + 15, chip25Button.y + 20);
        g.drawString("100", chip100Button.x + 12, chip100Button.y + 20);
        g.drawString("500", chip500Button.x + 12, chip500Button.y + 20);
    }

    public void drawGameInfo(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Pot: $" + potSize, 500, 30);
        g.drawString("Phase: " + gamePhase, 650, 30);
        g.drawString("Current Bet: $" + currentBet, 500, 200);
        g.drawString("Raise Amount: $" + raiseAmount, 650, 200);
        
        if (gameActive) {
            g.drawString("Current Player: " + players.get(currentPlayerIndex).getName(), 50, 30);
        }
    }

    public void drawPlayerInfo(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        
        int y = 250;
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            String status = p.isFolded() ? " (FOLDED)" : "";
            g.drawString(p.getName() + ": $" + p.getBalance().getBalance() + 
                        " (Bet: $" + p.getCurrentBet() + ")" + status, 500, y);
            y += 20;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!gameActive || !players.get(currentPlayerIndex).isUser()) return;
        
        Player user = players.get(0);
        Point p = e.getPoint();
        
        if (checkButton.contains(p)) {
            if (currentBet == user.getCurrentBet()) {
                nextPlayer();
            }
        } else if (callButton.contains(p)) {
            int callAmount = currentBet - user.getCurrentBet();
            if (user.bet(callAmount)) {
                potSize += callAmount;
                nextPlayer();
            }
        } else if (raiseButton.contains(p)) {
            int totalRaise = currentBet - user.getCurrentBet() + raiseAmount;
            if (user.bet(totalRaise)) {
                potSize += totalRaise;
                currentBet = user.getCurrentBet();
                nextPlayer();
            }
        } else if (foldButton.contains(p)) {
            user.fold();
            nextPlayer();
        } else if (chip1Button.contains(p)) {
            raiseAmount = 1;
        } else if (chip5Button.contains(p)) {
            raiseAmount = 5;
        } else if (chip25Button.contains(p)) {
            raiseAmount = 25;
        } else if (chip100Button.contains(p)) {
            raiseAmount = 100;
        } else if (chip500Button.contains(p)) {
            raiseAmount = 500;
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
