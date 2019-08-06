package events;

/**
 * Represents a player exiting the game.
 *
 * This event holds the GUID of the character that is exiting so that all clients can remove them from their local list.
 *
 * @author jeremypark
 *
 */
public class RemoveObjectEvent extends Event {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    // GUID of the box
    private int GUID;

    public RemoveObjectEvent(long timeStamp, long timeToHandle, int GUID) {
        setTimeStamp(timeStamp);
        setTimeToHandle(timeToHandle);
        setGUID( GUID );
        setType("REMOVE_OBJECT");
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
}
