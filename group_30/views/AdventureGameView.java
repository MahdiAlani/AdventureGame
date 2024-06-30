package views;

import AdventureModel.AdventureGame;
import AdventureModel.Room;
import AdventureModel.items.AdventureObject;
import NPCs.HelpfulNPC;
import NPCs.MeanNPC;
import NPCs.NPCAbstract;
import NPCs.TraderNPC;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.layout.*;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import javafx.scene.AccessibleRole;

import java.awt.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

/**
 * Class AdventureGameView.
 *
 * This is the Class that will visualize your model.
 * You are asked to demo your visualization via a Zoom
 * recording. Place a link to your recording below.
 *
 * ZOOM LINK: <https://utoronto-my.sharepoint.com/:v:/r/personal/simina_wang_mail_utoronto_ca/Documents/video1289524809.mp4?csf=1&web=1&e=iHtOpZ&nav=eyJyZWZlcnJhbEluZm8iOnsicmVmZXJyYWxBcHAiOiJTdHJlYW1XZWJBcHAiLCJyZWZlcnJhbFZpZXciOiJTaGFyZURpYWxvZyIsInJlZmVycmFsQXBwUGxhdGZvcm0iOiJXZWIiLCJyZWZlcnJhbE1vZGUiOiJ2aWV3In19>
 * PASSWORD: <PASSWORD HERE>
 */
public class AdventureGameView {

    AdventureGame model; //model of the game
    Stage stage; //stage on which all is rendered
    Button saveButton, loadButton, helpButton, interactButton; //buttons
    Button offer, trade;
    Boolean trading = false;
    Boolean helpToggle = false; //is help on display?

    HBox topButtons = new HBox();
    HBox midButtons = new HBox();
    GridPane gridPane = new GridPane();
    GridPane innerGridPane = new GridPane();
    GridPane innerGridPane2 = new GridPane(); //to hold images and buttons
    Label roomDescLabel = new Label(); //to hold room description and/or instructions
    Label text;
    VBox objectsInRoom = new VBox(); //to hold room items
    VBox objectsInInventory = new VBox(); //to hold inventory items
    ImageView roomImageView; //to hold room image
    TextField inputTextField; //for user input
    TextField playerTrades, npcInventory;


    private MediaPlayer mediaPlayer; //to play audio
    private boolean mediaPlaying; //to know if the audio is playing

    /**
     * Adventure Game View Constructor
     * __________________________
     * Initializes attributes
     */
    public AdventureGameView(AdventureGame model, Stage stage) {
        this.model = model;
        this.stage = stage;
        intiUI();
    }

    /**
     * Initialize the UI
     */
    public void intiUI() {

        // setting up the stage
        this.stage.setTitle("Group 30's Adventure Game"); //Replace <YOUR UTORID> with your UtorID

        //Inventory + Room items
        objectsInRoom.setSpacing(10);
        objectsInRoom.setAlignment(Pos.TOP_CENTER);
        objectsInInventory.setSpacing(10);
        objectsInInventory.setAlignment(Pos.TOP_CENTER);

        // GridPane, anyone?
        gridPane.setPadding(new Insets(20));
        gridPane.setBackground(new Background(new BackgroundFill(
                Color.valueOf("#000000"),
                new CornerRadii(0),
                new Insets(0)
        )));

        //Three columns, three rows for the GridPane
        ColumnConstraints column1 = new ColumnConstraints(150);
        ColumnConstraints column2 = new ColumnConstraints(650);
        ColumnConstraints column3 = new ColumnConstraints(150);
        column3.setHgrow( Priority.SOMETIMES ); //let some columns grow to take any extra space
        column1.setHgrow( Priority.SOMETIMES );

        // Row constraints
        RowConstraints row1 = new RowConstraints();
        RowConstraints row2 = new RowConstraints( 550 );
        RowConstraints row3 = new RowConstraints();
        row1.setVgrow( Priority.SOMETIMES );
        row3.setVgrow( Priority.SOMETIMES );
        gridPane.getColumnConstraints().addAll( column1 , column2 , column1 );
        gridPane.getRowConstraints().addAll( row1 , row2 , row1 );

        // Buttons
        saveButton = new Button("Save");
        saveButton.setId("Save");
        customizeButton(saveButton, 100, 50);
        makeButtonAccessible(saveButton, "Save Button", "This button saves the game.", "This button saves the game. Click it in order to save your current progress, so you can play more later.");
        addSaveEvent();

        loadButton = new Button("Load");
        loadButton.setId("Load");
        customizeButton(loadButton, 100, 50);
        makeButtonAccessible(loadButton, "Load Button", "This button loads a game from a file.", "This button loads the game from a file. Click it in order to load a game that you saved at a prior date.");
        addLoadEvent();

        helpButton = new Button("Instructions");
        helpButton.setId("Instructions");
        customizeButton(helpButton, 200, 50);
        makeButtonAccessible(helpButton, "Help Button", "This button gives game instructions.", "This button gives instructions on the game controls. Click it to learn how to play.");
        addInstructionEvent();

        // Add interact with NPC option if there is one in the room
        // Otherwise, remove the interact button if it exists
        interactButton = new Button("Interact");
        interactButton.setId("Interact");
        customizeButton(interactButton, 200, 50);
        makeButtonAccessible(interactButton, "Interact",
                "This button allows you to interact with NPC.",
                "This button allows you to interact with the NPC depending on their specific function.");
        addInteractEvent();

        topButtons.getChildren().addAll(saveButton, helpButton, loadButton);
        topButtons.setSpacing(10);
        topButtons.setAlignment(Pos.CENTER);

        inputTextField = new TextField();
        inputTextField.setFont(new Font("Arial", 16));
        inputTextField.setFocusTraversable(true);

        // text field for trader npc
        npcInventory = new TextField();
        npcInventory.setFont(new Font("Arial", 16));
        npcInventory.setFocusTraversable(true);
        npcInventory.setMaxWidth(150);
        playerTrades = new TextField();
        playerTrades.setFont(new Font("Arial", 16));
        playerTrades.setFocusTraversable(true);
        playerTrades.setMaxWidth(150);

        inputTextField.setAccessibleRole(AccessibleRole.TEXT_AREA);
        inputTextField.setAccessibleRoleDescription("Text Entry Box");
        inputTextField.setAccessibleText("Enter commands in this box.");
        inputTextField.setAccessibleHelp("This is the area in which you can enter commands you would like to play.  Enter a command and hit return to continue.");
        addTextHandlingEvent(); //attach an event to this input field

        //labels for inventory and room items
        Label objLabel =  new Label("Objects in Room");
        objLabel.setAlignment(Pos.CENTER);
        objLabel.setStyle("-fx-text-fill: white;");
        objLabel.setFont(new Font("Arial", 16));

        Label invLabel =  new Label("Your Inventory");
        invLabel.setAlignment(Pos.CENTER);
        invLabel.setStyle("-fx-text-fill: white;");
        invLabel.setFont(new Font("Arial", 16));

        //add all the widgets to the GridPane
        gridPane.add( topButtons, 1, 0, 1, 1 );  // Add buttons
        gridPane.add( invLabel, 2, 0, 1, 1 );  // Add label

        Label commandLabel = new Label("What would you like to do?");
        commandLabel.setStyle("-fx-text-fill: white;");
        commandLabel.setFont(new Font("Arial", 16));

        updateScene(""); //method displays an image and whatever text is supplied
        updateItems(); //update items shows inventory and objects in rooms

        // adding the text area and submit button to a VBox
        VBox textEntry = new VBox();
        textEntry.setStyle("-fx-background-color: #000000;");
        textEntry.setPadding(new Insets(20, 20, 20, 20));
        textEntry.getChildren().addAll(commandLabel, inputTextField);
        textEntry.setSpacing(10);
        textEntry.setAlignment(Pos.CENTER);
        gridPane.add( textEntry, 0, 2, 3, 1 );

        // Render everything
        var scene = new Scene( gridPane ,  1000, 800);

        // Check on screen clicks for drawers and objects
        scene.setOnMouseClicked(event -> {
            // A location has been clicked
            Boolean click = false;
            double[] coordinates = new double[2];

            //Gets x and y coordinates
            double x = event.getSceneX();
            double y = event.getSceneY();
            coordinates[0] = x;
            coordinates[1] = y;

            //Closed Drawer
            if (model.player.getCurrentRoom() == model.getRooms().get(15)) {
                //Drawer
                double[] drawer = new double[]{424, 544, 154, 454};
                openCabinet(coordinates, drawer, 6);
                click = true;
            }

            //Open Drawer
            if (model.player.getCurrentRoom() == model.getRooms().get(9)) {
                //Drawer
                double[] drawer = new double[]{295, 433, 200, 375};
                clickDrawer(coordinates, drawer,  15, 6);
                click = true;
            }

            // A cabinet/drawer has been clicked on, no need to check for an item
            if (!click) {
                //Key in the drawer
                double[] key = new double[]{402, 480, 237, 300};
                takeItem(coordinates,key, "key");

                //Chest on the table
                double[] chest = new double[]{627, 659, 338, 360};
                takeItem(coordinates,chest, "chest");

                //note on the table
                double[] note = new double[]{430, 514, 436, 458};
                takeItem(coordinates,note , "note");

                //bird drawing on the table
                double[] bird = new double[]{474, 484, 316, 325};
                takeItem(coordinates, bird, "bird");

                //An important book
                double[] book = new double[]{605, 620, 366, 386};
                takeItem(coordinates, book, "book");
            }
        });

        scene.setFill(Color.BLACK);
        this.stage.setScene(scene);
        this.stage.setResizable(false);
        this.stage.show();
    }

    /**
     * Helper method to open cabinet
     * Checks if cabinet has been clicked on, if yes, moves to cabinet room
     */
    private void openCabinet(double[] click, double[] coordinates, int cabinet) {
        //Check if object has been clicked
        if ( coordinates[0] <= click[0] && click[1] <= coordinates[1]
                 && coordinates[2] <= click[0] && click[1] <= coordinates[3]) {

            // Take the object and update
            int roomNum = this.model.player.getCurrentRoom().getRoomNumber();
            if (roomNum != 6 && roomNum != 15)
                return;
            model.player.setCurrentRoom(model.getRooms().get(cabinet));
            model.getRooms().get(cabinet).visit();
            updateScene("");
        }
        else
        {
            return;
        }
    }

    /**
     * Helper method for clicking on drawers
     * Checks if drawer has been clicked on, if yes, moves to correct drawer room
     */
    private void clickDrawer(double[] click, double[] coordinates, int closedDrawer, int openDrawer) {
        // If location is clicked
        if (coordinates[0] <= click[0] && click[1] <= coordinates[1]
                 && coordinates[2] <= click[0] && click[1] <= coordinates[3]) {
            // If the drawer has been opened, go to open drawer
            if (!model.getRooms().get(openDrawer).getVisited())
                model.player.setCurrentRoom(model.getRooms().get(closedDrawer));
            else
                model.player.setCurrentRoom(model.getRooms().get(openDrawer));
            updateScene("");
        }
    }


    /**
     * Helper method for picking up items
     * Checks if mouseclick is in correct location to take item from room
     */
    private void takeItem(double[] click, double[] coordinates, String itemName) {
        //Check if object has been clicked
        if (coordinates[0] <= click[0] && click[1] <= coordinates[1]
                && coordinates[2] <= click[0] && click[1] <= coordinates[3]) {
            // Take the object and update
            model.player.takeObject(itemName);
            updateItems();
            updateScene("");
        }
    }

    /**
     * makeButtonAccessible
     * __________________________
     * For information about ARIA standards, see
     * https://www.w3.org/WAI/standards-guidelines/aria/
     *
     * @param inputButton the button to add screenreader hooks to
     * @param name ARIA name
     * @param shortString ARIA accessible text
     * @param longString ARIA accessible help text
     */
    public static void makeButtonAccessible(Button inputButton, String name, String shortString, String longString) {
        inputButton.setAccessibleRole(AccessibleRole.BUTTON);
        inputButton.setAccessibleRoleDescription(name);
        inputButton.setAccessibleText(shortString);
        inputButton.setAccessibleHelp(longString);
        inputButton.setFocusTraversable(true);
    }

    /**
     * customizeButton
     * __________________________
     *
     * @param inputButton the button to make stylish :)
     * @param w width
     * @param h height
     */
    private void customizeButton(Button inputButton, int w, int h) {
        inputButton.setPrefSize(w, h);
        inputButton.setFont(new Font("Arial", 16));
        inputButton.setStyle("-fx-background-color: #17871b; -fx-text-fill: white;");
    }

    /**
     * addTextHandlingEvent
     * __________________________
     * Add an event handler to the myTextField attribute 
     *
     * Your event handler should respond when users 
     * hits the ENTER or TAB KEY. If the user hits 
     * the ENTER Key, strip white space from the
     * input to myTextField and pass the stripped 
     * string to submitEvent for processing.
     *
     * If the user hits the TAB key, move the focus 
     * of the scene onto any other node in the scene 
     * graph by invoking requestFocus method.
     */
    private void addTextHandlingEvent() {
        inputTextField.setOnKeyPressed(k -> {
            if (k.getCode() == KeyCode.ENTER) {
                String input = inputTextField.getText();
                input = input.strip();
                submitEvent(input);
                inputTextField.setText(""); // clear text field for next instruction
            }
            else if (k.getCode() == KeyCode.TAB) {
                roomImageView.requestFocus(); // move focus to the image of the room
            }
        });
    }


    /**
     * submitEvent
     * __________________________
     *
     * @param text the command that needs to be processed
     */
    private void submitEvent(String text) {
        text = text.strip(); //get rid of white space
        stopArticulation(); //if speaking, stop

        if (text.equalsIgnoreCase("LOOK") || text.equalsIgnoreCase("L")) {
            String roomDesc = this.model.getPlayer().getCurrentRoom().getRoomDescription();
            String objectString = this.model.getPlayer().getCurrentRoom().getObjectString();
            if (!objectString.isEmpty()) roomDescLabel.setText(roomDesc + "\n\nObjects in this room:\n" + objectString);
            return;
        } else if (text.equalsIgnoreCase("HELP") || text.equalsIgnoreCase("H")) {
            showInstructions();
            return;
        } else if (text.equalsIgnoreCase("COMMANDS") || text.equalsIgnoreCase("C")) {
            showCommands(); //this is new!  We did not have this command in A1
            return;
        }

        //try to move!
        String output = this.model.interpretAction(text); //process the command!

        if (output == null || (!output.equals("GAME OVER") && !output.equals("FORCED") && !output.equals("HELP"))) {
            updateScene(output);
            updateItems();
        } else if (output.equals("GAME OVER")) {
            updateScene("");
            updateItems();
            PauseTransition pause = new PauseTransition(Duration.seconds(10));
            pause.setOnFinished(event -> {
                Platform.exit();
            });
            pause.play();
        } else if (output.equals("FORCED")) {
            inputTextField.setEditable(false); // disables the text field so commands cannot be written
            objectsInInventory.getChildren().forEach(b -> b.setDisable(true));
            objectsInRoom.getChildren().forEach(b -> b.setDisable(true));

            updateScene("");
            updateItems();

            PauseTransition pause = new PauseTransition(Duration.seconds(5));
            pause.setOnFinished(next -> {
                inputTextField.setEditable(true); // enable the text field after pause
                objectsInRoom.getChildren().forEach(b -> b.setDisable(false));
                objectsInInventory.getChildren().forEach(b -> b.setDisable(false));
                submitEvent("FORCED"); // move player to FORCED room
            });
            pause.play();
        }
    }


    /**
     * showCommands
     * __________________________
     *
     * update the text in the GUI (within roomDescLabel)
     * to show all the moves that are possible from the 
     * current room.
     */
    private void showCommands() {
        String commands = this.model.getPlayer().getCurrentRoom().getCommands();
        roomDescLabel.setText("The possible commands for this room are: \n" + commands);
    }


    /**
     * updateScene
     * __________________________
     *
     * Show the current room, and print some text below it.
     * If the input parameter is not null, it will be displayed
     * below the image.
     * Otherwise, the current room description will be displayed
     * below the image.
     * 
     * @param textToDisplay the text to display below the image.
     */
    public void updateScene(String textToDisplay) {

        getRoomImage(); //get the image of the current room
        formatText(textToDisplay); //format the text to display
        roomDescLabel.setPrefWidth(500);
        roomDescLabel.setPrefHeight(500);
        roomDescLabel.setTextOverrun(OverrunStyle.CLIP);
        roomDescLabel.setWrapText(true);
        VBox roomPane = new VBox(roomImageView,roomDescLabel);
        roomPane.setPadding(new Insets(10));
        roomPane.setAlignment(Pos.TOP_CENTER);
        roomPane.setStyle("-fx-background-color: #000000;");

        if (!this.model.player.getCurrentRoom().hasNPC) {
            if (this.topButtons.getChildren().contains(interactButton))
                this.topButtons.getChildren().remove(interactButton);
        }
        else {
            if (!this.topButtons.getChildren().contains(interactButton))
                this.topButtons.getChildren().add(interactButton);
        }


        gridPane.add(roomPane, 1, 1);
        stage.sizeToScene();
    }

    /**
     * formatText
     * __________________________
     *
     * Format text for display.
     * 
     * @param textToDisplay the text to be formatted for display.
     */
    private void formatText(String textToDisplay) {
        if (textToDisplay == null || textToDisplay.isBlank()) {
            String roomDesc = this.model.getPlayer().getCurrentRoom().getRoomDescription() + "\n";
            String objectString = this.model.getPlayer().getCurrentRoom().getObjectString();
            if (objectString != null && !objectString.isEmpty()) roomDescLabel.setText(roomDesc + "\n\nObjects in this room:\n" + objectString);
            else roomDescLabel.setText(roomDesc);
        } else roomDescLabel.setText(textToDisplay);
        roomDescLabel.setStyle("-fx-text-fill: white;");
        roomDescLabel.setFont(new Font("Arial", 16));
        roomDescLabel.setAlignment(Pos.CENTER);
    }

    /**
     * getRoomImage
     * __________________________
     *
     * Get the image for the current room and place 
     * it in the roomImageView 
     */
    private void getRoomImage() {

        int roomNumber = this.model.getPlayer().getCurrentRoom().getRoomNumber();
        String roomImage = this.model.getDirectoryName() + "/room-images/" + roomNumber + ".png";

        Image roomImageFile = new Image(roomImage);
        roomImageView = new ImageView(roomImageFile);
        roomImageView.setPreserveRatio(true);
        roomImageView.setFitWidth(400);
        roomImageView.setFitHeight(400);

        //set accessible text
        roomImageView.setAccessibleRole(AccessibleRole.IMAGE_VIEW);
        roomImageView.setAccessibleText(this.model.getPlayer().getCurrentRoom().getRoomDescription());
        roomImageView.setFocusTraversable(true);
    }

    /**
     * updateItems
     * __________________________
     *
     * This method is partially completed, but you are asked to finish it off.
     *
     * The method should populate the objectsInInventory Vbox.
     * Each Vbox should contain a collection of nodes (Buttons, ImageViews, you can decide)
     * Each node represents a different object.
     * 
     * Images of each object are in the assets 
     * folders of the given adventure game.
     */
    public void updateItems() {
        //adds images of objects in a given room to the objectsInRoom Vbox
        ObservableList<Button> result = FXCollections.observableArrayList();
        ArrayList<AdventureObject> inRoom = this.model.getPlayer().getCurrentRoom().objectsInRoom;
        for (AdventureObject o: inRoom) {
            Image picture = new Image(this.model.getDirectoryName() + "/objectImages/" + o.getName() + ".jpg");
            ImageView display = new ImageView(picture);
            display.setFitWidth(100);
            display.setPreserveRatio(true);
            display.setAccessibleText("This is an image of a(n) " + o.getName());

            Button object = new Button(o.getName(), display); // create button
            object.setId(o.getName());
            object.setContentDisplay(ContentDisplay.TOP);
            object.setFont(new Font("Arial", 12));
            makeButtonAccessible(object, "Item button", "This is an item button ","This is an item button. Click it to move the " + o.getName() + " into your inventory");
            // only allow dropping or picking up and item when player is not trading
            object.setOnAction(e -> {
                if (!trading)
                    submitEvent("TAKE " + object.getId());
            });
            result.add(object);

        }
        objectsInRoom.getChildren().setAll(result);

        //adds images of objects in a player's inventory room to the objectsInInventory Vbox
        ObservableList<Button> inventory = FXCollections.observableArrayList();
        ArrayList<AdventureObject> inInventory = this.model.getPlayer().inventory;
        for (AdventureObject o: inInventory) {
            Image picture = new Image(this.model.getDirectoryName() + "/objectImages/" + o.getName() + ".jpg");
            ImageView display = new ImageView(picture);
            display.setFitWidth(100);
            display.setPreserveRatio(true);
            display.setAccessibleText("This is an image of a(n) " + o.getName());

            Button object = new Button(o.getName(), display);
            // Add right click functionality to objects in inventory
            rightClickButton(o, object);
            object.setId(o.getName());
            object.setContentDisplay(ContentDisplay.TOP);
            object.setFont(new Font("Arial", 12));
            makeButtonAccessible(object, "Item button", "This is an item button", "This is an item button. Click it to drop the " + o.getName() + " into the room");

            // only allow dropping or picking up and item when player is not trading
            object.setOnAction(e -> {
                if (!trading)
                    submitEvent("DROP " + object.getId());
            });
            inventory.add(object);
        }
        objectsInInventory.getChildren().setAll(inventory);

        ScrollPane scI = new ScrollPane(objectsInInventory);
        scI.setFitToWidth(true);
        scI.setStyle("-fx-background: #000000; -fx-background-color:transparent;");
        gridPane.add(scI,2,1);


    }

    /**rightClickButton
     * Adds right click functionality to objects in inventory
     * Right clicking an object the player has picked up shows the possible commands for the user
     * If a command is selected, it is run for that specific object
     */
    private void rightClickButton(AdventureObject object, Button button) {
        // Create a context menu
        ContextMenu contextMenu = new ContextMenu();

        for (String command: object.getCommands()) {
            MenuItem option = new MenuItem(command);
            option.setOnAction(e -> {
                Method method = null;
                // Getting the correct command
                try {
                    method = object.getClass().getMethod(command.toLowerCase());
                } catch (NoSuchMethodException ex) {
                    throw new RuntimeException(ex);
                }

                //Running the correct command
                try {
                    method.invoke(object);
                } catch (IllegalAccessException | InvocationTargetException ex) {
                    throw new RuntimeException(ex);
                }

            });
            contextMenu.getItems().add(option);
        }

        // Show context menu on right-click
        button.setOnContextMenuRequested(e -> {
            contextMenu.show(button, e.getScreenX(), e.getScreenY());
        });
    }

    /*
     * Show the game instructions.
     *
     * If helpToggle is FALSE:
     * -- display the help text in the CENTRE of the gridPane (i.e. within cell 1,1)
     * -- use whatever GUI elements to get the job done!
     * -- set the helpToggle to TRUE
     * -- REMOVE whatever nodes are within the cell beforehand!
     *
     * If helpToggle is TRUE:
     * -- redraw the room image in the CENTRE of the gridPane (i.e. within cell 1,1)
     * -- set the helpToggle to FALSE
     * -- Again, REMOVE whatever nodes are within the cell beforehand!
     */
    public void showInstructions() {
        if (!helpToggle) {
            roomImageView.imageProperty().set(null);
            roomDescLabel.setText("");

            String instructions = model.getInstructions();
            Label helpText = new Label(instructions);

            helpText.setWrapText(true);
            helpText.setStyle("-fx-text-fill: white;");
            helpText.setFont(new Font("Arial", 15));

            gridPane.add(helpText, 1, 1, 1, 1);
            helpToggle = true;
        }
        else {
            gridPane.getChildren().remove(1, 1); // remove items from centre pane
            helpToggle = false;
            updateScene("");
        }
    }

    /** Method for showing a piece of diagloue
     *  with an NPC, whether that be opening a trading menu
     * or simply outputting a message
     */
    public void showNPCDialogue()
    {
        if (!helpToggle)
        {
            // vars/default values
            roomImageView.imageProperty().set(null);
            roomDescLabel.setText("");
            NPCAbstract np = model.player.getCurrentRoom().getNPC();
            TraderNPC tnpc;
            HelpfulNPC hnpc;
            MeanNPC mnpc;
            String diagl;
            int npcType = model.player.getCurrentRoom().npcType;

            // trader village in the game
            if (npcType == 1) {
                this.trading = true;
                // Dialogue
                tnpc = (TraderNPC) np;
                diagl = tnpc.speak() + " \n Left side is what you want from me. I have everything. \n Right side is what " +
                        "you want to offer. \n I will only take one item at a time and the more we trade, the more lenient " +
                        "I will be. ";

                // generate textfield and buttons
                innerGridPane = new GridPane();
                innerGridPane.setPadding(new Insets(20));
                innerGridPane.add(npcInventory, 0, 1);
                innerGridPane2 = new GridPane();
                innerGridPane2.setPadding(new Insets(20));
                innerGridPane2.add(playerTrades, 0, 1);
                gridPane.add(innerGridPane, 0, 2);
                gridPane.add(innerGridPane2, 2, 2);

                trade = new Button("Trade");
                trade.setId("Trade");
                customizeButton(trade, 200, 50);
                makeButtonAccessible(trade, "Trade",
                        "This button allows you to interact with NPC.",
                        "This button allows you to interact with the NPC depending on their specific function.");
                addTradeEvent();
                midButtons = new HBox();
                midButtons.getChildren().addAll(trade);
                midButtons.setSpacing(10);
                midButtons.setAlignment(Pos.BASELINE_LEFT);
                gridPane.add(midButtons, 1, 2, 1 ,1 );
            }
            // helpfulnpc
            else if (npcType == 2) {
                hnpc = (HelpfulNPC) np;
                diagl = hnpc.speak();

                // Create button to offer up treasure
                offer = new Button("Offer");
                offer.setId("Offer");
                customizeButton(offer, 200, 50);
                makeButtonAccessible(offer, "Offer",
                        "This button allows you to interact with NPC.",
                        "This button allows you to interact with the NPC depending on their specific function.");
                addOfferEvent();

                // New gridpane
                innerGridPane = new GridPane();
                innerGridPane.setPadding(new Insets(20));
                ColumnConstraints column1 = new ColumnConstraints(520);
                ColumnConstraints column2 = new ColumnConstraints(650);
                RowConstraints row1 = new RowConstraints(14);
                RowConstraints row2 = new RowConstraints( 550 );
                innerGridPane.getColumnConstraints().addAll( column1 , column2 , column1 );
                innerGridPane.getRowConstraints().addAll( row1 , row2 , row1 );

                // add button to second gridpane
                midButtons.getChildren().addAll(offer);
                midButtons.setSpacing(10);
                midButtons.setAlignment(Pos.CENTER);
                innerGridPane.add(midButtons, 0, 2, 1 ,1 );
                gridPane.add(innerGridPane, 1, 1, 1, 1 );
            }
            // mean npc
            else {
                mnpc = (MeanNPC) np;
                diagl = mnpc.speak();

                // Decide whether or not to take user's item
                Random randGenerator = new Random();
                int randomNum = randGenerator.nextInt(10) + 1;
                if (randomNum > 0) {
                    diagl = mnpc.takeItem(this.model.player);
                    updateItems();
                }
            }
            // add text to center of screen
            text = new Label(diagl);
            text.setStyle("-fx-text-fill: white;");
            text.setFont(new Font("Arial", 15));
            gridPane.add(text, 1, 1);
            helpToggle = true;
        }
        else {
            // remove everything from the gridpane
            this.trading = false;
            midButtons.getChildren().removeAll();
            gridPane.getChildren().remove(midButtons);
            gridPane.getChildren().remove(text);
            gridPane.getChildren().remove(innerGridPane);
            gridPane.getChildren().remove(innerGridPane2);
            gridPane.getChildren().remove(1, 1); // remove items from centre pane
            helpToggle = false;
            updateScene("");
        }
    }

    /**
     * This method handles the event related to
     * the trade button
     */
    public void addTradeEvent()
    {
        trade.setOnAction(e -> {
            stopArticulation();

            // Check if items exists, if so then correlate them to their actual object
            AdventureObject old = null;
            AdventureObject newObj = null;
            String oldName = this.playerTrades.getText().strip().toLowerCase();
            String newName = this.npcInventory.getText().strip().toLowerCase();
            TraderNPC traderNPC = (TraderNPC) this.model.player.getCurrentRoom().getNPC();
            for (int i = 0; i < traderNPC.getAllItems().size(); i++)
            {
                String name = traderNPC.getAllItems().get(i).getName().toLowerCase().strip();
                if (name.equals(oldName))
                    old = traderNPC.getAllItems().get(i);
                else if (name.equals(newName))
                    newObj = traderNPC.getAllItems().get(i);
            }


            // try to process trade
            if (old == null || newObj == null)
                this.text.setText("Invalid Trade");
            boolean tradePerformed = traderNPC.trade(newObj, old, this.model.player);
            if (!tradePerformed)
                this.text.setText("Invalid Trade");
            else
                this.text.setText("Ask and you shall recieve.");
            updateItems();
        });
    }

    /**
     * This method handles the event related to
     * the offer button
     */
    public void addOfferEvent() {
        offer.setOnAction(e -> {
            stopArticulation(); //if speaking, stop
            // get if task is done
            boolean hasChest = false;
            int valid = 0;
            for (int i = 0; i < this.model.player.inventory.size(); i++) {
                if (this.model.player.inventory.get(i).getName().toLowerCase().strip().equals("bird")) {
                    valid = i;
                    hasChest = true;
                }
            }

            // user does not have the chest
            if (!hasChest)
                this.text.setText("You do not have what I want. Come back with a chest and I will reward you.");
            else
            {
                // cheeck if the suer has the required item and has already done
                HelpfulNPC helpfulNPC = (HelpfulNPC) this.model.player.getCurrentRoom().getNPC();
                if (helpfulNPC.isTaskCompleted())
                    this.text.setText("Nice try! I already gave you one and I will not give you another reward.");
                else {
                    helpfulNPC.setTaskCompleted(true);
                    this.model.player.inventory.remove(valid);
                    boolean complete = helpfulNPC.getHelp(this.model.player);
                    if (!complete)
                        this.text.setText("You already have the item I want to give you!");
                    else
                        this.text.setText("You have proven worthy of my gifts. Therefore I shall give you a big present for your troubles.");
                }
            }
            updateItems();
        });
    }

    /**
     * This method handles the event related to the
     * help button.
     */
    public void addInstructionEvent() {
        helpButton.setOnAction(e -> {
            stopArticulation(); //if speaking, stop
            showInstructions();
        });
    }

    /**
     * This method handles the event related to the
     * save button.
     */
    public void addSaveEvent() {
        saveButton.setOnAction(e -> {
            gridPane.requestFocus();
            SaveView saveView = new SaveView(this);
        });
    }

    /**
     * This method handles the event related to the
     * load button.
     */
    public void addLoadEvent() {
        loadButton.setOnAction(e -> {
            gridPane.requestFocus();
            LoadView loadView = new LoadView(this);
        });
    }

    /**
     * This method handles the events related to interacting
     * with the NPCs
     */
    public void addInteractEvent()
    {
        interactButton.setOnAction(e -> {
            gridPane.requestFocus();
            showNPCDialogue();
        });
    }

    /**
     * This method stops articulations 
     * (useful when transitioning to a new room or loading a new game)
     */
    public void stopArticulation() {
        if (mediaPlaying) {
            mediaPlayer.stop(); //shush!
            mediaPlaying = false;
        }
    }
}
