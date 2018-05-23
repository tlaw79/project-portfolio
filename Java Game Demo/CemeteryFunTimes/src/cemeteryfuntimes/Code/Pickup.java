package cemeteryfuntimes.Code;
import cemeteryfuntimes.Code.Shared.*;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
/**
* Pickup class contains variables and methods related to pickups.
* @author David Kozloff & Tyler Law
*/
public class Pickup extends PosVel {
    private final static int HEARTSIZE = 40;
    private final int type;
    public int getType() {
        return type;
    }
    private final static String[] imagePaths = {
        "General/heart.png","General/coin.png",
        "General/machinegun.png","General/flamethrower.png",
        "General/shotgun.png"
    };
    /**
    * Pickup class constructor initializes variables related to pickups.
    * 
    * @param x          The x-coordinate of the pickup.
    * @param y          The y-coordinate of the pickup.
    * @param type       The type of the pickup object, i.e. health, ammo, etc.
    */
    public Pickup (float x, float y, int type) {
        super(x,y);
        this.type = type;
        rad = HEARTSIZE/2; xRad = rad; yRad = rad;
        xSide = GAMEBORDER - rad;
        ySide = - rad;
    }
    /**
    * Renders the pickup.
    * 
    * @param g The Graphics object used by Java to render everything in the game.
    */
    public void draw (Graphics g) {
        // Draw a heart.
        BufferedImage image = ImageLoader.getImage(imagePaths[type],0);
        g.drawImage(image,(int)(xSide + xPos),(int)(ySide + yPos),null);
    }
}
