package AdventureModel;

import AdventureModel.items.AdventureObject;
import AdventureModel.items.ReadableItem;
import AdventureModel.items.KeyItem;
import AdventureModel.items.StrangeItem;
import NPCs.HelpfulNPC;
import NPCs.MeanNPC;
import NPCs.TraderNPC;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Class AdventureLoader. Loads an adventure from files.
 */
public class AdventureLoader {

    private AdventureGame game; //the game to return
    private ArrayList<AdventureObject> objList = new ArrayList<AdventureObject>(); // holds objects for npcs
    private HashMap<AdventureObject, Integer> valList = new HashMap<>();
    private String adventureName; //the name of the adventure

    /**
     * Adventure Loader Constructor
     * __________________________
     * Initializes attributes
     * @param game the game that is loaded
     * @param directoryName the directory in which game files live
     */
    public AdventureLoader(AdventureGame game, String directoryName) {
        this.game = game;
        this.adventureName = directoryName;
    }

     /**
     * Load game from directory
     */
    public void loadGame() throws IOException {
        parseRooms();
        parseObjects();
        parseSynonyms();
        parseNPCs();
        this.game.setHelpText(parseOtherFile("help"));
    }

     /**
     * Parse Rooms File
     */
    private void parseRooms() throws IOException {

        int roomNumber;

        String roomFileName = this.adventureName + "/rooms.txt";
        BufferedReader buff = new BufferedReader(new FileReader(roomFileName));

        while (buff.ready()) {

            String currRoom = buff.readLine(); // first line is the number of a room

            roomNumber = Integer.parseInt(currRoom); //current room number

            // now need to get room name
            String roomName = buff.readLine();

            // now we need to get the description
            String roomDescription = "";
            String line = buff.readLine();
            while (!line.equals("-----")) {
                roomDescription += line + "\n";
                line = buff.readLine();
            }
            roomDescription += "\n";

            // now we make the room object
            Room room = new Room(roomName, roomNumber, roomDescription, adventureName);

            // now we make the motion table
            line = buff.readLine(); // reads the line after "-----"
            while (line != null && !line.equals("")) {
                String[] part = line.split(" \s+"); // have to use regex \\s+ as we don't know how many spaces are between the direction and the room number
                String direction = part[0];
                String dest = part[1];
                if (dest.contains("/")) {
                    String[] blockedPath = dest.split("/");
                    String dest_part = blockedPath[0];
                    String object = blockedPath[1];
                    Passage entry = new Passage(direction, dest_part, object);
                    room.getMotionTable().addDirection(entry);
                } else {
                    Passage entry = new Passage(direction, dest);
                    room.getMotionTable().addDirection(entry);
                }
                line = buff.readLine();
            }
            this.game.getRooms().put(room.getRoomNumber(), room);
        }

    }

     /**
     * Parse Objects File
     */
    public void parseObjects() throws IOException {

        String objectFileName = this.adventureName + "/objects.txt";
        BufferedReader buff = new BufferedReader(new FileReader(objectFileName));

        while (buff.ready()) {
            String objectName = buff.readLine();
            String objectDescription = buff.readLine();
            String objectLocation = buff.readLine();
            String type = buff.readLine();
            String val = buff.readLine();
            int intVal = Integer.parseInt(val);
            String separator = buff.readLine();
            if (separator != null && !separator.isEmpty())
                System.out.println("Formatting Error!");
            int i = Integer.parseInt(objectLocation);
            Room location = this.game.getRooms().get(i);

            // Creating correct item type
            AdventureObject object = null;
            switch (type) {
                case "StrangeItem":
                    object = new StrangeItem(objectName, objectDescription, location);
                    this.objList.add(object);
                    this.valList.put(object, intVal);
                    break;
                case "KeyItem":
                    object = new KeyItem(objectName, objectDescription, location);
                    this.objList.add(object);
                    this.valList.put(object, intVal);
                    break;
                case "Readable":
                    object = new ReadableItem(objectName, objectDescription, location);
                    this.objList.add(object);
                    this.valList.put(object, intVal);
                    break;
            }
            location.addGameObject(object);
        }

    }

     /**
     * Parse Synonyms File
     */
    public void parseSynonyms() throws IOException {
        String synonymsFileName = this.adventureName + "/synonyms.txt";
        BufferedReader buff = new BufferedReader(new FileReader(synonymsFileName));
        String line = buff.readLine();
        while(line != null){
            String[] commandAndSynonym = line.split("=");
            String command1 = commandAndSynonym[0];
            String command2 = commandAndSynonym[1];
            this.game.getSynonyms().put(command1,command2);
            line = buff.readLine();
        }

    }

    public void parseNPCs() throws IOException
    {
        /** Read the NPCs text first and then give them
         *  random objects based on the objects.txt file. Afterwards,
         *  select one NPC randomly and place it in a room specified by the file.
         *
         *  Precondition: npc_list.txt is in the correct format meaning
         *  type
         *  id/room num
         *  10 Prompts
         *  obj_list
         *  bool, (if needed)
         *  */

        // Create reader and read file
        String fileName = this.adventureName + "/npc_list.txt";
        BufferedReader buff = new BufferedReader(new FileReader(fileName));
        String line = buff.readLine();
        ArrayList<String> info = new ArrayList<String>();
        while (line != null)
        {
            if (line.strip().equals(""))
                info.add("---");
            else
                info.add(line);
            line = buff.readLine();
        }

        // Create NPCS
        Random random = new Random();
        int chooser = random.nextInt(3) + 1; // Numbers 1-3
        int base = 14 * (chooser - 1);
        String npcType = info.get(base);

        // add prompts and objects
        ArrayList<AdventureObject> obj = new ArrayList<AdventureObject>();
        ArrayList<String> prompts = new ArrayList<String>();
        int roomNum = Integer.parseInt(info.get(base + 1));
        for (int i = base + 2; i < base + 12; i++)
            prompts.add(info.get(i));

        // create new mean npc
        if (npcType.toLowerCase().strip().equals("meannpc"))
        {
            MeanNPC meanNPC = new MeanNPC(roomNum,
                    prompts,
                    obj,
                    true);
            this.game.getRooms().get(roomNum).setNPC(meanNPC);
            this.game.getRooms().get(roomNum).npcType = 0;
        }

        // create helpful npc
        else if (npcType.toLowerCase().strip().equals("helpfulnpc"))
        {
            HelpfulNPC helpfulNPC = new HelpfulNPC(roomNum,
                    prompts,
                    this.objList);
            this.game.getRooms().get(roomNum).setNPC(helpfulNPC);
            this.game.getRooms().get(roomNum).npcType = 2;
        }

        // create tradernpc
        else
        {
            TraderNPC traderNPC = new TraderNPC(roomNum,
                    5,
                    prompts,
                    this.objList, this.valList);
            this.game.getRooms().get(roomNum).setNPC(traderNPC);
            this.game.getRooms().get(roomNum).npcType = 1;
        }
    }

    /**
     * Parse Files other than Rooms, Objects and Synonyms
     *
     * @param fileName the file to parse
     */
    public String parseOtherFile(String fileName) throws IOException {
        String text = "";
        fileName = this.adventureName + "/" + fileName + ".txt";
        BufferedReader buff = new BufferedReader(new FileReader(fileName));
        String line = buff.readLine();
        while (line != null) { // while not EOF
            text += line+"\n";
            line = buff.readLine();
        }
        return text;
    }

}
