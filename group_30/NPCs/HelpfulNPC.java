package NPCs;
import AdventureModel.items.AdventureObject;
import AdventureModel.Player;

import java.util.ArrayList;
import java.util.Random;

public class HelpfulNPC extends NPCAbstract
{
    /* Special type of NPC that is known as the HelpfulNPC. This
     * NPC provides the user with an item
     * the task provided to them. */

    // variables
    private AdventureObject reward;
    private String taskPrompt;
    private boolean taskCompleted = false;

    ArrayList<String> prompts;
    ArrayList<AdventureObject> items;

    // constructor
    public HelpfulNPC(int id, ArrayList<String> messages, ArrayList<AdventureObject> itemList)
    {
        this.id = id;
        this.taskPrompt = messages.get(0);
        this.prompts = messages;

        // get random reward from obj list
        Random random = new Random();
        int randElement = random.nextInt(itemList.size());
        this.reward = itemList.get(randElement);

        this.items = itemList;
    }

    // methods
    public boolean getHelp(Player player)
    {
        /* Method for giving the user a reward after completing
         * a task. If the user already has the item, than
         * the function returns False, otherwise the item is added
         * and the function returns true. */
        if (!player.inventory.contains(reward))
        {
            player.inventory.add(reward);
            return true;
        }
        return false;
    }

    public String getTask()
    {
        /* Method which returns the task that the user must
         * complete iff the task is not yet completed */
        if (!taskCompleted)
            return taskPrompt;
        return "You have already completed the task!";
    }

    public void setTaskCompleted(boolean val)
    {
        /* Method which changes the value of the variable
         * responsible for checking whether the task is
         * completed or not. */
        this.taskCompleted = val;
    }

    public boolean isTaskCompleted()
    {
        /* Method which returns whether the task
         * given to the user has been completed*/
        return this.taskCompleted;
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
