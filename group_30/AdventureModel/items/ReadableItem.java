package AdventureModel.items;

import AdventureModel.Room;

public class ReadableItem extends AdventureObject {

    /**
     * Adventure Object Constructor
     * ___________________________
     * This constructor sets the name, description, and location of the object.
     *
     * @param name        The name of the Object in the game.
     * @param description One line description of the Object.
     * @param location    The location of the Object in the game.
     */
    public ReadableItem(String name, String description, Room location) {
        super(name, description, location);
        getCommands().add("read");
    }

    public void read() {
        System.out.println("You have read: " + getName());
    }
}
