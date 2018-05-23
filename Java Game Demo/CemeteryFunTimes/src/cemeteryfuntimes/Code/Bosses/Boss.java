package cemeteryfuntimes.Code.Bosses;
import cemeteryfuntimes.Code.Player;
import cemeteryfuntimes.Code.Shared.*;
import cemeteryfuntimes.Code.Weapons.Weapon;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
/**
* Boss abstract class contains variables and methods related to boss enemies.
* @author David Kozloff & Tyler Law
*/
public abstract class Boss extends PosVel implements Globals {
    protected float health;
    protected final Player player;
        public Player Player() {
        return player;
    }
    protected final ArrayList<Weapon> weapons;
    public ArrayList<Weapon> weapons() {
        return weapons;
    }
    private final BufferedImage sourceBossImage;
    protected int contactDamage;
    public int ContactDamage() {
        return contactDamage;
    }
    /**
    * Boss class constructor initializes variables related to boss enemies.
    * 
    * @param player    The player.
    * @param imagePath Image path for the boss.
    * @param height    Image height
    * @param width     Image width
    */
    public Boss(Player player, String imagePath, int height, int width, int health, int contactDamage) {
        super (GAMEWIDTH/2,GAMEHEIGHT/2);
        this.player = player;
        this.health = health;
        weapons = new ArrayList();
        sourceBossImage = Utilities.getScaledInstance(IMAGEPATH+imagePath, width, height);
        xSide = GAMEBORDER;
        ySide = 0;
        xRad = width/2; yRad = height/2;
        this.contactDamage = contactDamage;
    }
    /**
    * Updates the boss.  Overridden by a specific boss implementation.
    */
    public void update() {
        for (int i=0; i<weapons.size(); i++) {
            weapons.get(i).update();
        }
    }
    /**
    * Renders the boss.
    * 
    * @param g The Graphics object used by Java to render everything in the game.
    */
    public void draw(Graphics2D g) {
         for (int i=0; i<weapons.size(); i++) {
            weapons.get(i).draw(g);
        }
        BufferedImage bossImage = Utilities.rotateImage(sourceBossImage, rotation);
        float xImagePad = bossImage.getWidth()/2;
        float yImagePad = bossImage.getHeight()/2;
        if(health>0) {g.drawImage(bossImage, Math.round(xSide+xPos-xImagePad), Math.round(ySide+yPos-yImagePad), null);}
    }
    /**
    * Indicates if the boss is dead.
    * 
    * @return A boolean indicating if the boss is dead.
    */
    public boolean isDead() {
        return this.health <= 0;
    }
     /**
     * Updates the health of the enemy upon taking damage.
     * TODO hurt animation/invincibility frames?
     * 
     * @param damage The damage being done to the enemy.
     */
    @Override
    public void damaged(float damage) {
       health -= damage;
    }
}