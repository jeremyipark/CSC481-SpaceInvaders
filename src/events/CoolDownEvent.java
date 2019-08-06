package events;

/**
 * Represents an event that will set the cool down timer for a gun.
 *
 * Holds the GUID of the game object shot it so that it can set its cooldown time.
 *
 * @author jeremypark
 *
 */
public class CoolDownEvent extends Event {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    // GUID of the parent
    private int parentGUID;

    public CoolDownEvent(long timeStamp, long timeToHandle, int parentGUID) {
        setTimeStamp(timeStamp);
        setTimeToHandle(timeToHandle);
        setParentGUID(parentGUID);
        setType("COOL_DOWN");
        setPriority(6);
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
