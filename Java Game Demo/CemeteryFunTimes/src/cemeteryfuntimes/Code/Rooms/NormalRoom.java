package cemeteryfuntimes.Code.Rooms;
import cemeteryfuntimes.Code.Player;
import cemeteryfuntimes.Code.Shared.*;
import cemeteryfuntimes.Code.Spawn;
import java.awt.Graphics2D;
import java.util.Random;
import org.w3c.dom.NamedNodeMap;
/**
* NormalRoom class contains variables and methods related to normal rooms.
* @author David Kozloff & Tyler Law
* 
* //DKOZLOFF 06/22 New alternate constructor
*/
public final class NormalRoom extends Room implements Globals {
    protected final int key;
    public final int Key() {
        return key;
    }
    private int numSpawns;
    public int getNumSpawns() {
        return numSpawns;
    }
    
    /**
    * NormalRoom class constructor initializes variables related to normal rooms.
    * 
    * @param player  The player.
    */
    public NormalRoom (Player player) {
        super(player,NORMALROOM);
        int roomKey = new Random().nextInt(ROOMKEYS) + 1;
        this.key = roomKey;
        loadRoom(roomKey);
    }
    /**
    * Alternate constructor used to create an empty room.
    * 
    * @param player     The player.
    * @param emptyRoom  Boolean = true if room is empty.
    * 
    * //DKOZLOFF 06/22 Created this routine
    */
    public NormalRoom (Player player, Boolean emptyRoom) {
        super (player,NORMALROOM); 
        key = -1;
    }
    /**
    * Updates the room.
    */
    @Override
    public void update() {
        for (int i=0; i<spawns.size(); i++) {
            spawns.get(i).update();
        }
        super.update();
    }
    /**
    *  
    * @param g 
    */
    @Override
    public void draw(Graphics2D g) {
        //Draw pickups and enemies
        for (int i=0; i<spawns.size(); i++) { 
            spawns.get(i).draw(g);
        }
        super.draw(g);
    }
    /**
    * Determines if a room has been cleared, which is determined by the
    * specific room type.
    * 
    * @return A boolean indicating if the room has been cleared.
    */
    @Override
    public boolean RoomClear() {
        Boolean doneSpawning = false;
        int count = 0;
        for (int i=0; i<spawns.size(); i++) {
            if (spawns.get(i).getCurrentDifficulty() >= spawns.get(i).getMaxDifficulty()) {
                count++;
            }
        }
        return (enemies.isEmpty() && count==spawns.size());
    }
    /**
    * Helper method for loadRoom.  Generates an integer array of enemy keys
    * specifying which enemy types can be spawned at a given spawn point.
    * 
    * @param  enemyString   The Spawn#Enemies string given by the xml file.
    * @return enemyIntArray The integer array of enemy keys.
    */
    private int[] getSpawnEnemies(String enemyString) {
        int[] enemyIntArray;
        // The enemy string specifies specific enemies to spawn.
        if (!enemyString.isEmpty()) {
            String[] enemyStringArray = enemyString.split(",");
            enemyIntArray = new int[enemyStringArray.length];
            for (int i=0; i<enemyStringArray.length; i++) {
                enemyIntArray[i] = Integer.parseInt(enemyStringArray[i]);
            }
        // If the enemy string is empty, all standard enemies can be spawned.
        } else {
            enemyIntArray = new int[5];
            for (int i=0; i<5; i++) {
                enemyIntArray[i] = i+1;
            }
        }
        return enemyIntArray;
    }
    /* Loads the room data for a specified room variant from an xml file.
    * 
    * @param roomKey The key corresponding to a specific room variant.
    */
    private void loadRoom(int roomKey) {
        NamedNodeMap attributes = cemeteryfuntimes.Code.Shared.Utilities.loadTemplate("Rooms.xml","Room",roomKey);
        numSpawns = Integer.parseInt(attributes.getNamedItem("NumSpawns").getNodeValue());
        for (int i=0; i<numSpawns; i++) {
            int maxDifficulty = Integer.parseInt(attributes.getNamedItem("Difficulty" + Integer.toString(i+1)).getNodeValue());
            int spawnx = Integer.parseInt(attributes.getNamedItem("Spawn" + Integer.toString(i+1) + "x").getNodeValue());
            int spawny = Integer.parseInt(attributes.getNamedItem("Spawn" + Integer.toString(i+1) + "y").getNodeValue());
            int delay = Integer.parseInt(attributes.getNamedItem("Delay" + Integer.toString(i+1)).getNodeValue());
            String enemyString = attributes.getNamedItem("Spawn" + Integer.toString(i+1) + "Enemies").getNodeValue();
            int[] enemyIntArray = getSpawnEnemies(enemyString);
            spawns.add(new Spawn(this.player, this, spawnx, spawny, delay, maxDifficulty, enemyIntArray));
        }
    }
}