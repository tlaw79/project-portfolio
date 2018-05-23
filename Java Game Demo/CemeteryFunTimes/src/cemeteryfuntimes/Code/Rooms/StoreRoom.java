package cemeteryfuntimes.Code.Rooms;
import cemeteryfuntimes.Code.Pickup;
import cemeteryfuntimes.Code.Player;
import cemeteryfuntimes.Code.Shared.Collision;
import cemeteryfuntimes.Code.Shared.Globals;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Random;
/**
* StoreRoom class contains variables and methods related to store rooms.
* @author David Kozloff & Tyler Law
*/
public final class StoreRoom extends Room implements Globals {
    private final Pickup heart;
    private final Pickup weapon;
    private final Random random;
    /**
    * StoreRoom class constructor initializes variables related to store rooms.
    * 
    * @param player The player.
    */
    public StoreRoom(Player player) {
        super(player,STOREROOM);
        this.heart = new Pickup(315, 300, 0);
        this.pickups.add(heart);
        random = new Random();
        int weaponType = random.nextInt(3) + 2;
        this.weapon = new Pickup(460, 303, weaponType);
        this.pickups.add(weapon);
    }
    /**
    * Updates the room.
    */
    @Override
    public void update() {
        Boolean collided = false;
        int pickupType = -1; 
        for (int i = 0; i < this.pickups.size(); i++) {
            if (player.collide(this.pickups.get(i))) {
                collided = true;
                pickupType = this.pickups.get(i).getType();
            }
        }
        if (collided) {
            // Health purchase.
            if (pickupType == 0 && this.player.getCoins() >= 3 && this.player.getHealth() < 6) {
                Collision.checkPickupCollision(this.player,this.pickups);
                this.player.removeMoney(3);
            // Machine gun purchase.
            } else if (pickupType == 2 && this.player.getCoins() >= 5 && !player.getWeaponKeys().contains(MACHINEGUN)) {
                Collision.checkPickupCollision(this.player,this.pickups);
                this.player.removeMoney(5);
            // Flamethrower purchase.
            } else if (pickupType == 3 && this.player.getCoins() >= 5 && !player.getWeaponKeys().contains(FLAMETHROWER)) {
                Collision.checkPickupCollision(this.player,this.pickups);
                this.player.removeMoney(5);
            // Shotgun purchase.
            } else if (pickupType == 4 && this.player.getCoins() >= 5 && !player.getWeaponKeys().contains(SHOTGUN)) {
                Collision.checkPickupCollision(this.player,this.pickups);
                this.player.removeMoney(5);
            }
        }
    }
    /**
    * Renders room objects.  Overridden by a specific room implementation.
    * 
    * @param g The Graphics object used by Java to render everything in the game.
    */
    @Override
    public void draw(Graphics2D g) {
        super.draw(g);
        for (int i=0; i < this.pickups.size(); i++) {
            this.pickups.get(i).draw(g);
        }
        if (!this.pickups.isEmpty()) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Courier", 1, 25));
            if (this.pickups.contains(this.heart)) {
                g.drawString("3", 508, 350);
            }
            if (this.pickups.contains(this.weapon)) {
                g.drawString("5", 665, 350);
            }
        }
    }
    /**
    * Store rooms are always clear since no enemies spawn.
    * 
    * @return True.
    */
    @Override
    public boolean RoomClear() {
        return true;
    }
}
