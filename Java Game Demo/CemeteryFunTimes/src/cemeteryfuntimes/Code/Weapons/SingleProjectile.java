package cemeteryfuntimes.Code.Weapons;
import cemeteryfuntimes.Code.Shared.ImageLoader;
import cemeteryfuntimes.Code.Shared.PosVel;
import java.awt.Graphics2D;
/**
* SingleProjectile class contains variables and methods related to single projectiles, i.e. projectiles for Flamethrower, Laser, etc.
* @author David Kozloff & Tyler Law
*/
public class SingleProjectile extends Projectile {
    
    private final PosVel posVel;
    private final Weapon weapon;
    private final float sourceDamage;
    private final int height;
    private final int width;
    private final float offset;
    private final String projectileImagePath;
    
    private boolean active = false;
    private int currentDirection = -1;
    private float xPadding;
    private float yPadding;
    /**
    * SingleProjectile class constructor initializes variables related to single projectiles.
    * 
    * @param posVel              The posVel firing the projectile
    * @param projectileImagePath The image path for the projectile image
    * @param weapon              The posVel's weapon.
    */
    public SingleProjectile(PosVel posVel, String projectileImagePath, Weapon weapon) {
        //TODO Differentiate player and enemy weapon initializations
        super(weapon);
        this.weapon = weapon;
        this.projectileImagePath = projectileImagePath;
        offset = weapon.ProjectileOffset();
        sourceDamage = weapon.Damage();
        damage = 0; //This projectile has no damage unless active
        this.posVel = posVel;
        projectileImage = ImageLoader.getImage(projectileImagePath, rotation);
        height = weapon.ProjectileHeight();
        width = weapon.ProjectileWidth();
    }
    /**
    * Updates the projectile.
    */
    @Override
    public void update() {
        int direction = weapon.shootDirection();
        if (direction == -1) { active = false; damage = 0; return; }
        active = true; damage = sourceDamage;
        //Update the position of the projectile to the current posVel position
        if (currentDirection != direction) {
            currentDirection = direction;
            
            //update the x and y padding
            int horizontal = (direction == LEFT || direction == RIGHT) ? 1 : 0;
            int vertical = (direction == UP || direction == DOWN) ? 1 : 0;
            int positive = (direction == RIGHT || direction == DOWN) ? 1 : -1;
            xRad = horizontal * height / 2 + vertical * width / 2;
            yRad = vertical * height / 2 + horizontal * width / 2;
            xPadding = positive * (horizontal * (posVel.rad() + xRad) - vertical * offset);
            yPadding = positive * (vertical * (posVel.rad() + yRad) + horizontal * offset);
            
            //Rotate the projectile image to match the posVel rotation
            double radians = ROTATION[weapon.shootDirection()];
            if (rotation != radians) {
                rotation = radians;
                projectileImage = ImageLoader.getImage(projectileImagePath, rotation);
            }
            xSide = GAMEBORDER - projectileImage.getWidth()/2;
            ySide = -projectileImage.getHeight()/2;
        }
        xPos = xPadding + posVel.xPos();
        yPos = yPadding + posVel.yPos();
    }
    /**
    * Renders the projectile.
    * 
    * @param g The Graphics object used by Java to render everything in the game.
    */
    @Override
    public void draw(Graphics2D g) {
        if (active) { super.draw(g); }
    }
}