package cemeteryfuntimes.Code.Rooms;
import cemeteryfuntimes.Code.Bosses.*;
import cemeteryfuntimes.Code.Player;
import cemeteryfuntimes.Code.Shared.Globals;
import cemeteryfuntimes.Code.Shared.ImageLoader;
import java.awt.Graphics2D;
import java.util.Random;
/**
* BossRoom class contains variables and methods related to boss rooms.
* @author David Kozloff & Tyler Law
* 
* //DKOZLOFF 06/22 Move boss creation code to room entered. Draw portal.
*/
public final class BossRoom extends Room implements Globals {
    private Boss boss;
    public Boss getBoss() {
        return boss;
    }
    private final int bossKey;
    private static final int portalX=(int)(GAMEBORDER+PORTALX-PORTALSIZE/2); //DKOZLOFF+1 06/22
    private static final int portalY=(int)(PORTALY-PORTALSIZE/2);
    /**
    * BossRoom class constructor initializes variables related to boss rooms.
    * 
    * @param player  The player.
    * 
    * //DKOZLOFF 06/22 Move boss creation code to room entered.
    */
    public BossRoom(Player player) {
        super(player,BOSSROOM);
        Random random = new Random();
        bossKey = random.nextInt(BOSSES);
    }
    /**
    * Updates the room.
    */
    @Override
    public void update() {
        boss.update();
        super.update();
    }
    /**
    * Determines if a room has been cleared, which is determined by the
    * specific room type.
    * 
    * @return A boolean indicating if the room has been cleared.
    */
    @Override
    public boolean RoomClear() {
        return boss.isDead();
    }
    /**
    * Create the boss upon entering the room.
    * 
    * //DKOZLOFF Created routine.
    */
    @Override
    public void RoomEntered() {
        if (boss == null) {
            switch(bossKey) {
                case BATLORD: 
                    boss = new BatLord(player,this);
                    break;
                case GHOULIE:
                    boss = new Ghoulie(player);
                    break;
                default:
                    boss = null;
                    break;
            }
        }
    }
    /**
    * Renders room objects.  Overridden by a specific room implementation.
    * 
    * @param g The Graphics object used by Java to render everything in the game.
    * 
    * //DKOZLOFF 06/22 Draw portal upon room clear.
    */
    @Override
    public void draw(Graphics2D g) {
        boss.draw(g); 
        if (RoomClear()) { //DKOZLOFF+2 06/22
            g.drawImage(ImageLoader.getImage("General/portal.png",0),portalX,portalY, null); 
        }
        super.draw(g);
    }
}