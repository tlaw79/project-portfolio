package cemeteryfuntimes.Code.Bosses;
import cemeteryfuntimes.Code.Player;
import cemeteryfuntimes.Code.Shared.*;
import cemeteryfuntimes.Code.Weapons.Weapon;
import java.util.Random;
/**
* Ghoulie boss.
* @author David Kozloff & Tyler Law
*/
public final class Ghoulie extends Boss implements Globals {
    private final static int height = 300;
    private final static int width = 300;
    private final static int maxHealth = 100;
    private float speed;
    public float Speed() {
        return speed;
    }
    private final int movementDelay;
    private long lastMovement;
    private int attackTimer;
    
    // Used for making hitbox smaller than model.
    private final int fauxXRad;
    public int getFauxXRad() {
        return fauxXRad;
    }
    private final int fauxYRad;
    public int getFauxYRad() {
        return fauxYRad;
    }
    private final static String imagePath = "General/GHOUL.png";
    /**
    * Ghoulie class constructor initializes variables related to the Ghoulie boss.
    * 
    * @param player     The player.
    */
    public Ghoulie(Player player) {
        super(player, imagePath, height, width, 100, 2);
        this.health = 100;
        this.rad = 150;
        this.xRad = this.rad;
        this.yRad = this.rad;
        this.fauxXRad = 35;
        this.fauxYRad = 110;
        this.xSide = GAMEBORDER;
        this.ySide = 0;
        this.movementDelay = 2000;
        this.speed = 2;
        this.attackTimer = 0;
        this.contactDamage = 2;
        Weapon weapon = new Weapon(this,GHOULIE1);
        this.weapons.add(weapon);
        weapon = new Weapon(this,GHOULIE2);
        this.weapons.add(weapon);
        weapon = new Weapon(this,GHOULIE3);
        this.weapons.add(weapon);
        this.weapons.get(0).inactive = true;
        this.weapons.get(1).inactive = true;
        this.weapons.get(2).inactive = true;
    }
    /**
    * Updates the boss.
    */
    @Override
    public void update() {
        super.update();
        if (isDead()) {
            weapons.get(0).inactive = true;
            weapons.get(1).inactive = true;
            weapons.get(2).inactive = true;
            return; 
        }
        attack();
        xPos += xVel;
        yPos += yVel;
    }
    /**
    * Boss AI.
    */
    public void attack() {
        if (this.health < (Ghoulie.maxHealth / 4)) {
            this.speed = 4;
        } else if (this.health < (Ghoulie.maxHealth / 2)) {
            this.speed = 3;
        }
        long now = System.currentTimeMillis();
        if ( now - lastMovement > movementDelay ) {
            lastMovement = now;
            this.attackTimer++;
            switch (this.attackTimer % 5) {
                case 4:
                    weapons.get(0).inactive = true;
                    weapons.get(1).inactive = false;
                    weapons.get(2).inactive = true;
                    break;
                case 3:
                    weapons.get(0).inactive = false;
                    weapons.get(1).inactive = true;
                    weapons.get(2).inactive = true;
                    break;
                case 2:
                    weapons.get(0).inactive = true;
                    weapons.get(1).inactive = true;
                    weapons.get(2).inactive = false;
                    moveRandomly();
                    break;
                case 1:
                    weapons.get(0).inactive = true;
                    weapons.get(1).inactive = true;
                    weapons.get(2).inactive = true;
                    this.speed += 1;
                    moveTowardPlayer();
                    this.speed -= 1;
                    break;
                case 0:
                    weapons.get(0).inactive = true;
                    weapons.get(1).inactive = true;
                    weapons.get(2).inactive = true;
                    this.speed += 1;
                    moveRandomly();
                    this.speed -= 1;                    
                    break;
                default:
                    break;
            }
        }
    }
    /**
    * Updates the velocity of the enemy to move randomly. 
    */
    private void moveRandomly() {
        long now = System.currentTimeMillis();
        if ( now - lastMovement > movementDelay ) {
            lastMovement = now;
            Random random = new Random();
            do {
                xVel = (random.nextFloat() < 0.5 ? -1 : 1) * random.nextFloat();
                yVel = (random.nextFloat() < 0.5 ? -1 : 1) * random.nextFloat();
            }
            while (!validMoveDirection());
            //Adjust to have correct speed
            float totalSpeed = (float) Math.sqrt(xVel*xVel + yVel*yVel);
            xVel = speed * xVel / totalSpeed;
            yVel = speed * yVel / totalSpeed;
        }
    }
    /**
     * Checks if enemy will collide with wall before changing direction
     * 
     * @return false if enemy will collide with wall, true otherwise 
     */
    private boolean validMoveDirection() {
        double steps = movementDelay/TIMERDELAY;
        boolean valid = true;
        xPos+=xVel*steps;
        yPos+=yVel*steps;
        if (this.hitWall()) {valid=false;}
        xPos-=xVel*steps;
        yPos-=yVel*steps;
        return valid;
    }
    /**
    * Updates the velocity of the enemy to be moving directly toward the player. 
    */
    private void moveTowardPlayer() {
        //Calculates what the current velocity of the enemy should be
        //Find the vector pointing from the enemy to the player
        if (xVel ==0 && yVel==0) {
            int x = 6;
        }
        float xDist = player.xPos() - xPos;
        float yDist = player.yPos() - yPos;
        float totDist = (float) Math.sqrt(xDist*xDist + yDist*yDist);
        
        //Scale the vector to be the length of enemy speed to get speed
        xVel = speed * (xDist / totDist);
        yVel = speed * (yDist / totDist);
    }
}