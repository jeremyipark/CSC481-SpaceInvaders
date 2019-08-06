package server;

import java.util.Random;

import abstract_components.Collidable;
import abstract_components.Controllable;
import abstract_components.Movable;
import abstract_components.Renderable;
import abstract_components.Sizeable;
import concrete_components.CollisionComponent;
import concrete_components.ControlComponent;
import concrete_components.DrawComponent;
import concrete_components.MoveComponent;
import concrete_components.SizeComponent;
import display.Screen;
import events.EventManager;
import events.NewObjectEvent;
import game_objects.Box;
import game_objects.DeathZone;
import game_objects.EnemyShip;
import game_objects.GameObject;
import game_objects.GameObjectList;
import game_objects.MovingPlatform;
import game_objects.Projectile;
import game_objects.SpawnPoint;
import game_objects.Terrain;
import processing.core.PApplet;
import time.RealTimeline;

/**
 * GameServer starts up the server for client connections and will add new players to the game.
 * @author jeremypark
 *
 */
public class GameServer extends Screen {
    // Server that accept clients, updates Screen with new information
    static Server server;

    // Colors for game world
    private final int TERRAIN_COLOR = color(150,212,123);
    private final int STATIC_PLATFORM_COLOR = color(84,64,34);
    private final int MOVING_PLATFORM_COLOR = color(200,100,50);
    private static int PROJECTILE_COLOR = 0;
    private static int MOTHERSHIP_COLOR = 0;
    private static int ENEMY_COLOR = 0;

    /**
     * Create the PApplet
     * Start up the server to accept clients
     * @param args console
     */
    public static void main(String[] args) {
        PApplet.main("server.GameServer");

        server = new Server();
    }

    /**
     * Set up environment
     */
    @Override
    public void setup() {
        // Set up master screen (Singleton pattern)
        super.setup();

        // start a real timeline
        // the global game world is based on a conception of real time
        // measured in milliseconds
        timeline = new RealTimeline();
        timeline.setTicSize( 1 );
        timeline.start();

        // start up the event manager's timeline
        // a tic is a frame
        EventManager.eventTimeline.anchorTimeline(Screen.timeline);
        EventManager.eventTimeline.setTicSize( Screen.FRAME_DELTA );
        EventManager.eventTimeline.start();

        /**
         * Creating colors in setup since Processing functions cannot be called statically.
         */
        PROJECTILE_COLOR = color(135,0,0);
        MOTHERSHIP_COLOR = color(46,139,87);
        ENEMY_COLOR = color(83,104,120);

        // load everything into the game world
        loadGameEnvironment();
    }

    /**
     * Draw all of the game world elements!
     */
    public void loadGameEnvironment() {
        createEnemyShip();

        // Set up terrain
        //createTerrain();

        // Set up death zone
        //createDeathZone();

        // Set up static platforms
        //createStaticPlatforms();

        // Set up moving platforms
        //createMovingPlatforms();

        // Set up spawn points
        //createSpawnPoints();
    }

    /**
     * Create terrain
     * Set its render, size components
     * Put the terrain on the map
     * Add it to the list of game objects
     *
     * Referred to Game Programming Patterns chapter in the header.
     */
    public void createTerrain() {
        Renderable terrainDraw = new DrawComponent(TERRAIN_COLOR);
        Sizeable terrainSize = new SizeComponent(300,200);

        GameObject terrain = new Terrain(newGUID(), terrainSize, terrainDraw);
        terrain.setLocation(0, 600);

        GameObject terrain2 = new Terrain(newGUID(), terrainSize, terrainDraw);
        terrain2.setLocation(500, 600);

        gameObjects.add( terrain );
        gameObjects.add( terrain2 );
    }

    /**
     * Create terrain
     * Set its render, size components
     * Put the terrain on the map
     * Add it to the list of game objects
     *
     * Referred to Game Programming Patterns chapter in the header.
     */
    public void createDeathZone() {
        Sizeable deathZoneSize = new SizeComponent(800,100);

        GameObject deathZone = new DeathZone(newGUID(), deathZoneSize);
        deathZone.setLocation(0, height);

        gameObjects.add( deathZone );
    }

    /**
     * Create static platforms
     * Set its render, size components
     * Put the terrain on the map
     * Add it to the list of game objects
     *
     * Referred to Game Programming Patterns chapter in the header.
     */
    public void createStaticPlatforms() {
        Renderable staticPlatformDraw = new DrawComponent(STATIC_PLATFORM_COLOR);
        Sizeable staticPlatformSize = new SizeComponent(200, 25);

        GameObject staticPlatform1 = new Terrain(newGUID(), staticPlatformSize, staticPlatformDraw);
        staticPlatform1.setLocation(300, 150);

        GameObject staticPlatform2 = new Terrain(newGUID(), staticPlatformSize, staticPlatformDraw);
        staticPlatform2.setLocation(300, 450);

        gameObjects.add( staticPlatform1 );
        gameObjects.add( staticPlatform2 );
    }

    /**
     * Create static platforms
     * Set its render, size components
     * Put the terrain on the map
     * Add it to the list of game objects
     *
     * Referred to Game Programming Patterns chapter in the header.
     */
    public void createMovingPlatforms() {
        Renderable movingPlatformDraw = new DrawComponent(MOVING_PLATFORM_COLOR);
        Sizeable movingPlatformSize = new SizeComponent(150, 25);
        Movable platformMove = new MoveComponent(0, 3);

        GameObject movingPlatform1 = new MovingPlatform(newGUID(), movingPlatformSize, movingPlatformDraw, platformMove);
        movingPlatform1.setLocation(150, 375);

        GameObject movingPlatform2 = new MovingPlatform(newGUID(), movingPlatformSize, movingPlatformDraw, platformMove);
        movingPlatform2.setLocation(500, 375);

        gameObjects.add( movingPlatform1 );
        gameObjects.add( movingPlatform2 );
    }

    /**
     * Add the spawn points to the map
     */
    public void createSpawnPoints() {
        GameObject sp1 = new SpawnPoint(newGUID(), 0, 0);
        GameObject sp2 = new SpawnPoint(newGUID(), 0, 500);
        GameObject sp3 = new SpawnPoint(newGUID(), 700, 0);
        GameObject sp4 = new SpawnPoint(newGUID(), 700, 500);

        gameObjects.add( sp1 );
        gameObjects.add( sp2 );
        gameObjects.add( sp3 );
        gameObjects.add( sp4 );

        spawnPoints.add( (SpawnPoint) sp1 );
        spawnPoints.add( (SpawnPoint) sp2 );
        spawnPoints.add( (SpawnPoint) sp3 );
        spawnPoints.add( (SpawnPoint) sp4 );
    }

    /**
     * Add a game object to the list
     * @param obj new game object
     */
    public static void addPlayer(GameObject obj) {
        gameObjects.add( obj );
    }

    /**
     * Get a random spawn point
     * @return random spawn point
     */
    public static SpawnPoint getRandomSpawnPoint() {
        Random random = new Random();
        int spawnPointNumber = random.nextInt(spawnPoints.size());
        return spawnPoints.get( spawnPointNumber );
    }

    /**
     * Create user box
     * Set its render, size, move components
     * Place the character on the map
     * Set its controllability for this screen
     * Add it to the list of game objects
     *
     * Referred to Game Programming Patterns chapter in the header of GameWorld.java.
     */
    public static Box createUserBox() {
        Renderable characterDraw = new DrawComponent(MOTHERSHIP_COLOR);
        Sizeable characterSize = new SizeComponent(100,100);
        Movable characterMove = new MoveComponent(5, 0);
        Controllable characterControl = new ControlComponent();
        Collidable characterCollision = new CollisionComponent();

        int guid = newGUID();
        Box character = new Box(guid, characterSize, characterDraw, characterMove, characterCollision, characterControl, "MOTHERSHIP");
        character.setLocation( 0, 600 );

        // register this game object with the event manager.
        EventManager.register( character, "COLLISION" );
        EventManager.register( character, "SPAWN" );
        EventManager.register( character, "DEATH" );
        EventManager.register( character, "PROJECTILE" );
        EventManager.register( character, "COOL_DOWN" );

        gameObjects.add( character );
        Server.addMover( character );
        Screen.setActivePlayer(guid);

        return character;
    }

    /**
     * Create enemy ship
     * Set its render, size, move components
     * Place the character on the map
     * Add it to the list of game objects
     *
     * Referred to Game Programming Patterns chapter in the header of GameWorld.java.
     */
    public static void createEnemyShip() {
        for (int row = 0; row < 2; row++) {
            for (int column = 0; column < 3; column++) {
                Renderable characterDraw = new DrawComponent(ENEMY_COLOR);
                Sizeable characterSize = new SizeComponent(100,100);
                Movable characterMove = new MoveComponent(5, 0);
                Collidable characterCollision = new CollisionComponent();

                int guid = newGUID();

                EnemyShip enemyShip = new EnemyShip(guid, characterSize, characterDraw, characterMove, characterCollision, "ENEMY_SHIP");
                enemyShip.setLocation( (column * 200), (row * 150) );

                // register this game object with the event manager.
                EventManager.register( enemyShip, "COLLISION" );
                EventManager.register( enemyShip, "SPAWN" );
                EventManager.register( enemyShip, "DEATH" );
                EventManager.register( enemyShip, "PROJECTILE" );
                EventManager.register( enemyShip, "COOL_DOWN" );

                // add to the list of game objects and movers.
                gameObjects.add( enemyShip );
                Server.addMover( enemyShip );
            }
        }
    }

    /**
     * Create projectile
     * Set its render, size, move components
     * Add it to the list of game objects
     */
    public static void createProjectile(int parentGUID) {
        Sizeable projectileSize = new SizeComponent(10,10);
        Renderable projectileDraw = new DrawComponent(PROJECTILE_COLOR);
        Movable projectileMove = new MoveComponent(5, 0);
        Collidable projectileCollision = new CollisionComponent();

        int guid = newGUID();

        // Get the parent's x and y
        GameObject parent = gameObjects.getByGUID( parentGUID );

        // glitch?
        if (parent != null) {
            Projectile projectile = new Projectile(guid, parentGUID, parent.getType(), projectileSize, projectileDraw, projectileMove, projectileCollision);

            // create it at the middle way of the ship dependent on ship type!
            if (projectile.type.equals( "MOTHERSHIP" )) {
                projectile.setLocation( parent.x + (parent.width / 2) - (projectile.width / 2), parent.y - projectile.height);
            } else if (projectile.type.equals( "ENEMY_SHIP" )) {
                projectile.setLocation( parent.x + (parent.width / 2) - (projectile.width / 2), parent.y + parent.height);
            }

            gameObjects.add( projectile );

            // register this game object with the event manager.
            EventManager.register( projectile, "COLLISION" );
            EventManager.register( projectile, "DEATH" );

            NewObjectEvent newPlayerEvent = new NewObjectEvent(EventManager.nextFrame(), EventManager.offset(), guid, projectile);
            Server.addEvent( newPlayerEvent );
            Server.addMover( projectile );
        }
    }


    /**
     * Create a new game object
     * @return new guid
     */
    public static int newGUID() {
        return ++guidCount;
    }

    /**
     * Get current GUID
     * @return
     */
    public static int getCurrentGUID() {
        return guidCount;
    }

    /**
     * Get a list of all of the game objects in the system.
     * @return list of game objects
     */
    public synchronized static GameObjectList getGameObjects() {
        return gameObjects;
    }
}
