package io.github.cristinarubylee.models;

public class Player extends GameObject{
    private int totalHealth;
    private int currHealth;

    public Player(){
        totalHealth = 100;
        currHealth = 100;
    }


    public ObjectType getType() {
        return ObjectType.PLAYER;
    }

    public int getTotalHealth(){return totalHealth;}
    public int getCurrHealth(){return currHealth;}


    /**
     * Changes the current health by x, decreasing it if x is negative and increasing it if x is positive.
     *
     * Health is bounded by 0 and totalHealth values.
     */
    public void changeHealth(int x) {
        currHealth = x < 0 ? Math.max(currHealth + x, 0) : Math.min(currHealth + x, totalHealth);
    }
}
