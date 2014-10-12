package net.connerbrooks.blackjack;

import android.util.Log;

import net.connerbrooks.blackjack.models.Card;
import net.connerbrooks.blackjack.models.Deck;
import net.connerbrooks.blackjack.models.Player;

import java.util.ArrayList;

public class Game {
    private Deck gameDeck;
    private ArrayList<Player> players;
    boolean isHoleFlipped;
    int currPlayer;

    public Game() {
        gameDeck = new Deck();

        players = new ArrayList<Player>();
        players.add(new Player("Dealer", 500));
        players.add(new Player("Player1", 500));
        isHoleFlipped = true;
        currPlayer = 0;

        dealHands();
    }

    public Player scoreHands() {
        Player winner = null;
        int maxScore = Integer.MIN_VALUE;
        for(Player p: players) {
            p.setScore(score(p));
            if(p.getScore() > maxScore && p.getScore() <= 21) {
                winner = p;
                maxScore = p.getScore();
            }
        }
        return winner;
    }

    public int score(Player p) {
        int score = 0;
        int numAces = 0;

        for(Card c : p.getHand()) {
            int rank = c.getRank();
            if(rank <= 8)
                score += rank;
            if(rank <= 11)
                score += 10;
            else {
                score += 11;
                numAces++;
            }
            while(score > 21 && numAces > 0) {
                score -= 10;
                numAces--;
            }
        }
        return score;
    }

    // Deal cards to players
    public void dealHands() {
        for(Player p : players)
            for(int i = 0; i < 2; i++)
                p.giveCard(gameDeck.deal());

        Log.i("Hand Size after deal", "size: " + players.get(0).getHand().size());
    }


    public void hit(Player p) {
        p.giveCard(gameDeck.deal());
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void nextPlayer() {
        currPlayer = (++currPlayer)%players.size();
    }

    public Player getCurrentPlayer() {
        return players.get(currPlayer);
    }

}
