package events;

/**
 * Represents the entrance of a new player to the system.
 * @author jeremypark
 *
 */
public class HealthUpdateEvent extends Event {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    // GUID of the box
    private int GUID;

    // health level of the box
    private int health;

    public HealthUpdateEvent(long timeStamp, long timeToHandle, int GUID, int health) {
        setTimeStamp(timeStamp);
        setTimeToHandle(timeToHandle);
        setGUID( GUID );
        setType("HEALTH");
        setPriority(4);
        setHealth(health);
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
     * @return the health
     */
    public int getHealth () {
        return health;
    }

    /**
     * @param health the health to set
     */
    public void setHealth ( int health ) {
        this.health = health;
    }
}
