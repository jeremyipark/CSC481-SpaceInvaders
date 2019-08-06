package events;

/**
 * Represents a collision event.
 *
 * Holds the GUID of the game object that collided and on which side.
 *
 * @author jeremypark
 *
 */
public class CollisionEvent extends Event {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private int GUID;
    private String collisionType;

    public CollisionEvent(long timeStamp, long timeToHandle, int GUID, String collisionType) {
        setTimeStamp(timeStamp);
        setTimeToHandle(timeToHandle);
        setGUID(GUID);
        setCollisionType(collisionType);
        setType("COLLISION");
        setPriority(1);
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
     * @return the direction
     */
    public String getType () {
        return type;
    }
    /**
     * @param direction the direction to set
     */
    private void setCollisionType ( String collisionType ) {
        this.collisionType = collisionType;
    }

    /**
     * Get collision type
     * @return
     */
    public String getCollisionType() {
        return collisionType;
    }
}
