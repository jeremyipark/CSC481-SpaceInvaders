package client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

import display.Screen;
import events.Event;
import events.EventHandler;
import events.EventManager;
import events.HealthUpdateEvent;
import events.KeyPressEvent;
import events.NewObjectEvent;
import events.PositionUpdateEvent;
import events.RemoveObjectEvent;
import events.Replay;
import game_objects.GameObject;
import game_objects.GameObjectList;
import time.LocalTimeline;
import time.Timeline;


/**
 * This class represents a client of the server.
 * Communication occurs via Sockets and a ServerSocket.
 *
 * The client code was inspired by Dr. Roberts' Client.java class.
 *
 * @author jeremypark
 *
 */
public class Client implements Runnable, EventHandler {
    // localhost
    private static final String ipAddress = "127.0.0.1";

    // port number to connect to
    private static final int portNumber = 9001;

    // list of all the game objects in the system.
    private GameObjectList gameObjects;

    // object i/o streams
    private static ObjectInputStream input = null;
    private static ObjectOutputStream output = null;

    // socket connection
    private static Socket s = null;

    // queue of events raised by client
    private static ArrayBlockingQueue<KeyPressEvent> eventQueue;

    // local variable to hold the replay
    private volatile Replay replay = null;

    // flag for recording
    private volatile boolean recording = false;

    // flag for replaying
    private volatile boolean replaying = false;

    /**
     * Client class to handle client information
     * @param events queue
     */
    public Client (ArrayBlockingQueue<KeyPressEvent> events) {
        eventQueue = events;

        try {
            // new socket connection
            s = new Socket(ipAddress, portNumber);

            // get i/o streams
            output = new ObjectOutputStream(s.getOutputStream());  // create output stream
            input = new ObjectInputStream(s.getInputStream()); // create input stream

            // register client with these events!
            EventManager.register( this, "POSITION" );
            EventManager.register( this, "NEW_OBJECT" );
            EventManager.register( this, "REMOVE_OBJECT" );
            EventManager.register( this, "HEALTH" );

            // I'm gonna try to get stuff from the server
            gameObjects = (GameObjectList) input.readObject();
            Screen.newGameWorld( gameObjects );

            // get the GUID from the server
            int guid = (int) input.readObject();
            ClientWorld.setGUID( guid );
            Screen.setActivePlayer( guid );

            // get the local timeline
            Timeline localTimeline = (LocalTimeline) input.readObject();
            ClientWorld.setTimeline(localTimeline);

            // start the client thread to accept user input.
            (new Thread(this)).start();

            // continuously send user input to the server
            while(true)
            {
                // get an event from local event queue
                KeyPressEvent update = eventQueue.take();

                // send it out to the server
                output.writeObject( update );
            }
        } catch (Exception e) {
            System.out.println(e.toString());

        } finally {
            // upon exit
            try {
                KeyPressEvent update = new KeyPressEvent("QUIT", ClientWorld.getGUID());
                output.writeObject( update );
                input.close();
                output.close();
                s.close();
            }
            catch (Exception e) {
            }
        }
    }

    /**
     * Thread to accept position updates from the server
     */
    @Override
    public void run () {
        while ( true ) {
            try {
                // get an event from the server
                Event event = (Event) input.readObject();

                // time stamp it with the local time
                event.setTimeStamp( EventManager.nextFrame());

                // give it the time stamp of the next frame
                event.setTimeToHandle( EventManager.offset());

                // add it to the event handler
                EventManager.addEvent( event );
            }
            catch ( Exception e ) {
                System.out.println( "I'M BREAKING" );
            }
        }
    }

    /**************** EVENT HANDLING ****************/

    /**
     * General purpose helper method for all event types
     */
    public void onEvent ( Event e ) {
        switch (e.type) {
            case "POSITION":
                handlePositionUpdate(e);
                break;

            case "NEW_OBJECT":
                addNewObject(e);
                break;

            case "REMOVE_OBJECT":
                removeObject(e);
                break;

            case "HEALTH":
                updateHealth(e);
                break;

            default:
                System.out.println( "Invalid event type." );
        }
    }

    /**
     * Private helper method to update the health
     * @param newObjectEvent new object event
     */
    private void updateHealth(Event event) {
        HealthUpdateEvent healthUpdateEvent = (HealthUpdateEvent) event;

        // if you're getting your own health update
        if (healthUpdateEvent.getGUID() == ClientWorld.getGUID()) {
            // get the box
            GameObject box = gameObjects.getByGUID(healthUpdateEvent.getGUID());

            if (box != null) {
                box.setLives( healthUpdateEvent.getHealth() );
            }
        }
    }

    /**
     * Private helper method to add new objects to the game
     * @param newObjectEvent new object event
     */
    private void addNewObject(Event event) {
        NewObjectEvent newObjectEvent = (NewObjectEvent) event;

        GameObject newObject = newObjectEvent.getObj();

        // add it to your local list
        gameObjects.add( newObject );

        // update your local list
        Screen.newGameWorld( gameObjects );
    }

    /**
     * Private helper method to remove objects
     * @param removeObjectEvent event with object's GUID to remove
     */
    private void removeObject(Event event) {
        RemoveObjectEvent removeObjectEvent = (RemoveObjectEvent) event;

        // remove it to your local list
        gameObjects.removeByGUID( removeObjectEvent.getGUID()  );

        // update your local list
        Screen.newGameWorld( gameObjects );
    }

    /**
     * Update the position by getting the GUID of the game object and its new position
     * @param e position update event
     */
    public void handlePositionUpdate(Event e) {
        // take the collision event
        PositionUpdateEvent positionUpdate = (PositionUpdateEvent) e;

        if (positionUpdate != null) {
            GameObject obj = gameObjects.getByGUID( positionUpdate.getGUID() );

            // update position
            if (obj != null) {
                obj.x = positionUpdate.getX();
                obj.y = positionUpdate.getY();
            }
        }
    }
}
