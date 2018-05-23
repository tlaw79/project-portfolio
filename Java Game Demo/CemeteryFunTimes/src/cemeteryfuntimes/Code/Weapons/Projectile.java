package cemeteryfuntimes.Code.Weapons;
import cemeteryfuntimes.Code.Shared.PosVel;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
/**
* Projectile abstract class contains variables and methods related to projectiles.
* @author David Kozloff & Tyler Law
*/
public abstract class Projectile extends PosVel {
    
    protected BufferedImage projectileImage;
    public boolean collide;
    protected float damage;
    public float damage() {
        return damage;
    }
    protected int type;
    public int type() {
        return type;
    }
    /** 
     * Default projectile constructor
     * 
     * @param weapon The weapon firing the projectile
     */
    public Projectile(Weapon weapon) {
        super(0,0);
        type = weapon.Type();
        xSide = GAMEBORDER;
        ySide = 0;
    }
    /**
    * Updates the projectile.
    */
    public void update() {
        xPos += xVel;
        yPos += yVel;
    }
    /**
    * Renders the projectile.
    * 
    * @param g The Graphics object used by Java to render everything in the game.
    */
    public void draw(Graphics2D g) {
        g.drawImage(projectileImage, Math.round(xSide+xPos), Math.round(ySide+yPos), null);
    }
    
    //Not using this yet, but maybe eventually will be
    public void collide(float xPos, float yPos, int direction) {}
}