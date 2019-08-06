package events;

/**
 * Represents a projectile event
 *
 * Holds the GUID of the game object shot it too.
 *
 * @author jeremypark
 *
 */
public class ProjectileEvent extends Event {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    // GUID of the projectile itself
    private int GUID;

    // GUID of the parent
    private int parentGUID;

    public ProjectileEvent(long timeStamp, long timeToHandle, int GUID, int parentGUID) {
        setTimeStamp(timeStamp);
        setTimeToHandle(timeToHandle);
        setGUID(GUID);
        setParentGUID(parentGUID);
        setType("PROJECTILE");
        setPriority(5);
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
     * @return the gUID
     */
    public int getParentGUID () {
        return parentGUID;
    }
    /**
     * @param gUID the gUID to set
     */
    private void setParentGUID ( int parentGUID ) {
        this.parentGUID = parentGUID;
    }

}
