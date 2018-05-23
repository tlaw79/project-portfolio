package cemeteryfuntimes.Code;
import cemeteryfuntimes.Code.Bosses.Boss;
import cemeteryfuntimes.Code.Weapons.Weapon;
import cemeteryfuntimes.Code.Shared.*;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
/**
* Player class contains variables and methods related to the player.
* @author David Kozloff & Tyler Law
*/
public final class Player extends PosVel {

    //Movement based variables
    private float xAccel;
    private float yAccel;
    private final boolean[] moveKeysPressed;

    //Other
    public double Rotation() {
        return rotation;
    }
    private BufferedImage playerImage;
    private BufferedImage sourcePlayerImage;
    private int health;
    public int getHealth() {
        return health;
    }
    private int coins;
    public int getCoins() {
        return coins;
    }
    public long invincTimer = 0;
    private final Weapon weapon;
    private final ArrayList<Integer> weaponKeys;
    public ArrayList<Integer> getWeaponKeys() {
        return weaponKeys;
    }
    private int currentWeaponKey;
    public Weapon getWeapon() {
        return weapon;
    }
    /**
    * Player class constructor initializes variables related to the player.
    * 
    * @param xPos      The x-coordinate of the player.
    * @param yPos      The y-coordinate of the player.
    */
    public Player(int xPos, int yPos) {
        super (xPos,yPos);
        moveKeysPressed = new boolean [4];
        health = 6;
        coins = 3;
        rad = PLAYERSIZE*4/9; xRad = rad; yRad = rad;
        xSide = GAMEBORDER-rad;
        ySide = -rad;
        weaponKeys = new ArrayList();
        weapon = new Weapon(this, PISTOL);
        currentWeaponKey = PISTOL;
        weaponKeys.add(PISTOL);
        changePlayerImage();
        rotatePlayerImage(1);
        this.xPos = 400;
        this.yPos = 400;
    }
    /**
     * Updates player position on room change.
     * 
     * @param side The door the player just entered.
     */
    public void changeRoom(int side) {
        weapon.changeRoom();
        switch(side) {
            case LEFT:
                xPos = GAMEWIDTH - rad;
                break;
            case RIGHT:
                xPos = rad;
                break;
            case UP:
                yPos = GAMEHEIGHT - rad;
                break;
            case DOWN:
                yPos = rad;
                break;
        }
    }
    /**
    * Records which movement direction keys are currently being pressed.
    * 
    * @param direction    The movement direction key currently being pressed.
    * @param keyIsPressed Returns true if the key is currently being pressed.
    */
    public void movementKeyChanged(int direction, boolean keyIsPressed) {
        //Record which directional keys are being pressed
        moveKeysPressed[direction] = keyIsPressed;
    }
    /**
    * Records which shooting direction keys are currently being pressed.
    * 
    * @param direction    The shooting direction key currently being pressed.
    * @param keyIsPressed Returns true if the key is currently being pressed.
    */
    public void shootKeyChanged(int direction, boolean keyIsPressed) {
        //If an arrow key was pressed, pass event to weapon
        if (keyIsPressed) {
            weapon.keyPressed(direction);
            //Also rotate the image of the player
            rotatePlayerImage(weapon.shootDirection());
        } else {
            weapon.keyReleased(direction);
        }
    }
    /**
    * Records which change weapon keys are currently being pressed.
    * 
    * @param gameCode    The change weapon key currently being pressed.
    * @param keyIsPressed Returns true if the key is currently being pressed.
    * @param specificWeapon
    */
    public void changeWeaponKeyChanged(int gameCode, boolean keyIsPressed, boolean specificWeapon) {
        if (!keyIsPressed) { return; }
        int newWeaponKey;
        int lastShotDirection = weapon.shootDirection();
        boolean shooting = lastShotDirection != -1;
        // Stop shooting the old weapon.
        // This allows the player to automatically begin firing the new gun 
        // if a change weapon key is pressed while the fire key is being held down.
        if (shooting) {
            weapon.keyReleased(lastShotDirection);
        }
        // If gameCode is a number key, just switch to that weapon.
        if (specificWeapon) {
            int index = weaponKeys.indexOf(gameCode);
            newWeaponKey = weaponKeys.get(index);
        // Otherwise, scroll to the next/previous weapon.
        } else {
            int nextIndex = weaponKeys.indexOf(currentWeaponKey);
            nextIndex += (gameCode==NEXTWEAPON ? 1 : -1);
            int mod = weaponKeys.size();
            //Get next index using modulus arithmetic
            nextIndex = ((nextIndex % mod) + mod) % mod;
            newWeaponKey = weaponKeys.get(nextIndex);
        }
        // Load the new weapon provided from the above logic,
        // as long as the new weapon is a different weapon.
        if (newWeaponKey != currentWeaponKey) {
            this.weapon.loadWeapon(newWeaponKey);
            currentWeaponKey = newWeaponKey;
        }
        // Start shooting the new weapon.
        // This allows the player to automatically begin firing the new gun 
        // if a change weapon key is pressed while the fire key is being held down.
        if (shooting) {
            weapon.keyPressed(lastShotDirection);
        }
        changePlayerImage();
    }
    /**
    * Updates player image on weapon change.
    */
    private void changePlayerImage() {
        sourcePlayerImage = Utilities.getScaledInstance(IMAGEPATH+weapon.PlayerImagePath(),rad*2,rad*2);
        playerImage = Utilities.rotateImage(sourcePlayerImage,rotation);
    }
    /**
    * Rotates the player's image.
    * 
    * @param direction    The direction of rotation.
    */
    public void rotatePlayerImage(int direction) {
        //Rotate the image of the player
        rotation = ROTATION[direction];
        playerImage = Utilities.rotateImage(sourcePlayerImage,rotation);
    }
    /**
    * Updates the player.
    */
    public void update() {
        //Update postion and velocity
        weapon.update();
        calcAccels();
        xVel += xAccel;
        yVel += yAccel;
        xPos += xVel;
        yPos += yVel;
        //Stop invincibility frames after a certain amount of time
        if (invincTimer != 0 && System.currentTimeMillis() - invincTimer > INVINCFRAMES) {invincTimer = 0;}
    }
    /**
    * Calculates the player's acceleration.
    */
    public void calcAccels() {
        //Calculate x and y accelerations
        // ^ is the Exclusive or operation
        // ? 1 : 0 casts a boolean as 1 if true and 0 if false
        xAccel = 0;
        yAccel = 0;

        //If only one of the x directional keys is pressed apply an acceleration in that direction
        if( moveKeysPressed[LEFT] ^ moveKeysPressed[RIGHT] ) {
            xAccel = (moveKeysPressed[LEFT] ? 1 : 0)* -PLAYERACCEL + (moveKeysPressed[RIGHT] ? 1 : 0)*PLAYERACCEL;
        }
        if( moveKeysPressed[UP] ^ moveKeysPressed[DOWN] ) {
            yAccel = (moveKeysPressed[UP] ? 1 : 0)* -PLAYERACCEL + (moveKeysPressed[DOWN] ? 1 : 0)*PLAYERACCEL;
        }
        if (xAccel != 0 && yAccel != 0) {
            //If moving in two directions divide both accels by square root of 2
            //This ensures diagonal moving speed does not exceed linear moving speed
            xAccel /= 1.41421356237;
            yAccel /= 1.41421356237;
        }
        //Combine both player accel and friction deaccel
        xAccel += -FRICTION * xVel / PLAYERMAXSPEED;
        yAccel += -FRICTION *  yVel / PLAYERMAXSPEED;
    }
    /**
    * Handles collision if the player is not currently in invincibility frames.
    * 
    * @param enemy   The enemy the player collided with.
    * @param horVert Integer array retrieved from Utilities.getHorizontalVertical.
    */
    public void enemyCollide(Enemy enemy, int[] horVert) {
        if (invincTimer == 0) {
            damaged(enemy.ContactDamage());
            xVel = - PLAYERCOLLISIONVEL * horVert[HORIZONTAL];
            yVel = - PLAYERCOLLISIONVEL * horVert[VERTICAL];
            //Maybe add in some sort of knockback on collision?
        }
    }
    /**
    * Handles collision if the player is not currently in invincibility frames.
    * 
    * @param boss    The boss the player collided with.
    * @param xDist   The proportion of collision in xDirection
    * @param yDist   The proportion of collision in yDirection
    */
    public void bossCollide(Boss boss, float xDist, float yDist) {
        if (invincTimer == 0) {
            damaged(boss.ContactDamage());
            xVel = - BOSSCOLLISIONVEL * xDist;
            yVel = - BOSSCOLLISIONVEL * yDist;
            //Maybe add in some sort of knockback on collision?
        }
    }
    public void spawnCollide (Spawn spawn, int[] horVert) {
        if (horVert[0] != 0) {
            xPos = spawn.xPos() - 90*horVert[0];
        } 
        if (horVert[1] != 0) {
            yPos = spawn.yPos() - 95*horVert[1];
        }
    }
    /**
    * Updates player's health upon taking damage.
    * 
    * @param damage 
    */
    @Override
    public void damaged (float damage) {
        if (invincTimer == 0) {
            invincTimer = System.currentTimeMillis();
            health -= damage;
        }
    }
    /** Updates player's health upon receiving a health pickup.
    * 
    * @param hp The amount of healing done.
    */
    public void healed (float hp) {
        this.health += hp;
    }
    /** Updates player's coin count upon receiving a coin pickup.
    * 
    * @param coins The amount of coins received.
    */
    public void addMoney (float coins) {
        this.coins += coins;
    }
    /** Updates player's coin count upon buying an item.
    * 
    * @param coins The amount of coins spent.
    */
    public void removeMoney (float coins) {
        this.coins -= coins;
    }
    /**
    * Renders the player.
    * 
    * @param g The Graphics object used by Java to render everything in the game.
    */
    public void draw(Graphics2D g) {
        weapon.draw(g);
        g.drawImage(playerImage, Math.round(xSide+xPos), Math.round(ySide+yPos), null);
    }
}