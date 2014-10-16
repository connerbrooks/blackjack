package net.connerbrooks.blackjack.models;

import net.connerbrooks.blackjack.models.Card;

import java.util.ArrayList;

public class Player {
    private String name;
    private ArrayList<Card> hand;
    private int chips;
    private int score;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Player(String name, int chips) {
        this.name = name;
        hand = new ArrayList<Card>();
        this.chips = chips;
    }

    public void takeHand() {
        hand.clear();
    }

    public void giveCard(Card card) {
        hand.add(card);
    }

    // Todo: implement betting system
    public void setChips(int chips) {
        this.chips = chips;
    }

    public int getChips() {
        return chips;
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public String getName() {
        return name;
    }
}
