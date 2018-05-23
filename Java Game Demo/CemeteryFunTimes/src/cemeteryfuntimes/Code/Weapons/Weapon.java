package cemeteryfuntimes.Code.Weapons;
import cemeteryfuntimes.Code.Shared.*;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import org.w3c.dom.NamedNodeMap;
/**
* Weapon class contains variables and methods related to weapons.
* @author David Kozloff & Tyler Law
*/
public class Weapon implements Globals {

    //Member variables
    private final ArrayList<Projectile> projectiles;
    public ArrayList<Projectile> Projectiles() {
        return projectiles;
    }
    private final boolean[] keyPressed;
    private final PosVel posVel;
    private long lastUpdate;

    //Gun definition
    private float damage;
    public float Damage() {
        return damage;
    }
    private int key;
    public int Key() {
        return key;
    }
    private String name;
    private int weaponLength;
    private float projectileSpeed;
    public float ProjectileSpeed() {
        return projectileSpeed;
    }
    private int projectileWidth;
    public int ProjectileWidth() {
        return projectileWidth;
    }
    private int projectileHeight;
    public int ProjectileHeight() {
        return projectileHeight;
    }
    private int projectileSpread;
    public int ProjectileSpread() {
        return projectileSpread;
    }
    private int numberofProjectiles;
    private int type;
    public int Type() {
        return type;
    }
    private int projectileDelay;
    private int projectileOffset;
    public int ProjectileOffset() {
        return projectileOffset;
    }
    private String projectileImagePath;
    private boolean enemyWeapon;
    public boolean EnemyWeapon() {
        return enemyWeapon;
    }
    private String playerImagePath;
    public String PlayerImagePath() {
        return playerImagePath;
    }
    private SingleProjectile singleProjectile;
    public boolean inactive=false;
    /**
    * Weapon class constructor initializes variables related to weapons.
    * 
    * @param posVel    The player or enemy this weapon belongs to.
    * @param weaponKey The key corresponding to a specific weapon type.
    */
    public Weapon(PosVel posVel, int weaponKey) {
        this.posVel = posVel;
        projectiles = new ArrayList();
        keyPressed = new boolean[4];
        loadWeapon(weaponKey);
        key = weaponKey;
    }
    /**
    * Records which shooting keys are currently being pressed, represented as a boolean array.
    * 
    * @param direction The shooting direction.
    */
    public void keyPressed(int direction) {
        keyPressed[LEFT] = false;
        keyPressed[RIGHT] = false;
        keyPressed[UP] = false;
        keyPressed[DOWN] = false;
        keyPressed[direction] = true;
    }
    /**
    * Records when a shooting key has been released.
    * 
    * @param direction The shooting direction.
    */
    public void keyReleased(int direction) {
        keyPressed[direction] = false;
    }
    /**
    * Empties the current projectiles 
    */
    public void changeRoom() {
        projectiles.clear();
        if (singleProjectile != null) {
            projectiles.add(singleProjectile);
        }
    }
    /**
    * Updates the weapon.
    */
    public void update() {
        Projectile projectile;
        for (Iterator<Projectile> projectileIt = projectiles.iterator(); projectileIt.hasNext();) {
            projectile = projectileIt.next();
            if (projectile.type() != 2 && projectile.collide) { projectileIt.remove(); break; }
            projectile.update();
        }
        if (singleProjectile != null) { singleProjectile.update(); }
        // Checking if the player is firing allows the weapon
        // to fire immediately when a shoot key is pressed,
        // rather than waiting the projectile delay period before firing.
        if ((type != 2) && (shootDirection() != -1)) { 
            createProjectile();
        } 
    }
    /**
    * Spawns a new projectile.
    */
    public void createProjectile() {
        if (inactive) { return; }
        //Check if enough time has passed for more projectiles to spawn
        long now = System.currentTimeMillis();
        if (now - lastUpdate < projectileDelay) {
            return;
        }
        lastUpdate = now;
 
        //Create new projectile with correct location relative to posVel
        if (type == AOEBALLISTIC) {
            double angle = 2*Math.PI / numberofProjectiles;
            for (int i=0; i<numberofProjectiles; i++) {
                Projectile projectile = new StandardProjectile(posVel, -1, i*angle, projectileImagePath, this, 0.0f);
                projectiles.add(projectile);
            }
        }
        else {
            int direction = shootDirection();
            Projectile projectile;
            if (direction >= 0) {
                if (this.key == SHOTGUN) {
                    for (int i=1; i<6; i++) {
                        projectile = new StandardProjectile(posVel, direction, Double.POSITIVE_INFINITY, projectileImagePath, this, i * 0.5f - 1.5f);
                        projectiles.add(projectile);
                    }
                }
                projectile = new StandardProjectile(posVel, direction, Double.POSITIVE_INFINITY, projectileImagePath, this, 0.0f);
                projectiles.add(projectile);
            }
        }
    }
    /**
    * Returns the direction in which the player is shooting, represented as an integer.
    * 
    * @return The direction in which the player is shooting, represented as an integer.
    */
    public int shootDirection() {
        for (int i = 0; i < 4; i++) {
            if (keyPressed[i]) {
                return i;
            }
        }
        return -1;
    }
    /**
    * Renders the weapon.
    * 
    * @param g The Graphics object used by Java to render everything in the game.
    */
    public void draw(Graphics2D g) {
        for (int i=0; i<projectiles.size(); i++) {
            projectiles.get(i).draw(g);
        }
    }
    /**
    * Loads the weapon data for a specified weapon variant from an xml file.
    * 
    * @param weaponKey The key corresponding to a specific weapon type.
    */
    public void loadWeapon(int weaponKey) {
        //Load the weapon definition from Weapons.xml
        NamedNodeMap attributes = cemeteryfuntimes.Code.Shared.Utilities.loadTemplate("Weapons.xml","Weapon",weaponKey);
        //Load variables that are universal to all weapon types
        damage = Float.parseFloat(attributes.getNamedItem("Damage").getNodeValue());
        name = attributes.getNamedItem("Name").getNodeValue();
        type = Integer.parseInt(attributes.getNamedItem("Type").getNodeValue());
        enemyWeapon = Boolean.parseBoolean(attributes.getNamedItem("EnemyWeapon").getNodeValue());
        if (enemyWeapon) { keyPressed[0] = true; }
        weaponLength = Integer.parseInt(attributes.getNamedItem("WeaponLength").getNodeValue());
        projectileWidth = Integer.parseInt(attributes.getNamedItem("ProjectileWidth").getNodeValue());
        projectileHeight= Integer.parseInt(attributes.getNamedItem("ProjectileHeight").getNodeValue());
        projectileOffset = Integer.parseInt(attributes.getNamedItem("ProjectileOffset").getNodeValue());
        projectileImagePath = attributes.getNamedItem("ProjectileImage").getNodeValue();
        ImageLoader.loadImage(projectileImagePath,projectileHeight,projectileWidth);
        //Load variables that are type dependent
        if (!enemyWeapon) {
            playerImagePath = attributes.getNamedItem("PlayerImage").getNodeValue();
        }
        if (type != SINGLEBULLET) { 
            projectileSpeed = Float.parseFloat(attributes.getNamedItem("ProjectileSpeed").getNodeValue());
            projectileDelay = Integer.parseInt(attributes.getNamedItem("ProjectileDelay").getNodeValue());
            projectileSpread = Integer.parseInt(attributes.getNamedItem("ProjectileSpread").getNodeValue());
            if (singleProjectile != null) { projectiles.remove(singleProjectile); singleProjectile = null;}
        }
        if (type == SINGLEBULLET) {
            singleProjectile = new SingleProjectile(posVel,projectileImagePath,this);
            projectiles.add(singleProjectile); 
        }
        if (type == AOEBALLISTIC) {
            numberofProjectiles = Integer.parseInt(attributes.getNamedItem("NumberOfProjectiles").getNodeValue());
        }
        key = weaponKey;
    }
}