package game_objects;

import java.io.Serializable;

import abstract_components.Collidable;
import abstract_components.Movable;
import abstract_components.Renderable;
import abstract_components.Sizeable;
import concrete_components.CollisionComponent;
import concrete_components.MoveComponent;
import display.Screen;
import events.CollisionEvent;
import events.Event;
import events.EventHandler;
import events.EventManager;
import events.RemoveObjectEvent;
import server.Server;

/**
 * A projectile that is shot across the screen by both the mothership and enemy ships.
 *
 * @author jeremypark
 *
 */
public class Projectile extends GameObject implements Serializable, EventHandler {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    // list of game objects, so that they can be checked for collisions
    private GameObjectList gameObjects;

    /**
     * Components
     */
    Sizeable sizeComponent;
    Renderable renderComponent;
    MoveComponent moveComponent;
    CollisionComponent collisionComponent;

    // ship type
    private String shipType;

    // if the box is falling
    boolean falling;

    // projectile needs to know where it came from
    private int parentGUID;

    /**
     * Make a projectile
     * @param width
     * @param height
     */
    public Projectile (int GUID, int parentGUID, String shipType, Sizeable sizeComponent, Renderable renderComponent, Movable moveComponent, Collidable collisionComponent) {
        super(GUID);
        super.setType( shipType );
        this.renderComponent = renderComponent;
        this.sizeComponent = sizeComponent;
        this.moveComponent = (MoveComponent) moveComponent;
        this.collisionComponent = (CollisionComponent) collisionComponent;
        sizeComponent.setDimensions( this );
        this.setParentGUID( parentGUID );
        super.setLives( 1 );
    }

    /**
     * Update for projectile
     */
    public void update () {
        // Get a record of all of the game objects
        gameObjects = Screen.gameObjects;

        moveComponent.projectileMove( this );


        // Check to see if you collide with any objects
        for (int i = 0; i < gameObjects.size(); i++) {

            // don't check against yourself
            // don't check against the box that shot you
            if (i + 1 != this.getGUID()) {
                GameObject potentialObstacle = gameObjects.get( i );

                // create a collision event (it can be null though).
                Event collision = collisionComponent.checkOtherCollisions( this, potentialObstacle );

                // if you are colliding with something
                if (this != potentialObstacle && collision != null) {
                    // if it is colliding, add the collision event to the queue
                    EventManager.addEvent( collision );

                    break;
                }
            }
        }

        //render
        renderComponent.display( this );

        if (y > 1000 || y < -100) {
            // raise death event
            Event removeObject = new RemoveObjectEvent(EventManager.nextFrame(), EventManager.offset(), this.getGUID());

            // add death!
            Server.addEvent( removeObject );
        }
    }

    /**
     * Draw
     */
    @Override
    public void draw () {
        renderComponent.display( this );
    }

    /**
     * set x speed
     * @param xSpeed new x speed
     */
    public void setXSpeed(int xSpeed) {
        moveComponent.setXSpeed( xSpeed );
    }

    /**
     * set y speed
     * @param ySpeed new y speed
     */
    public void setYSpeed(int ySpeed) {
        moveComponent.setYSpeed( ySpeed );
    }

    /**
     * Get x speed
     * @return x speed
     */
    public int getXSpeed() {
        return moveComponent.xSpeed;
    }

    /**
     * Get y speed
     * @return y speed
     */
    public int getYSpeed() {
        return moveComponent.ySpeed;
    }

    /**************** EVENT HANDLING ****************/

    /**
     * General purpose helper method for all event types
     */
    public void onEvent ( Event e ) {
        switch (e.type) {
            case "COLLISION":
                handleCollision(e);
                break;
            case "DEATH":
                handleDeath(e);
                break;
            default:
                System.out.println( "Invalid event type." );
        }
    }

    /**
     * Projectile collided, remove itself from the list of game objects
     * @param e
     */
    public void handleDeath(Event e) {
        /**
         * Erase yourself from the list of game objects
         */
    }


    /**
     * Upon collision
     * @param e event
     */
    public void handleCollision(Event e) {
        // take the collision event
        CollisionEvent collision = (CollisionEvent) e;

        // check if you are interested
        if (collision.getGUID() == this.getGUID()) {
            if (collision.getCollisionType() != type) {

                super.die();

                // raise death event
                Event removeObject = new RemoveObjectEvent(EventManager.nextFrame(), EventManager.offset() + 5, this.getGUID());

                // add death!
                Server.addEvent( removeObject );
            }
        }
    }

    /**
     * @return the shipType
     */
    public String getShipType () {
        return shipType;
    }

    /**
     * @param shipType the shipType to set
     */
    public void setShipType ( String shipType ) {
        this.shipType = shipType;
    }

    /**
     * @return the parentGUID
     */
    public int getParentGUID () {
        return parentGUID;
    }

    /**
     * @param parentGUID the parentGUID to set
     */
    public void setParentGUID ( int parentGUID ) {
        this.parentGUID = parentGUID;
    }
}
