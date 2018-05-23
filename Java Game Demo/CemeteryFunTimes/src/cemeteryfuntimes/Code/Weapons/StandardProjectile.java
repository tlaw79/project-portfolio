package cemeteryfuntimes.Code.Weapons;
import cemeteryfuntimes.Code.Bosses.Boss;
import cemeteryfuntimes.Code.Enemy;
import cemeteryfuntimes.Code.Player;
import cemeteryfuntimes.Code.Shared.ImageLoader;
import cemeteryfuntimes.Code.Shared.PosVel;
import java.util.Random;
/**
* StandardProjectile class contains variables and methods related to standard projectiles, i.e. projectiles for Pistol, Shotgun, etc.
* @author David Kozloff & Tyler Law
*/
public class StandardProjectile extends Projectile {
    private final float offset;
    /**
    * StandardProjectile class constructor initializes variables related to standard projectiles.
    * 
    * @param posVel              The posVel firing the projectile.
    * @param direction           The direction in which the projectile was fired.
    * @param angle               The angle in which the projectile fires
    * @param projectileImagePath The file path for the projectile image.
    * @param weapon              The weapon firing the projectile.
    * @param offset              Bullet offset value for shotgun projectiles.
    */
    public StandardProjectile(PosVel posVel, int direction, double angle, String projectileImagePath, Weapon weapon, float offset) {
        super(weapon);
        this.offset = offset;
        damage = weapon.Damage();
        rotation = posVel.rotation();
        if (weapon.EnemyWeapon()) {enemyWeaponInit(posVel,weapon,angle);}
        else {playerWeaponInit(posVel,direction,weapon);}
        int height = weapon.ProjectileHeight();
        int width = weapon.ProjectileWidth();
        //TODO update xRad and yRad code when collision detection can handle rotated rectangle collisions
        xRad = height/2;
        yRad = width/2;
        this.projectileImage = ImageLoader.getImage(projectileImagePath, rotation);
        int xImagePad = projectileImage.getWidth()/2;
        int yImagePad = projectileImage.getHeight()/2;
        xSide = GAMEBORDER - xImagePad;
        ySide = -yImagePad;
    }
    /**
    * Initializes the projectile if the player is firing it.
    * 
    * @param posVel    The player object.
    * @param direction Direction projectile is fired in.
    * @param weapon    The player's weapon.
    */
    private void playerWeaponInit(PosVel posVel, int direction, Weapon weapon) {
        float speed = weapon.ProjectileSpeed();
        int offset = weapon.ProjectileOffset();
        float playerRad = posVel.rad();
  
        //Create projectile
        //Decide starting position by player position and direction projectile is being fired
        //Decide velocity by adding PROJECTILESPEED with player velocity in that direction 
        int[] horVert = cemeteryfuntimes.Code.Shared.Utilities.getHorizontalVertical(direction);
        
        xPos = posVel.xPos() + horVert[HORIZONTAL] * playerRad - horVert[VERTICAL] * offset;
        yPos = posVel.yPos() + horVert[VERTICAL] * playerRad + horVert[HORIZONTAL] * offset;
        xVel = horVert[HORIZONTAL] * (PROJECTILEBOOST * posVel.xVel() + speed);
        yVel = horVert[VERTICAL] * (PROJECTILEBOOST * posVel.yVel() + speed);
        handleSpread(weapon);
    }
    /**
    * Initializes projectiles if an enemy fired it.
    * 
    * @param posVel The enemy object.
    * @param weapon The enemy's weapon.
    */
    private void enemyWeaponInit(PosVel posVel, Weapon weapon, double angle) {
        Player player;
        if (posVel instanceof Enemy) {
            Enemy enemy = (Enemy) posVel;
            player = enemy.Player();
        }
        else {
            Boss boss = (Boss) posVel;
            player = boss.Player();
        }
        float speed = weapon.ProjectileSpeed();
        //TODO use offset: int offset = weapon.ProjectileOffset();
  
        //Create projectile
        //Decide starting position by player position and direction projectile is being fired
        //Decide velocity by adding PROJECTILESPEED with player velocity in that direction 
        
        xPos = posVel.xPos();
        yPos = posVel.yPos();
        if (angle == Double.POSITIVE_INFINITY) {
            xVel = PROJECTILEBOOST * posVel.xVel();
            yVel = PROJECTILEBOOST * posVel.yVel();
            float xDist = player.xPos() - posVel.xPos();
            float yDist = player.yPos() - posVel.yPos();
            float totDist = (float) Math.sqrt(xDist*xDist + yDist*yDist);
            xVel += speed * xDist / totDist;
            yVel += speed * yDist / totDist;
            xPos += (float) Math.cos(posVel.rotation()-Math.PI/2) * posVel.rad();
            yPos += (float) Math.sin(posVel.rotation()-Math.PI/2) * posVel.rad();
        }
        else {
            xVel = speed * (float) Math.cos(angle);
            yVel = speed * (float) Math.sin(angle);
        }
        handleSpread(weapon);
    }
    /**
     * Handles the spread of bullets upon initialization.
     * 
     * @param weapon The weapon firing this projectile.
     */
    private void handleSpread(Weapon weapon) {
        //Calculates a random angle smaller than "spread".
        //Updates xVel and yVel and rotation to account for the spread.
         double spread = weapon.ProjectileSpread();
         if (weapon.Key() == SHOTGUN) {
                spread = Math.toRadians(this.offset * spread);
         } else {
             if (spread != 0) {
                Random random = new Random();
                int sign = random.nextFloat() < 0.5 ? -1 : 1;
                spread = Math.toRadians(sign * random.nextFloat() * spread);
            }
        }
        //Rotates velocity vector by angle spread
        xVel = (float) (xVel * Math.cos(spread) - yVel * Math.sin(spread));
        yVel = (float) (xVel * Math.sin(spread) + yVel * Math.cos(spread));
        rotation += spread; 
    }
}
