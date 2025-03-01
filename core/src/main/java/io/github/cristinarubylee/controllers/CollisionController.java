package io.github.cristinarubylee.controllers;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.*;
import io.github.cristinarubylee.models.GameObject;
import io.github.cristinarubylee.models.Nightmare;
import io.github.cristinarubylee.models.Photon;
import io.github.cristinarubylee.models.Player;

public class CollisionController {
    /**
     * Width of the region to detect collisions (should be screen size)
     */
    private float width;
    /**
     * Height of the region to detect collisions (should be screen size)
     */
    private float height;
    /**
     * A list of cells where each cell contains a list of game objects
     */
    private Array<Array<GameObject>> cells;
    /**
     * The total number of cells to divide the collision region into
     */
    private int totalCells;
    /**
     * The number of cells in each row
     */
    private int totalRows;
    /**
     * The side length of each (square) cell
     */
    private float cellLength;

    /**
     * Creates a CollisionController for the given screen dimensions.
     *
     * @param width      Width of the screen
     * @param height     Height of the screen
     * @param cellLength Size of each cell
     */
    public CollisionController(float width, float height, float cellLength) {
        this.width = width;
        this.height = height;
        this.cellLength = cellLength;
        this.totalRows = (int) Math.ceil(width / cellLength);
        int ysize = (int) Math.ceil(height / cellLength);
        this.totalCells = totalRows * ysize;

        cells = new Array<>(totalCells);
        for (int ii = 0; ii < totalCells; ii++) {
            cells.add(new Array<GameObject>());
        }
    }

    /**
     * Add a GameObject to a cell.
     * <p>
     * Each object only gets one cell.
     *
     * @param o Object to add
     */
    private void add(GameObject o) {
        // Do not process invalid objects
        if (Float.isNaN(o.getX()) || Float.isNaN(o.getY())) {
            return;
        }

        if ((o.getX() >= 0.0f && o.getX() <= width) && (o.getY() >= 0.0f && o.getY() <= height)) {
            int x = (int) Math.floor(o.getX() / cellLength);
            int y = (int) Math.floor(o.getY() / cellLength);
            // Process objects at screen border
            x = (x >= totalRows ? x - 1 : x);
            y = (totalRows * y) + x >= cells.size ? y - 1 : y;
            cells.get((totalRows * y) + x).add(o);
        }
    }

    /**
     * Creates a new collection of cells for the given game objects.
     *
     * @param objects List of active objects
     */
    private void buildCells(Array<GameObject> objects) {
        // Clear previous cells
        for (int ii = 0; ii < totalCells; ii++) {
            cells.get(ii).clear();
        }

        // Add an object that is not destroyed
        for (GameObject o : objects) {
            if (!o.isDestroyed()) {
                add(o);
            }
        }
    }

    /**
     * Check for collisions in each cell.
     */
    public void processCells() {
        boolean toprow;
        boolean leftcol;

        for (int ii = 0; ii < totalCells; ii++) {
            toprow = ii < totalRows;
            leftcol = ii % totalRows == 0;

            Array.ArrayIterator<GameObject> iterator = new Array.ArrayIterator<>(cells.get(ii));
            while (iterator.hasNext()) {
                GameObject go = iterator.next();

                // Check box above and to the left
                if (!toprow && !leftcol) {
                    for (GameObject target : cells.get(ii - (totalRows + 1))) {
                        processCollision(go, target);
                    }
                }
                // Check box above
                if (!toprow) {
                    for (GameObject target : cells.get(ii - totalRows)) {
                        processCollision(go, target);
                    }
                }
                // Check box to the left
                if (!leftcol) {
                    for (GameObject target : cells.get(ii - 1)) {
                        processCollision(go, target);
                    }
                }
                // Check this box
                for (GameObject target : cells.get(ii)) {
                    // Objects cannot collide with themselves
                    if (!go.equals(target)) {
                        processCollision(go, target);
                    }
                }
            }
        }
    }

    public void processCollisions(Array<GameObject> objects) {
        buildCells(objects);
        processCells();

        for (GameObject object : objects){
            processBounds(object);
        }
    }

    /**
     * Detect and resolve collisions between two game objects
     *
     * @param o1 First object
     * @param o2 Second object
     */
    private void processCollision(GameObject o1, GameObject o2) {
        if (o1.isDestroyed() || o2.isDestroyed()) return;

        if (o1.getCollider().overlaps(o2.getCollider())){
            switch (o1.getType()) {
                case PLAYER:
                    handleCollision((Player) o1, o2);
                    break;
                case NIGHTMARE:
                    handleCollision((Nightmare)o1, o2);
                    break;
                case PHOTON:
                    handleCollision((Photon) o1, o2);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Check if a GameObject is out of bounds and take action.
     *
     * @param o      Object to check
     */
    private void processBounds(GameObject o) {
        switch (o.getType()) {
            case PLAYER:
                break;
            case PHOTON:
                handleBounds((Photon)o);
                break;
            case NIGHTMARE:
                handleBounds((Nightmare)o);
                break;
            default:
                break;
        }
    }

    private void handleBounds(Photon p) {
        // Destroy a photon once off-screen.
        if (p.getX() > width) {
            p.setDestroyed(true);
        }
    }

    private void handleBounds(Nightmare p) {
        // Destroy a nightmare once off-screen.
        Rectangle rect = p.getCollider();
        if (p.getX() + rect.width <= 0 || p.getX() > width) {
            p.setDestroyed(true);
        }
    }

    private void handleCollision(Player player, GameObject other) {
        if (other.getType() == GameObject.ObjectType.NIGHTMARE) {
            player.changeHealth(-1);
            other.setDestroyed(true);
        }
    }

    private void handleCollision(Nightmare nightmare, GameObject other) {
        if (other.getType() == GameObject.ObjectType.PLAYER) {
            ((Player) other).changeHealth(-1);
            nightmare.setDestroyed(true);
        }
    }

    private void handleCollision(Photon photon, GameObject other) {
        if (other.getType() == GameObject.ObjectType.NIGHTMARE) {
            other.setDestroyed(true);
            photon.setDestroyed(true);

        }
    }
}
