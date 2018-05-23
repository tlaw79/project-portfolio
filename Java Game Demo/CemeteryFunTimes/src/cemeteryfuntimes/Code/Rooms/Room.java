package cemeteryfuntimes.Code.Rooms;
import cemeteryfuntimes.Code.Enemy;
import cemeteryfuntimes.Code.Pickup;
import cemeteryfuntimes.Code.Player;
import cemeteryfuntimes.Code.Shared.*;
import cemeteryfuntimes.Code.Spawn;
import cemeteryfuntimes.Code.Weapons.Projectile;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
/**
* Room abstract class contains variables and methods related to rooms.
* @author David Kozloff & Tyler Law
* 
* //DKOZLOFF 06/22 Add room entered routine.
*/
public abstract class Room implements Globals {
    protected final Player player;
    protected final Object[] neighbors;
    protected final ArrayList<Enemy> enemies;
    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }
    protected final ArrayList<Spawn> spawns;
    public ArrayList<Spawn> getSpawns() {
        return spawns;
    }
    protected final ArrayList<Projectile> deadEnemyProjectiles;
    public ArrayList<Projectile> deadEnemyProjectiles() {
        return deadEnemyProjectiles;
    }
    protected final ArrayList<Pickup> pickups;
    public ArrayList<Pickup> getPickups() {
        return pickups;
    }
    public boolean visited;
    public final int type;
    private final Random random;
    
    //Constants
    private final static double pickupSpawnProb = 0.2;
    
    /**
    * Room class constructor initializes variables related to rooms.
    * 
    * @param player  The player.
    * @param type    The type of pickup.
    */
    public Room (Player player, int type) {
        this.player = player;
        neighbors = new Object[4];
        this.type = type;
        enemies = new ArrayList();
        pickups = new ArrayList();
        spawns = new ArrayList();
        deadEnemyProjectiles = new ArrayList();
        random = new Random();
    }
    /**
    * Updates the room.  Overridden by a specific room implementation.
    */
    public void update() {
        Collision.checkPickupCollision(player,pickups);
        for (int i=0; i<spawns.size(); i++) {
            spawns.get(i).update();
        }
        Projectile projectile;
        for (int i=0; i<deadEnemyProjectiles.size(); i++) {
            projectile = deadEnemyProjectiles.get(i);
            if (projectile.collide) { deadEnemyProjectiles.remove(i); }
            else { projectile.update(); }
        }
        Enemy enemy;
        for (Iterator<Enemy> enemyIt = enemies.iterator(); enemyIt.hasNext();) {
            enemy = enemyIt.next();
            if (enemy.health <= 0) { 
                if (this.random.nextFloat() <= pickupSpawnProb) {
                    Boolean collide = false;
                    float y = 0;
                    for (int i = 0; i < this.spawns.size(); i++) {
                        if (this.spawns.get(i).collide(new Pickup(enemy.xPos(), enemy.yPos(), this.random.nextInt(PICKUPTYPES)))) {
                            collide = true;
                            y = spawns.get(i).yPos();
                        }
                    }
                    if (collide) {
                        this.pickups.add(new Pickup(enemy.xPos(), y-100, this.random.nextInt(PICKUPTYPES)));
                    } else {
                        this.pickups.add(new Pickup(enemy.xPos(), enemy.yPos(), this.random.nextInt(PICKUPTYPES)));
                    }
                }
                EnemyDead(enemy);
                enemyIt.remove();
                break;
            }
            enemy.update();
        }
    }
    /**
    * Renders room objects.  Overridden by a specific room implementation.
    * 
    * @param g The Graphics object used by Java to render everything in the game.
    */
    public void draw(Graphics2D g) {
        for (int i=0; i < enemies.size(); i++) {
            enemies.get(i).draw(g);
        }
        for (int i=0; i < pickups.size(); i++) {
            pickups.get(i).draw(g);
        }
        for (int i=0; i < deadEnemyProjectiles.size(); i++) {
            deadEnemyProjectiles.get(i).draw(g);
        }
        BufferedImage sourceDoor = RoomClear() ? ImageLoader.getImage("General/doorOpen.png",0) : ImageLoader.getImage("General/doorClosed.png",0);
        BufferedImage door;
        if (GetNeighbor(LEFT) != null) {
            door = sourceDoor;
            g.drawImage(door, GAMEBORDER - door.getWidth()/2, GAMEHEIGHT/2 - door.getHeight()/2 , null);
        }
        if (GetNeighbor(RIGHT) != null) {
            door = Utilities.rotateImage(sourceDoor, ROTATION[RIGHT]);
            g.drawImage(door, SCREENWIDTH - GAMEBORDER - door.getWidth()/2, GAMEHEIGHT/2 - door.getHeight()/2 , null);
        }
        if (GetNeighbor(UP) != null) {
            door = Utilities.rotateImage(sourceDoor, ROTATION[UP]);
            g.drawImage(door, GAMEBORDER + GAMEWIDTH/2 - door.getWidth()/2, - door.getHeight()/2 , null);
        }
        if (GetNeighbor(DOWN) != null) {
            door = Utilities.rotateImage(sourceDoor, ROTATION[DOWN]);
            g.drawImage(door, GAMEBORDER + GAMEWIDTH/2 - door.getWidth()/2, GAMEHEIGHT - door.getHeight()/2 - 20, null);
        }
    }
    /**
    * Determines if a room has been cleared, which is overridden by the
    * specific room type.
    * 
    * @return A boolean indicating if the room has been cleared.
    */
    public abstract boolean RoomClear();
    /**
    * Call any necessary code upon entering a room.
    * 
    * //DKOZLOFF 06/22 Created routine.
    */
    public void RoomEntered() {}
    /**
    * Removes dead enemy frome the enemies array
    * Adds any remaining projectiles to deadEnemyProjectiles
    * Generates pickup if necessary
    * 
    * @param enemy The dead enemy
    */
    public void EnemyDead(Enemy enemy) {
        if (enemy.getWeapon() == null) { return; }
        deadEnemyProjectiles.addAll(enemy.getWeapon().Projectiles());
        boolean collide = false;
        float y=0;
        if (random.nextFloat() <= pickupSpawnProb) {
            for (int i = 0; i < this.spawns.size(); i++) {
                if (this.spawns.get(i).collide(new Pickup(enemy.xPos(), enemy.yPos(), this.random.nextInt(PICKUPTYPES)))) {
                    collide = true;
                    y = spawns.get(i).yPos();
                }
            }
            if (collide) {
                this.pickups.add(new Pickup(enemy.xPos(), y-100, this.random.nextInt(PICKUPTYPES)));
            } else {
                this.pickups.add(new Pickup(enemy.xPos(), enemy.yPos(), this.random.nextInt(PICKUPTYPES)));
            }
        }
    }
    /**
    * Gets the neighboring room according to the given side.
    * 
    * @param  side The neighboring side.
    * @return      The neighboring room.
    */
    public Room GetNeighbor(int side) {
        if (side < 0 || side > 3) { return null; }
        return (Room) neighbors[side];
    }
    /**
    * Sets a neighboring room according to the given room and side.
    * 
    * @param neighbor The neighboring room.
    * @param side     The neighboring side.
    */
    public void SetNeighbor(Room neighbor, int side) {
        neighbors[side] = neighbor;
    }
}