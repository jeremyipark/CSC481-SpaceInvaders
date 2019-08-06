package concrete_components;

import java.io.Serializable;

import abstract_components.Movable;
import display.Screen;
import game_objects.GameObject;
import scripting.ScriptManager;

/**
 * MoveComponent allows the GameObject to move.
 *
 * I read this Game Programming Patterns textbook chapter to help me design each concrete component:
 * http://gameprogrammingpatterns.com/component.html
 *
 * Specifically, the idea of having a concrete component with methods to change game object data is very helpful.
 *
 * @author jeremypark
 *
 */
public class MoveComponent extends Movable implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    // Screen to move on
    private transient Screen screen = null;

    // default box speed
    private static final int DEFAULT_BOX_SPEED = 7;

    private int upper_bound = 150;
    private int lower_bound = 450;
    private int screen_width = 800;
    public volatile int distanceTravelled = 0;

    private volatile boolean hasStarted = false;

    /**
     * Set X speed
     * @param xSpeed in x direction
     * @param ySpeed in y direction
     */
    public MoveComponent(int xSpeed, int ySpeed) {
        setXSpeed(xSpeed);
        setYSpeed(ySpeed);
    }

    /**
     * Move behavior for box
     * @param box to move
     */
    public void move (GameObject obj) {
        // get the width of the screen
        if (screen == null) {
            screen = Screen.getScreen();
            screen_width = screen.width;
        }

        /**
         * Algorithm for loading, binding and executing taken from Dr. Roberts' ScriptIntegrationExample.java class.
         */

        // load the script
        ScriptManager.loadScript("src/scripting/move_mothership.js");

        // bind
        ScriptManager.bindArgument("xSpeed", xSpeed);
        ScriptManager.bindArgument("obj", obj);
        ScriptManager.bindArgument("screen_width", screen_width);

        // execute script
        ScriptManager.executeScript();
    }

    /**
     * Movement behavior for projectiles.
     * @param obj projectile to move
     */
    public void projectileMove (GameObject obj) {
        /**
         * Algorithm for loading, binding and executing taken from Dr. Roberts' ScriptIntegrationExample.java class.
         */

        // load the script
        ScriptManager.loadScript("src/scripting/move_projectile.js");

        // give a Java object a Javascript name. Just renaming
        ScriptManager.bindArgument("obj", obj);

        // you can send in parameters for your script
        ScriptManager.executeScript();
    }

    /**
     * Movement behavior for projectiles.
     * @param obj projectile to move
     */
    public void enemyShipMove (GameObject obj) {
        if (Screen.getActivePlayer() != 0) {
            hasStarted = true;
        }

        /**
         * Algorithm for loading, binding and executing taken from Dr. Roberts' ScriptIntegrationExample.java class.
         */

        // load the script
        ScriptManager.loadScript("src/scripting/move_enemy.js");

        // give a Java object a Javascript name. Just renaming
        ScriptManager.bindArgument("obj", obj);
        ScriptManager.bindArgument("screen_width", screen_width);
        ScriptManager.bindArgument("moveComponent", this);
        ScriptManager.bindArgument("upper_bound", upper_bound);
        ScriptManager.bindArgument("lower_bound", lower_bound);
        ScriptManager.bindArgument("distanceTravelled", distanceTravelled);
        ScriptManager.bindArgument("hasStarted", hasStarted);


        // you can send in parameters for your script
        ScriptManager.executeScript();
    }

    /**
     * Keyboard input to go left
     */
    public void goLeft() {
        xSpeed = Math.abs( DEFAULT_BOX_SPEED ) * (-1);  //go left
    }

    /**
     * Keyboard input to go right
     */
    public void goRight() {
        xSpeed = Math.abs( DEFAULT_BOX_SPEED );  //go right
    }
}
