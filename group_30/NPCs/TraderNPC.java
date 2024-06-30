package NPCs;
import AdventureModel.items.AdventureObject;
import AdventureModel.Player;
import java.util.ArrayList;
import java.util.Map;

public class TraderNPC extends NPCAbstract
{
    /* Special type of NPC that is known as the TraderNPC. This
     * NPC provides the user with the ability to trade their
     * items to get another */

    // variables
    private int maxTrades;
    private int currentTrades = 0;
    private double relationStrength = 1;
    private Map<AdventureObject, Integer> itemValues;

    // constructor
    public TraderNPC(int id,
                     int maxTrades,
                     ArrayList<String> messages,
                     ArrayList<AdventureObject> itemList,
                     Map<AdventureObject, Integer> inputMap)
    {
        this.id = id;
        this.maxTrades = maxTrades;
        this.prompts = messages;
        this.items = itemList;
        this.itemValues = inputMap;
    }

    // methods
    public ArrayList<AdventureObject> getAllItems()
    {
        /* Method which returns all the items that
         * are available to trade with this NPC */
        return this.items;
    }

    public boolean trade(AdventureObject newItem, AdventureObject oldItem, Player player)
    {
        /* Method which trades an item with the NPC contingent on
         * not having the max trades and the relationStrength is
         * valid enough. Returns True or False depending on the trade
         * condition.
         *
         * Precondition: Items have already been displayed to the user */
        if (currentTrades >= maxTrades)
            return false;
        if (!this.itemValues.containsKey(newItem))
            return false;
        if (!player.inventory.contains(oldItem))
            return false;

        // get value of the item
        int oldVal = this.itemValues.get(oldItem);
        int itemVal = this.itemValues.get(newItem);
        int modifier = (int)(itemVal * relationStrength);

        // item being offered is too little value
        if (oldVal < modifier)
            return false;
        player.inventory.remove(oldItem);
        player.inventory.add(newItem);
        this.relationStrength += 0.5;

        return true;
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
