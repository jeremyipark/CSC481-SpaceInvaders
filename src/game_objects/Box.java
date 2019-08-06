package game_objects;

import java.io.Serializable;

import abstract_components.Collidable;
import abstract_components.Controllable;
import abstract_components.Movable;
import abstract_components.Renderable;
import abstract_components.Sizeable;
import concrete_components.CollisionComponent;
import concrete_components.ControlComponent;
import events.CollisionEvent;
import events.CoolDownEvent;
import events.DeathEvent;
import events.Event;
import events.EventHandler;
import events.EventManager;
import events.HealthUpdateEvent;
import events.ProjectileEvent;
import events.RemoveObjectEvent;
import events.SpawnEvent;
import scripting.ScriptManager;
import server.GameServer;
import server.Server;

/**
 * Character represents a square on the screen that the user can manipulate.
 * @author jeremypark
 *
 */
public class Box extends GameObject implements Serializable, EventHandler {
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
    Movable moveComponent;
    CollisionComponent collisionComponent;
    ControlComponent controlComponent;

    // if the box is falling
    boolean falling;

    private int spawn_x = 0;
    private int spawn_y = 0;

    // cool down flag
    private boolean coolDown = false;

    // cool down time
    private static final int COOL_DOWN_TIME = 50;

    //private int[] killers = new int[3];

    private boolean moving = false;

    /**
     * Make a box character
     * @param width
     * @param height
     */
    public Box (int GUID, Sizeable sizeComponent, Renderable renderComponent, Movable moveComponent, Collidable collisionComponent, Controllable controlComponent, String shipType) {
        super(GUID);
        this.renderComponent = renderComponent;
        this.sizeComponent = sizeComponent;
        this.moveComponent = moveComponent;
        this.controlComponent = (ControlComponent) controlComponent;
        this.collisionComponent = (CollisionComponent) collisionComponent;
        sizeComponent.setDimensions( this );
        super.setType( shipType );
        super.setLives( 10 );
    }

    /**
     * How the Box updates
     */
    public void update () {
        // Get a record of all of the game objects
        gameObjects = GameServer.getGameObjects();

        //move
        if (moving) {
            moveComponent.move(this);
        }

        // Check to see if you collide with any objects
        for (int i = 0; i < gameObjects.size(); i++) {
            // don't check against yourself
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
    }

    /**
     * Draw
     */
    @Override
    public void draw () {
        renderComponent.display( this );
    }

    /**
     * Keyboard input to go left
     */
    public void goLeft() {
        enableMoving();

        moveComponent.goLeft();
    }

    /**
     * Keyboard input to go right
     */
    public void goRight() {
        enableMoving();

        moveComponent.goRight();
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

    /**
     * Jump method
     * Taken from https://happycoding.io/tutorials/processing/collision-detection#snapping-to-an-edge
     */
    public void jump() {
        if ( !falling ) {
            moveComponent.setYSpeed( ControlComponent.JUMP_VALUE );
            falling = true;
        }
    }

    /**
     * Indicate whether the box is falling
     * @return
     */
    public boolean isFalling() {
        return falling;
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
            case "SPAWN":
                handleSpawn(e);
                break;
            case "PROJECTILE":
                handleProjectile(e);
                break;
            case "COOL_DOWN":
                handleCoolDown(e);
                break;
            default:
                System.out.println( "Invalid event type." );
        }

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
            // if you are not of the same type
            if (collision.getCollisionType() != type) {
                // raise death event
                Event death = new DeathEvent(EventManager.nextFrame(), EventManager.offset(), this.getGUID());

                // add death!
                EventManager.addEvent( death );
            }
        }
    }

    /**
     * Upon projectile
     * @param e event
     */
    public void handleProjectile(Event e) {
        // take the collision event
        ProjectileEvent projectile = (ProjectileEvent) e;

        // create the projectile
        if (projectile.getGUID() == this.getGUID()) {

            if (!coolDown) {
                GameServer.createProjectile( this.getGUID() );
            }

            // after firing, set the cool down time flag in the future!
            CoolDownEvent coolDownEvent = new CoolDownEvent(EventManager.nextFrame(), EventManager.offset() + COOL_DOWN_TIME, this.getGUID());
            EventManager.addEvent( coolDownEvent );
            coolDown = true;
        }
    }

    /**
     * Handle the cool down by setting the cool down flag back to false!
     * @param e event
     */
    public void handleCoolDown(Event e) {
        // take the collision event
        CoolDownEvent coolDownEvent = (CoolDownEvent) e;

        // create the projectile
        if (coolDownEvent.getParentGUID() == this.getGUID()) {
            if (coolDown) {
                coolDown = false;
            }
        }
    }

    /**
     * Spawn the character in a new place
     * @param e event
     */
    public void handleSpawn (Event e) {
        SpawnEvent spawn = (SpawnEvent) e;

        /**
         * Algorithm for loading, binding and executing taken from Dr. Roberts' ScriptIntegrationExample.java class.
         */

        // load the script
        ScriptManager.loadScript("src/scripting/spawn_handler.js");

        // give a Java object a Javascript name. Just renaming
        ScriptManager.bindArgument("spawn", spawn);
        ScriptManager.bindArgument("box", this);

        // you can send in parameters for your script
        ScriptManager.executeScript();
    }

    /**
     *  PUT THIS IN A SCRIPT
     *
     *  GO DARKER
     */

    public void handleDeath (Event e) {
        DeathEvent death = (DeathEvent) e;

        // if you died
        if (death.getGUID() == this.getGUID()) {
            super.die();

            // add the health update
            Event healthUpdate = new HealthUpdateEvent(EventManager.nextFrame(), EventManager.offset(), this.getGUID(), getLives());

            // add to event handler
            Server.addEvent( healthUpdate );

            if (lives <= 0) {
                // remove yourself from the list of game objects
                Event removeObject = new RemoveObjectEvent(EventManager.nextFrame(), EventManager.offset(), this.getGUID());

                // add to event handler
                Server.addEvent( removeObject );
            }
        }
    }

    /**
     * Set the moving flag when a key is pressed.
     */
    public void enableMoving() {
        if (!moving) {
            moving = true;
        }
    }

    /**
     * Disable the moving flag when a key is pressed.
     */
    public void disableMoving() {
        if (moving) {
            moving = false;
        }
    }

    public boolean isMoving() {
        return moving;
    }
}
