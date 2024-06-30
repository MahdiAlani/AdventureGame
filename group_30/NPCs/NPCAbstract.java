package NPCs;
import AdventureModel.items.AdventureObject;
import java.util.ArrayList;

public abstract class NPCAbstract
{
    /* Abstract class that creates a default NPC
     *  Contains the minimum number of methods and
     *  variables required to create a special NPC
     *  Should not be instantiated directly */

    // Variables
    int numVisits = 0;
    int id;
    ArrayList<String> prompts;
    ArrayList<AdventureObject> items;
}