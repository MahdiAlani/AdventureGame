package NPCs;
import AdventureModel.items.AdventureObject;
import AdventureModel.Player;
import java.util.Random;
import java.util.ArrayList;

public class MeanNPC extends NPCAbstract
{
    /* Special type of NPC that is known as the TraderNPC. This
     * NPC does not provide the user with much other than funny
     * quips. Will take the users' items depending on the
     * difficulty. */

    // variables
    private boolean hardMode;
    ArrayList<String> prompts;
    ArrayList<AdventureObject> items;

    // constructor
    public MeanNPC(int id,
                   ArrayList<String> messages,
                   ArrayList<AdventureObject> itemList,
                   boolean isHardMode)
    {
        this.id = id;
        this.prompts = messages;
        this.items = itemList;
        this.hardMode = isHardMode;
    }

    public String takeItem(Player player)
    {
        /* Takes a specific item from the user which cannot
         * be given back, (yes it may end the game for the
         * user) */
        if (player.inventory.size() == 0)
            return "You got luck you had nothing!.";
        Random random = new Random();
        int idx = random.nextInt(player.inventory.size());

        String itemName = player.inventory.get(idx).getName();
        player.inventory.remove(player.inventory.get(idx));
        return ("Oh you got a really nice inventory, would be a shame if someone took your: " + itemName);
    }

    // methods
    public String speak()
    {
        /* Returns a String of a message to
         * say to the user */
        if (numVisits == prompts.size())
            this.numVisits = 1;
        String out = this.prompts.get(this.numVisits);
        this.numVisits++;
        return out;
    }
}
