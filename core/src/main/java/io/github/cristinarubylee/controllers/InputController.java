package io.github.cristinarubylee.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class InputController {

    private float movement;
    private boolean pressedFire;

    //Positive vertical movement
    private int[] posKeyCodes = {Input.Keys.UP, Input.Keys.RIGHT, Input.Keys.W, Input.Keys.D};
    //Negative vertical movement
    private int[] negKeyCodes = {Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.S, Input.Keys.A};
    //Firing
    private int[] fireKeyCodes = {Input.Keys.SPACE};

    /**
     * Creates a new input controller for the specified player.
     */
    public InputController() {
    }


    /**
     * Returns the amount of vertical movement.
     * -1 = down, 1 = up, 0 = still
     *
     * @return amount of vertical movement.
     */
    public float getMovement() {
        return movement;
    }

    /**
     * Returns whether the fire button was pressed.
     *
     * @return whether the fire button was pressed.
     */
    public boolean didPressFire() {
        return pressedFire;
    }


    /**
     * Reads the input and converts the result into game logic.
     */
    public void readInput() {
        // Reset movement
        movement = 0;

//        // Mouse movement input
//        movement = -Gdx.input.getDeltaY() * 0.05f; // Scale sensitivity
//        if (Math.abs(movement) < 0.1f) movement = 0.0f; // Deadzone check

        // Keyboard input for movement
        for (int key : posKeyCodes) {
            if (Gdx.input.isKeyPressed(key)) {
                movement += 1;
                break; // No need to check other keys once we detect movement
            }
        }
        for (int key : negKeyCodes) {
            if (Gdx.input.isKeyPressed(key)) {
                movement -= 1;
                break;
            }
        }

        // Keyboard input for firing
        pressedFire = false;
        for (int key : fireKeyCodes) {
            if (Gdx.input.isKeyPressed(key)) {
                pressedFire = true;
                break;
            }
        }

//        // Mouse fire input
//        pressedFire = pressedFire || Gdx.input.isButtonPressed(Input.Buttons.LEFT);
    }
}
