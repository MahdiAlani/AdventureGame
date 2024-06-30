package AdventureModel.items;

import AdventureModel.Room;

public class KeyItem extends AdventureObject {

    /**
     * Adventure Object Constructor
     * ___________________________
     * This constructor sets the name, description, and location of the object.
     *
     * @param name        The name of the Object in the game.
     * @param description One line description of the Object.
     * @param location    The location of the Object in the game.
     */
    public KeyItem(String name, String description, Room location) {
        super(name, description, location);
        getCommands().add("describe");
    }

    public void describe() {
        System.out.println(getDescription());
    }
}
