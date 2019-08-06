package client;

import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import display.Screen;
import events.Event;
import events.EventManager;
import events.KeyPressEvent;
import game_objects.GameObject;
import processing.core.PApplet;
import time.Timeline;

/**
 * ClientWorld represents the screen that the user will see and control.
 * @author jeremypark
 *
 */
public class ClientWorld extends Screen {
    // Client class to represent the client network connection
    static Client client;

    // queue of events created by user input
    private static ArrayBlockingQueue<KeyPressEvent> eventQueue = new ArrayBlockingQueue<KeyPressEvent>(1024);

    // GUID of box user controls
    private static int GUID = 0;

    // replay queue
    public static LinkedList<Event> replayQueue = new LinkedList<Event>();

    // player's frame delta
    private static final int PLAYER_FRAME_DELTA = 1;

    // start time
    private long startTime = 0;

    // end time
    private long elapsedTime = 0;

    /**
     * Draw screen
     * @param args
     */
    public static void main(String[] args) {
        PApplet.main("client.ClientWorld");

        // make a new client, give it the event queue
        client = new Client(eventQueue);
    }

    public void setup() {
        super.setup();
    }

    /**
     * Draws every box on the screen.
     *
     * Inspired by: https://www.youtube.com/watch?v=GY-c2HO2liA&t=450s
     */
    @Override
    public void draw() {
        // Calculate the time elapsed since the last game loop began.
        if (timeline != null) {
            startTime = timeline.getTime();
            elapsedTime = startTime - lastIterationTime;
            lastIterationTime = startTime;
        }

        // handle the events
        EventManager.handleEvents();

        // draw
        if (timeline != null && !timeline.isPaused()) {
            drawBackground();

            for (int i = 0; i < gameObjects.size(); i++) {
                GameObject obj = gameObjects.get( i );

                obj.draw();
            }
        }

        // Frame Rate Governing: Sleep for the remainder of the frame.
        if ( elapsedTime < PLAYER_FRAME_DELTA ) {
            try {
                TimeUnit.MILLISECONDS.sleep( (PLAYER_FRAME_DELTA - elapsedTime) );
            }
            catch ( InterruptedException e ) {
            }
        }

        if (GUID != 0) {
            fill(255,255,255);
            textSize(20);

            if (gameObjects.getByGUID( GUID ) != null) {
                text("HP: " + gameObjects.getByGUID( GUID ).getLives(), 700, 50);
            }
        }
    }

    /**
     * Get GUID of box user controls
     * @return GUID of box user controls
     */
    public static int getGUID() {
        return GUID;
    }

    /**
     * Set GUID of box user controls
     * @param newGuid of box user controls
     */
    public static void setGUID(int newGuid) {
        GUID = newGuid;
    }

    /**
     * Set the timeline!
     * @param tl timeline
     */
    public static void setTimeline (Timeline tl) {
        // start up the client's timeline
        timeline = tl;
        timeline.start();

        // start up the event manager's timeline
        // a tic is a frame
        EventManager.eventTimeline.anchorTimeline(timeline);
        EventManager.eventTimeline.setTicSize( 1 );
        EventManager.eventTimeline.start();
    }

    /**
     * Interpret user keyboard input
     *
     * Let Processing keyPressed be signal to create an UpdateEvent
     * Put UpdateEvent on event queue
     *
     */
    public void keyPressed() {
        if (key == CODED) {
            if (keyCode == LEFT) {
                KeyPressEvent left = new KeyPressEvent("LEFT", GUID);

                try {
                    // if (!replaying)
                    if (!timeline.isPaused()) {
                        eventQueue.put(left);
                    }
                }
                catch ( Exception e ) {
                    e.printStackTrace();
                }

            } else if (keyCode == RIGHT) {
                KeyPressEvent right = new KeyPressEvent("RIGHT", GUID);

                try {
                    if (!timeline.isPaused()) {
                        eventQueue.put(right);
                    }
                }
                catch ( Exception e ) {
                    e.printStackTrace();
                }
            }

        } else if (keyCode == 32) { // if space
            //SHOOT
            KeyPressEvent projectile = new KeyPressEvent("PROJECTILE", GUID);

            try {
                if (!timeline.isPaused()) {
                    eventQueue.put(projectile);
                }
            }
            catch ( Exception e ) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Interpret user keyboard input
     *
     * Let Processing keyPressed be signal to create an UpdateEvent
     * Put UpdateEvent on event queue
     *
     */
    public void keyReleased() {
        if (key == CODED) {
            if (keyCode == LEFT) {
                KeyPressEvent left = new KeyPressEvent("LEFT_RELEASED", GUID);

                try {
                    // if (!replaying)
                    if (!timeline.isPaused()) {
                        eventQueue.put(left);
                    }
                }
                catch ( Exception e ) {
                    e.printStackTrace();
                }

            } else if (keyCode == RIGHT) {
                KeyPressEvent right = new KeyPressEvent("RIGHT_RELEASED", GUID);

                try {
                    if (!timeline.isPaused()) {
                        eventQueue.put(right);
                    }
                }
                catch ( Exception e ) {
                    e.printStackTrace();
                }
            }
        }
    }
}
