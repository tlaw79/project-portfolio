package cemeteryfuntimes.Code;
import cemeteryfuntimes.Code.Weapons.Weapon;
import cemeteryfuntimes.Code.Shared.*;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;
import org.w3c.dom.NamedNodeMap;
/**
* Enemy class contains variables and methods related to enemies.
* @author David Kozloff & Tyler Law.
*/
public class Enemy extends PosVel {

    //Member variables
    public float health;
    private final Player player;
    public Player Player() {
        return player;
    }
    private float xImagePad;
    private float yImagePad;

    //Enemy definition
    private int contactDamage;
    public int ContactDamage() {
        return contactDamage;
    }
    private int weaponKey;
    private float speed;
    public float Speed() {
        return speed;
    }
    private int type;
    public int Type() {
        return type;
    }
    private int difficulty;
    public int Difficulty() {
        return difficulty;
    }
    private int movementType;
    public int MovementType() {
        return movementType;
    }
    private long invincTimer;
    private int movementDelay;
    private long lastMovement;
    private String name;
    private String enemyImagePath;
    private Weapon weapon = null;
    public Weapon getWeapon() {
        return weapon;
    }
    /**
    * Enemy class constructor initializes variables related to enemies.
    *
    * @param player The player.
    * @param xPos   The x-coordinate of the enemy.
    * @param yPos   The y-coordinate of the enemy.
    * @param key    The key corresponding to a specific enemy type.
    */
    public Enemy(Player player, float xPos, float yPos, int key) {
        super (xPos,yPos);
        this.player = player;
        loadEnemy(key);
        xRad = rad; yRad = rad;
        xSide = GAMEBORDER;
        ySide = 0;
        if (weaponKey != 0) { weapon = new Weapon(this,weaponKey); }
        invincTimer = System.currentTimeMillis();
    }
    /**
    * Updates the enemy.
    */
    public void update() {
        xPos += xVel;
        yPos += yVel;
        if (weapon != null) { weapon.update(); }
        if (invincTimer != 0 && System.currentTimeMillis() - invincTimer > INVINCFRAMES) {invincTimer = 0;}
    }
    /**
    * Calculates the direction the enemy should be moving in.
    */
    public void calcVels() {
        switch(movementType) {
            case STDTOWARDPLAYER:
                moveTowardPlayer();
                break;
            case STDRANDOM:
                moveRandomly();
                break;
        }
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
        rotateEnemyImage();
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
            rotateEnemyImage();
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
    * Rotates the enemy's image.
    */
    private void rotateEnemyImage() {
        if (xVel == 0 && yVel == 0) {return;}
        double radians = Math.PI - Math.atan2(xVel, yVel);
        if (Math.abs(rotation - radians) > MINIMUMROTATION) {
            rotation = radians;
        }
    }
    /**
    * Loads the enemy data for a specified enemy variant from an xml file.
    *
    * @param enemyKey The key corresponding to a specific enemy type.
    */
    private void loadEnemy(int enemyKey) {
        NamedNodeMap attributes = Utilities.loadTemplate("Enemies.xml","Enemy",enemyKey);
        contactDamage = Integer.parseInt(attributes.getNamedItem("ContactDamage").getNodeValue());
        health = Integer.parseInt(attributes.getNamedItem("Health").getNodeValue());
        name = attributes.getNamedItem("Name").getNodeValue();
        difficulty = Integer.parseInt(attributes.getNamedItem("Difficulty").getNodeValue());
        speed = Float.parseFloat(attributes.getNamedItem("EnemySpeed").getNodeValue());
        rad = Integer.parseInt(attributes.getNamedItem("EnemySize").getNodeValue());
        type = Integer.parseInt(attributes.getNamedItem("Type").getNodeValue());
        movementType = Integer.parseInt(attributes.getNamedItem("MovementType").getNodeValue());
        movementDelay = Integer.parseInt(attributes.getNamedItem("MovementDelay").getNodeValue());
        enemyImagePath = attributes.getNamedItem("EnemyImage").getNodeValue();
        ImageLoader.loadImage(enemyImagePath, rad*2, rad*2);
        if (type == PROJECTILEENEMY) {
            weaponKey = Integer.parseInt(attributes.getNamedItem("WeaponKey").getNodeValue());
        }
    }
    /**
     * Updates the health of the enemy upon taking damage.
     * TODO hurt animation/invincibility frames?
     *
     * @param damage The damage being done to the enemy.
     */
    @Override
    public void damaged(float damage) {
        if (invincTimer == 0) {
            health -= damage;
        }
    }
    /**
    * Renders the enemy.
    *
    * @param g The Graphics object used by Java to render everything in the game.
    */
    public void draw(Graphics2D g) {
        BufferedImage enemyImage = ImageLoader.getImage(enemyImagePath, rotation);
        xImagePad = enemyImage.getWidth()/2;
        yImagePad = enemyImage.getHeight()/2;
        if(health>0) {g.drawImage(enemyImage, Math.round(xSide+xPos-xImagePad), Math.round(ySide+yPos-yImagePad), null);}
        if( weapon!=null ) { weapon.draw(g); }
    }
}
