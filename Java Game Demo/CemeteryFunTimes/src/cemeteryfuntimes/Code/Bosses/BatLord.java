package cemeteryfuntimes.Code.Bosses;
import cemeteryfuntimes.Code.Enemy;
import cemeteryfuntimes.Code.Player;
import cemeteryfuntimes.Code.Rooms.BossRoom;
import cemeteryfuntimes.Code.Shared.*;
import cemeteryfuntimes.Code.Weapons.Weapon;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
/**
* Bat Lord boss.
* @author David Kozloff & Tyler Law
* 
* //DKOZLOFF 06/22 Make boss easier.
*/
public class BatLord extends Boss implements Globals {
    
    //Constants
    private final static int height = 200;
    private final static int width = (int)(height*1.29); //Conversion factor for dimensions of image
    private final static String imagePath = "Bosses/BatLord/batlord.png";
    private final static int movementTypes = 2;
    private final static float chargeSpeed = 3;
    private final static int attackTypes = 3;
    private final long batSpawnDelay = 700; //Delay between spawning of bats
    private final static int[][] durations = {
        {2000,1000},{5000,2500},{0,4500}
    };
    
    //Member Variables
    private final Random randType;
    private int movementType=0; //The style of movement batlord is currently using
                                // 0 - Rest, duration = 2000
                                // 1 - Charge, duration = 5000
                                // 2 - Spiral
    private long movementDuration=durations[0][0];
    private long movementTimer=System.currentTimeMillis();
    private int attackType=0; //The weapon batlord is currently using
                              // 0 - Rest, duration = 1000
                              // 1 - Bat cannon, duration = 2500
                              // 2 - Fire bat, duration = 4500
    private long attackDuration=durations[0][1];
    private long attackTimer=System.currentTimeMillis();
    private long batTimer; //Timer to keep track of when last bat was spawned
    private final ArrayList<Enemy> bats; //Spawned bats

    public BatLord(Player player, BossRoom room) {
        super(player, imagePath, height, width, 50, 2);
        this.health = 50;
        bats = room.getEnemies();
        randType = new Random();
        Weapon firebat = new Weapon(this,FIREBAT);
        firebat.inactive = true;
        weapons.add(firebat);
    }
    
    @Override
    public void update() {
        super.update();
        updateBats();
        if (isDead()) { weapons.get(0).inactive=true; return; }
        movementUpdate();
        attackUpdate();
    }
    
    /**
    * Helper function for update, updates the array of bats 
    */
    private void updateBats() {
        Enemy enemy;
        for (Iterator<Enemy> enemyIt = bats.iterator(); enemyIt.hasNext();) {
            enemy = enemyIt.next();
            if (enemy.health <= 0) { 
                enemyIt.remove(); 
                break;
            }
            enemy.update();
        }
    }
    
    /**
    * Updates how bat lord is moving 
    */
    private void movementUpdate() {
        xPos += xVel;
        yPos += yVel;
        long now = System.currentTimeMillis();
        if (now - movementTimer < movementDuration) { 
            switch (movementType) {
                case 0: //Rest
                    xVel -= FRICTION * xVel;
                    yVel -= FRICTION * yVel;
                    break;
                case 1: //Charge
                    int wall = hitSpecificWall();
                    switch (wall) {
                        case -1:
                            break;
                        case LEFT:
                        case RIGHT:
                            xVel = -xVel;
                            xPos += xVel;
                            break;
                        case UP:
                        case DOWN:
                            yVel = -yVel;
                            yPos +=yVel;
                            break;
                    }
                    break;
            }
        }
        else {
            movementTimer = now; 
            movementType = randType.nextInt(movementTypes);
            movementDuration = durations[movementType][0];
            switch (movementType) {
                case 1: //Charge, calculate initial direction
                    float xDist = player.xPos() - xPos;
                    float yDist = player.yPos() - yPos;
                    float totDist = (float) Math.sqrt(xDist*xDist + yDist*yDist);
                    xVel = chargeSpeed * xDist / totDist;
                    yVel = chargeSpeed * yDist / totDist;
                    break;
            }
        }
    }
    
    /**
    * Updates how bat lord is attacking
    */
    private void attackUpdate() {
        long now = System.currentTimeMillis();
        if (now - attackTimer < attackDuration) { 
            switch (attackType) {
                case 1: //Bat Cannon
                    if (now - batTimer > batSpawnDelay) { spawnBat(); batTimer=now; }
                    break;
            }
        }
        else {
            attackTimer = now; 
            attackType = randType.nextInt(attackTypes);
            attackDuration = durations[attackType][1];
            switch (attackType) {
                case 0:
                    for (int i = 0; i < weapons.size(); i++) {
                        weapons.get(i).inactive = true;
                    }
                    break;
                case 1: //Bat Cannon
                    batTimer = 0;
                    break;
                case 2: //Fire Bat
                    weapons.get(0).inactive = false;
                    break;
            }
        }
    }
    
    /**
    * Spawns bats.
    */
    private void spawnBat() {
        Enemy bat = new Enemy(player,xPos,yPos,BAT);
        bats.add(bat);
    }
}