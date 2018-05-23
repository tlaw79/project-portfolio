package cemeteryfuntimes.Code.Shared;
import cemeteryfuntimes.Code.Rooms.Room;
import cemeteryfuntimes.Code.Enemy;
import cemeteryfuntimes.Code.Weapons.Projectile;
import cemeteryfuntimes.Code.Weapons.Weapon;
import cemeteryfuntimes.Code.*;
import cemeteryfuntimes.Code.Bosses.Boss;
import cemeteryfuntimes.Code.Rooms.BossRoom;
import cemeteryfuntimes.Code.Rooms.NormalRoom;
import java.util.ArrayList;
import java.util.Iterator;
/**
* Collision class contains methods related to object collisions.
* @author David Kozloff & Tyler Law
* 
* //DKOZLOFF 06/22 Fixed problem where boss projectiles stopped being checked upon roomClear.
*/
public class Collision implements Globals {
    /**
    * Collision class constructor calls all collision methods.
    * 
    * @param player  The player.
    * @param room    The current room
    * @return        -1 if room is not clear or no door collided, 
    *                PORTALCOLLISION if cleared boss room and player steps into portal,
    *                else side of door collision
    * 
    * //DKOZLOFF 06/22 Fix boss projectile collisions after room clear. Add portal collision.
    */
    public static int checkCollisions(Player player, Room room) {
        // Check for collisions between Player and player projectiles with enemies
        // As well as enemy and enemy projectile collision with player
        // Also collisions with pickups / interactables and walls
        // Update accordingly
        
        boolean roomClear = room.RoomClear();
        int portalCollision=-1; //DKOZLOFF 06/22
        ArrayList<Enemy> enemies = room.getEnemies();
        for (int i=0; i < enemies.size(); i++) {
            enemies.get(i).calcVels();
        }
        ArrayList<Projectile> deadEnemyProjectiles = room.deadEnemyProjectiles();
        checkBallisticCollisions(player,enemies,deadEnemyProjectiles);
        ArrayList<Spawn> spawns;
        if (room instanceof NormalRoom) {
            spawns = ((NormalRoom) room).getSpawns();
            checkPlayerSpawnCollision(player,spawns);
        }
        if (room instanceof BossRoom) { //DKOZLOFF+3 06/22 Moved out of if (!roomClear)
            BossRoom bossRoom = (BossRoom) room;
            portalCollision = handleBossCollisions(bossRoom.getBoss(),player,roomClear);
        }
        if (!roomClear) { 
            checkEnemyWallCollisions(enemies);
            checkEnemyEnemyCollision(enemies);
            checkEnemyPlayerCollision(player,enemies);
        }
        checkBallisticWallCollisions(player,enemies,deadEnemyProjectiles);
        boolean[] wall = checkPlayerWallCollision(player);
        //Return PORTALCOLLISION if this is a cleared boss room and player steps into portal. //DKOZLOFF+2 06/22
        if (portalCollision == PORTALCOLLISION) { return portalCollision; }
        //Check if player collides with a door, if so return the door.
        return checkPlayerDoorCollision(player,wall,roomClear);
    }
    /**
     * Handles all collisions for boss
     * 
     * @param boss   The boss of this level.
     * @param player The player.
     * @return       Returns PORTALCOLLISION if boss is dead and player steps into portal, else -1.
     * 
     * //DKOZLOFF 06/22 Added if(!roomClear) block. Add return type, and portal collision code.
     */
    private static int handleBossCollisions(Boss boss, Player player, Boolean roomClear) {
        //Handle ballistic wall collisions
        ArrayList<Weapon> weapons = boss.weapons();
        for (int i=0; i <weapons.size(); i++) {
            handleBallisticCollisions(weapons.get(i).Projectiles(),player);
            ballisticWallCollisionLoop(weapons.get(i).Projectiles());
        }
        if (!roomClear) { //DKOZLOFF+17 06/22
            handleBallisticCollisions(player.getWeapon().Projectiles(),boss); 
            //Handle wall collisions
            handleWallCollision(boss,boss.checkWallCollision()); 
            //Handle physical collisions
            if (boss.collide(player)) {
                handleBossPlayerCollision(player,boss);
            }
        }
        //If room is clear, then check if player steps into portal.
        else {
            if (player.xPos > PORTALX - PORTALSIZE/2 && player.xPos < PORTALX + PORTALSIZE/2) {
                if (player.yPos > PORTALY - PORTALSIZE/2 && player.yPos < PORTALY + PORTALSIZE/2) {
                    return PORTALCOLLISION;
                }
            }
        }
        return -1;
    }
    /**
    * Checks for collisions between player projectiles and enemies.
    * 
    * @param player  The player.
    * @param enemies The array list of enemies.
    */
    private static void checkBallisticCollisions(Player player, ArrayList<Enemy> enemies, ArrayList<Projectile> deadEnemyProjectiles) {
        Enemy enemy;
        Projectile projectile;
        Weapon playerWeapon = player.getWeapon();
        ArrayList<Projectile> projectiles = playerWeapon.Projectiles();
        float damage = playerWeapon.Damage();
        handleBallisticCollisions(deadEnemyProjectiles,player);
        //Check player projectiles collision with enemies
        for (Iterator<Enemy> enemyIt = enemies.iterator(); enemyIt.hasNext();) {
            enemy = enemyIt.next();
            handleBallisticCollisions(projectiles,enemy);
            if (enemy.getWeapon() != null) {
                handleBallisticCollisions(enemy.getWeapon().Projectiles(),player);
            }
        }
    }
    /**
    * Handles ballistic collisions with both enemy and player.
    * 
    * @param projectiles Array of projectiles.
    * @param target      The PosVel to check if the projectiles collided with.
    * 
    * //DKOZLOFF 06/22 Make it so a single projectile can collide with multiple enemies.
    */
    private static void handleBallisticCollisions(ArrayList<Projectile> projectiles, PosVel target) {
        Projectile projectile;
        for (Iterator<Projectile> projectileIt = projectiles.iterator(); projectileIt.hasNext();) {
            projectile =projectileIt.next();
            if (target.collide(projectile)) {
                target.damaged(projectile.damage());
                projectile.collide = true;
                if (projectile.type() != SINGLEBULLET) { break; } //DKOZLOFF 06/22
            }
        }
    }
    /**
    * Checks for collisions between the player and enemies.
    * 
    * @param player  The player.
    * @param enemies The array list of enemies.
    */
    private static void checkEnemyPlayerCollision(Player player, ArrayList<Enemy> enemies) {
        for (Enemy enemy : enemies) {
            if (player.collide(enemy)) {
                handleEnemyPlayerCollision(player, enemy);
            }
        }
    }
    /**
    * Handles collision between the player and an enemy.
    * 
    * @param player The player.
    * @param enemy  The array list of enemies.
    */
    private static void handleEnemyPlayerCollision(Player player, Enemy enemy) {
        int side = player.sideCollided(enemy);
        // Positive is equal to 1 if player has the greater x or y coordinate on the side of the collision else -1
        int[] horVert = cemeteryfuntimes.Code.Shared.Utilities.getHorizontalVertical(side);
        
        player.enemyCollide(enemy, horVert);
        
        //Adjust the position of the enemy to be right next to the player on the side of the collision
        //Also set the enemy's speed to 0 in the direction of the collision
        
        if (horVert[HORIZONTAL] != 0) {
            enemy.xPos = player.xPos() + horVert[HORIZONTAL] * (player.rad() + enemy.rad()); 
        }
        else {
            enemy.yPos =  player.yPos() + horVert[VERTICAL] * (player.rad() + enemy.rad()); 
        }
        enemy.xVel=0; 
        enemy.yVel=0; 
    }
    /**
     * Handles collisions between the boss and player
     * 
     * @param player The player.
     * @param boss   The boss.
     */
    private static void handleBossPlayerCollision(Player player, Boss boss) {
        float xDist = boss.xPos - player.xPos;
        float yDist = boss.yPos - player.yPos;
        float totDist = (float) Math.sqrt(xDist*xDist + yDist*yDist);
        player.bossCollide(boss,xDist/totDist,yDist/totDist);
    }
    /**
    * Checks for collisions between an enemy and other enemies.
    * 
    * @param enemies The array list of enemies.
    */
    private static void checkEnemyEnemyCollision(ArrayList<Enemy> enemies) {
        Enemy enemyOne;
        Enemy enemyTwo;
        int side;
        for (int i=0; i < enemies.size(); i++) {
            enemyOne = enemies.get(i);
            for (int j=i+1; j < enemies.size(); j++) {
                enemyTwo = enemies.get(j);
                if (enemyOne.collide(enemyTwo)) {
                    handleEnemyEnemyCollision(enemyOne, enemyTwo);
                }
            }
        }
    }
    /**
    * Handles collisions between an enemy and another enemy.
    * 
    * @param enemyOne The first enemy.
    * @param enemyTwo The second enemy.
    */
    private static void handleEnemyEnemyCollision(Enemy enemyOne, Enemy enemyTwo) {
        int side = enemyOne.sideCollided(enemyTwo);
        float overlap;
        // Horizontal is equal to 1 if collision was on left or right wall else 0
        // same equivalent thing for vertical
        int[] horVert = cemeteryfuntimes.Code.Shared.Utilities.getHorizontalVertical(side);
        
        //Calculate the overlapping region between the two enemies
        overlap = enemyOne.rad + enemyTwo.rad - Math.abs(horVert[HORIZONTAL] * (enemyOne.xPos - enemyTwo.xPos)) - Math.abs(horVert[VERTICAL] * (enemyOne.yPos - enemyTwo.yPos));
        
        //Update the position to no longer be overlapping
        enemyOne.xPos = enemyOne.xPos - horVert[HORIZONTAL] * overlap/2; 
        enemyTwo.xPos = enemyTwo.xPos + horVert[HORIZONTAL] * overlap/2; 
        enemyOne.yPos = enemyOne.yPos - horVert[VERTICAL] * overlap/2; 
        enemyTwo.yPos = enemyTwo.yPos + horVert[VERTICAL] * overlap/2; 
    }
    /**
    * Checks for collisions between the player and pickups.
    * 
    * @param player     The player.
    * @param pickups    The array list of pickups.
    */
    public static void checkPickupCollision(Player player, ArrayList<Pickup> pickups) {
        for (int i = 0; i < pickups.size(); i++) {
            if (player.collide(pickups.get(i))) {
                switch (pickups.get(i).getType()) {
                    case 0:
                        // Add health if health pickup.
                        if (player.getHealth() == 5) {
                            player.healed(1);
                        } else if (player.getHealth() < 5) {
                            player.healed(2);
                        }
                        break;
                    case 1:
                        // Add money if coin pickup.
                        player.addMoney(1);
                        break;
                    case 2:
                        // Add machine gun if machine gun pickup.
                        player.getWeaponKeys().add(MACHINEGUN);
                        break;
                    case 3:
                        // Add flamethrower if flamethrower pickup.
                        player.getWeaponKeys().add(FLAMETHROWER);
                        break;
                    case 4:
                        // Add shotgun if shotgun pickup.
                        player.getWeaponKeys().add(SHOTGUN);
                        break;
                    default:
                        break;
                }
                pickups.remove(i); 
                i--;
            }
        }
    }
    /**
    * Checks for collisions between the player and doors.
    * 
    * @param player  The player.
    */
    private static int checkPlayerDoorCollision(Player player, boolean[] wall, boolean roomClear) {
        if (!roomClear) { return -1; }
        if (wall[RIGHT] && player.yPos <= GAMEHEIGHT/2 + 50 && player.yPos >= GAMEHEIGHT/2 - 50) {
            return RIGHT;
        }
        else if (wall[LEFT] && player.yPos <= GAMEHEIGHT/2 + 50 && player.yPos >= GAMEHEIGHT/2 - 50) {
            return LEFT;
        }
        else if (wall[UP] && player.xPos <= GAMEWIDTH/2 + 50 && player.xPos >= GAMEWIDTH/2 - 50) {
            return UP;
        }
        else if (wall[DOWN] && player.xPos <= GAMEWIDTH/2 + 50 && player.xPos >= GAMEWIDTH/2 - 50) {
            return DOWN;
        }
        return -1;
    }
    /**
    * Checks for collisions between projectiles and walls.
    * 
    * @param player  The player.
    * @param enemies The array list of enemies.
    */
    private static void checkBallisticWallCollisions(Player player, ArrayList<Enemy> enemies, ArrayList<Projectile> deadEnemyProjectiles) {
        ballisticWallCollisionLoop(player.getWeapon().Projectiles());
        ballisticWallCollisionLoop(deadEnemyProjectiles);
        Weapon enemyWeapon;
        enemies.stream().forEach((Enemy enemy) -> {
            if (enemy.getWeapon() != null) {
                ballisticWallCollisionLoop(enemy.getWeapon().Projectiles());
            }
        });
    }
    /**
    * Sub-routine for checkBallisticWallCollisions.
    * Sets projectile.collide to true if the projectile is colliding with a wall.
    * 
    * @param projectiles The array list of projectiles.
    */
    private static void ballisticWallCollisionLoop(ArrayList<Projectile> projectiles) {
        Projectile projectile;
        for (Iterator<Projectile> projectileIt = projectiles.iterator(); projectileIt.hasNext();) {
            projectile =projectileIt.next();
            if (projectile.hitWall()) {
                projectile.collide = true;
            }
        }
    }
    /**
    * Handle enemy wall collisions.
    * 
    * @param enemies Arraylist of enemies.
    */
    private static void checkEnemyWallCollisions(ArrayList<Enemy> enemies) {
        for (Enemy enemy : enemies) {
            handleWallCollision(enemy,enemy.checkWallCollision());
        }
    }
    /**
    * Checks for collisions between the player and walls.
    * 
    * @param player  The player.
    * @return        Boolean[] telling which walls were collided with.
    */
    private static boolean[] checkPlayerWallCollision(Player player) {
        boolean[] wall=player.checkWallCollision();
        return handleWallCollision(player,wall);
    }
    /**
     * Handles player and enemy wall collisions.
     * 
     * @param posVel The posVel to handle the wall collision for.
     * @param wall   An array of booleans that tell you whether or not a wall was collided with.
     * @return       The wall that was collided with.
     */
    private static boolean[] handleWallCollision(PosVel posVel, boolean[] wall) {
        if (wall[RIGHT]) {
            posVel.xVel = 0;
            posVel.xPos = GAMEWIDTH - posVel.xRad;
        }
        else if (wall[LEFT]) {
            posVel.xVel = 0;
            posVel.xPos = posVel.xRad;
        }
        if (wall[UP]) {
            posVel.yVel = 0;
            posVel.yPos = posVel.yRad;
        }
        else if (wall[DOWN]) {
            posVel.yVel = 0;
            posVel.yPos = GAMEHEIGHT - posVel.yRad;
        }
        return wall;
    }
    /**
    * Checks for collisions between the player and spawns.
    * 
    * @param player  The player.
    * @param spawns  The array list of spawns.
    */
    private static void checkPlayerSpawnCollision (Player player, ArrayList<Spawn> spawns) {
        for (Spawn spawn : spawns) {
            if (player.collide(spawn)) {
                int side = player.sideCollided(spawn);
                int[] horVert = cemeteryfuntimes.Code.Shared.Utilities.getHorizontalVertical(side);
                player.spawnCollide(spawn, horVert);
            }
        }
    }
}
