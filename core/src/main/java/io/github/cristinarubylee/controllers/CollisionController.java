package io.github.cristinarubylee.controllers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.*;
import io.github.cristinarubylee.models.*;

public class CollisionController implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        Object userDataA = fixtureA.getBody().getUserData();
        Object userDataB = fixtureB.getBody().getUserData();

        // Check to see if the userData objects are instances of GameObject
        if (userDataA instanceof GameObject && userDataB instanceof GameObject){
            GameObject gameObjectA = (GameObject) userDataA;
            GameObject gameObjectB = (GameObject) userDataB;

            GameObject.ObjectType typeA = gameObjectA.getType();
            GameObject.ObjectType typeB = gameObjectB.getType();

//            System.out.println("Contact between fixtures: " + fixtureA + " and " + fixtureB);
//            System.out.println("Fixture " + typeA + " at " + gameObjectA.getX() + ", " + gameObjectA.getY() );
//            System.out.println("Fixture " + typeB + " at " + gameObjectB.getX() + ", " + gameObjectB.getY() );
//            Vector2 point = contact.getWorldManifold().getPoints()[0];
//            System.out.println("Contact point: " + point + "\n");

            switch (typeA) {
                case PLAYER:
                    switch (typeB){
                        case PLAYER:
                        case PHOTON:
                            break;
                        case NIGHTMARE:
                            int damage = ((Nightmare)gameObjectB).getDamage();
                            gameObjectB.setDestroyed(true);
                            ((Player)gameObjectA).changeHealth(-damage);
                            break;
                    }
                    break;

                case NIGHTMARE:
                    switch (typeB){
                        case PLAYER:
                            int damage = ((Nightmare)gameObjectA).getDamage();
                            gameObjectA.setDestroyed(true);
                            ((Player)gameObjectB).changeHealth(-damage);
                            break;
                        case NIGHTMARE:
                            break;
                        case PHOTON:
                            gameObjectB.setDestroyed(true);
                            gameObjectA.setDestroyed(true);
                            break;
                    }
                    break;

                case PHOTON:
                    switch (typeB) {
                        case PLAYER:
                        case PHOTON:
                            break;
                        case NIGHTMARE:
                            gameObjectB.setDestroyed(true);
                            gameObjectA.setDestroyed(true);
                    }
                    break;
            }
        }
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {

    }
}
