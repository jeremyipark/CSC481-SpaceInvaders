package events;

import game_objects.GameObject;

/**
 * Represents the entrance of a new player to the system.
 * @author jeremypark
 *
 */
public class NewObjectEvent extends Event {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    // GUID of the box
    private int GUID;

    // box
    private GameObject obj;

    public NewObjectEvent(long timeStamp, long timeToHandle, int GUID, GameObject obj) {
        setTimeStamp(timeStamp);
        setTimeToHandle(timeToHandle);
        setGUID( GUID );
        setObj( obj );
        setType("NEW_OBJECT");
        setPriority(0);
    }

    /**
     * @return the gUID
     */
    public int getGUID () {
        return GUID;
    }

    /**
     * @param gUID the gUID to set
     */
    private void setGUID ( int gUID ) {
        GUID = gUID;
    }

    /**
     * @return the obj
     */
    public GameObject getObj () {
        return obj;
    }
    /**
     * @param obj the obj to set
     */
    private void setObj ( GameObject obj ) {
        this.obj = obj;
    }
}
