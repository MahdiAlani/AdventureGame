package AdventureModel.items;

import AdventureModel.Room;

public class StrangeItem extends AdventureObject {

    /**
     * Adventure Object Constructor
     * ___________________________
     * This constructor sets the name, description, and location of the object.
     *
     * @param name        The name of the Object in the game.
     * @param description One line description of the Object.
     * @param location    The location of the Object in the game.
     */
    public StrangeItem(String name, String description, Room location) {
        super(name, description, location);
        getCommands().add("observe");
    }


    public void observe() {
        System.out.println(getDescription());
    }
}
