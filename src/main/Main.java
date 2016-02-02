package main;

import interfaces.Gui;
import interfaces.NPC;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Main Class of this game.
 * It's the base of all other classes, initiates the gui and delegates the GUI movements to the controller
 */
public class Main extends Application implements Gui {
    private static String mapFile;
    private static final int LINEWIDTH=3;
    private static final int SCREENWIDTH=1250;
    private static final int SCREENHEIGHT=650;
    private Controller controller;
    private TroopSelector troopSelector;
    private Endscreen endscreen;
    private Startscreen startscreen;

    // ----- Produce Stage -----

    /**
     * Initiates the whole gui
     * @param primaryStage
     */
    @Override
    public void start(Stage primaryStage) {
        // Exception kann nur passieren, wenn Map Datei nicht in Text umgewandelt werden kann. Programm wird dann beendet
        //init Worldmap and draw it on Pane
        Pane root = null;
        try {
            root=initWorld();
        } catch (IOException e) {
            System.out.println("Map File is no Text file");
            e.printStackTrace();
            System.exit(1);
        }

        DataSystem data = DataSystem.getInstance();

        //Init and Add status label
        Label status = new Label();
        status.textProperty().bind(data.statusProperty());
        status.setLayoutX(400);
        status.setLayoutY(SCREENHEIGHT-(status.getHeight()+50));
        root.getChildren().add(status);

        //Init and Add "Next" button
        Button nextPhase = new Button("Next");
        nextPhase.setLayoutX(SCREENWIDTH-(nextPhase.getWidth()+70));
        nextPhase.setLayoutY(SCREENHEIGHT-(nextPhase.getHeight()+50));
        nextPhase.setOnMouseClicked(this::onNextPhase);
        root.getChildren().add(nextPhase);

        //Init controller so TroopSelector can delegate to it
        //Must be after the world is initiated, because the Controller needs the DataSystem for initiation
        controller = new Controller(this);

        //Init and Add TroopSelector (only visible, when needed)
        troopSelector= new TroopSelector(controller);
        troopSelector.setVisible(false);
        troopSelector.setLayoutX((SCREENWIDTH-troopSelector.getPrefWidth())/2);
        troopSelector.setLayoutY((SCREENHEIGHT-troopSelector.getPrefHeight())/2);
        root.getChildren().add(troopSelector);

        //Init and Add Endscreen (only visible, when needed)
        endscreen = new Endscreen(SCREENWIDTH+10, 150); //The Endscreen is a small banner horizontal all over the screen at centered height
        endscreen.setLayoutY((SCREENHEIGHT-endscreen.getPrefHeight())/2);
        endscreen.setVisible(false);
        root.getChildren().add(endscreen);

        //Init and Add Startscreen (Visible in the beginning)
        startscreen=new Startscreen(300,150,this);
        startscreen.setLayoutY((SCREENHEIGHT-startscreen.getPrefHeight())/2); //centered at the window
        startscreen.setLayoutX((SCREENWIDTH-startscreen.getPrefWidth())/2);
        root.getChildren().add(startscreen);

        //Stage settings
        Scene scene = new Scene(root,SCREENWIDTH,SCREENHEIGHT);
        primaryStage.setTitle("All those Territories");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        //So you can Press Enter to Play
        startscreen.requestFocus();

    }

    // ------ Private Methods -----

    /**
     * Init Background and World and fill DataSystem with information
     * @return the world root pane
     * @throws IOException When Map File cant be read as text file
     */
    private Pane initWorld()throws IOException{
        Pane root = new Pane();
        Pane world = new Pane();
        Pane seaConnections = new Pane();
        root.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE,null,null)));

        //Throws Exception, when Mapfile is not found. This will terminate the program
        if(mapFile==null)mapFile="resources/world.map";
        FileReader fr=null;
        try {
            fr=new FileReader(mapFile);
        } catch (FileNotFoundException e) {
            System.out.println("404 MapFile NOT FOUND");
            e.printStackTrace();
            System.exit(1);
        }
        BufferedReader br = new BufferedReader(fr);
        String zeile;

        DataSystem data = DataSystem.getInstance();
        Map<String,Nation> Nations = data.getNations();

        //It will go through the Map file line per line
        while( (zeile = br.readLine())!= null){
            //System.out.println(zeile);
            String[] parts = zeile.split(" ");

            //Index 0 is the type of the values, so the real values start with index 1
            int i = 1;

            //Names can be 1 or more parts long (with spaces in between), so they will be collected, until something other than letters occur
            String name="";
            while(parts[i].matches("[A-Za-z]+")){
                name+=parts[i]+" ";
                i++;
            }
            //The name without spaces will be the key for all he Nation maps
            String nameNoSpace=name.replace(" ","");

            //Patch-of contains the border of the nation
            if (parts[0].equals("patch-of")) {
                Nation nation;
                if(Nations.containsKey(nameNoSpace)) {
                    nation = Nations.get(nameNoSpace);
                } else {
                    nation = new Nation(name);
                    Nations.put(nameNoSpace, nation);
                }

                //Startpixel of the national border
                Path path = new Path();
                MoveTo moveTo = new MoveTo();
                moveTo.setX(Double.parseDouble(parts[i]));
                moveTo.setY(Double.parseDouble(parts[i + 1]));
                path.getElements().add(moveTo);
                //each Pixel consists of 2 values (x,y), so we need to add 2 to our index for the next one
                i = i + 2;

                //Draw a line between all the pixels
                for (; i < parts.length; i = i + 2) {
                    LineTo line = new LineTo();
                    line.setX(Double.parseDouble(parts[i]));
                    line.setY(Double.parseDouble(parts[i + 1]));
                    path.getElements().add(line);
                }
                //Connect the nation to the DataSystem
                path.fillProperty().bind(nation.getColorProperty());
                path.setId(nameNoSpace);
                path.setOnMouseClicked(this::mouseClickHandler);
                world.getChildren().add(path);

            //captital-of contains the coordinates of the capital.
            // We will use this as the location of the troop counting label
            } else if (parts[0].equals("capital-of")) {
                int x = Integer.parseInt(parts[i]);
                int y = Integer.parseInt(parts[i + 1]);

                Nation nation = Nations.get(nameNoSpace);
                nation.setCapitalX(x);
                nation.setCapitalY(y);

                //Connect Label to the dataSystem
                Label count = new Label();
                count.setFont(Font.font(14));
                count.setLayoutX(x);
                count.setLayoutY(y);
                count.textProperty().bind(nation.getTruppCounter().asString());
                count.setId(nameNoSpace);
                count.setMouseTransparent(true);
                world.getChildren().add(count);


            } else if (parts[0].equals("neighbors-of")) {
                Nation nation = Nations.get(nameNoSpace);
                i++;
                while (i < parts.length) {
                    String neighbor = "";
                    while (i < parts.length && parts[i].matches("[A-Za-z]+")) {
                        neighbor += parts[i];
                        i++;
                    }
                    // Line between capitals, except for Alaska and Kamchatka (will be drawn manually)
                    if (!(nameNoSpace.equals("Alaska") || nameNoSpace.equals("Kamchatka"))) {
                        seaConnections.getChildren().add(lineBetweenCapitals(Nations.get(nameNoSpace), Nations.get(neighbor)));
                    }

                    // Neighbors are mostly only indicated once --> need to add them for both nations
                    nation.addNeighbor(neighbor);
                    Nations.get(neighbor).addNeighbor(nameNoSpace);

                    i++; //jump over bullet point
                }
            //continent contains the bonus value, which will be added when someone has a whole continent and the nations that are part of that continent
            } else if (parts[0].equals("continent")) {
                List<String> nations = new ArrayList<>();
                int addValue = Integer.parseInt(parts[i]);
                //jump over addValue and ":"
                i = i + 2;
                //get all the nations of the continent
                while (i < parts.length) {
                    String nation = "";
                    while (i < parts.length && parts[i].matches("[A-Za-z]+")) {
                        nation += parts[i];
                        i++;
                    }
                    nations.add(nation);
                    i++; //jump over bullet point
                }

                data.getContinents().put(nameNoSpace, new Continent(name, addValue, nations.toArray(new String[nations.size()])));

            } else {
                //Should not occur!
                System.err.println("Unerwartete Zeile: " + zeile);
            }
        }
        br.close();

        //Add connection between Alaska and Kamchatka manually, if they both exist in the map file
        Nation alaska = Nations.get("Alaska");
        Nation kamchatka = Nations.get("Kamchatka");
        if(alaska!=null && kamchatka!=null){
            Line alaskaLine = new Line();
            alaskaLine.setStrokeWidth(LINEWIDTH);
            alaskaLine.setStartY(alaska.getCapitalY());
            alaskaLine.setStartX(alaska.getCapitalX());
            alaskaLine.setEndX(0);
            alaskaLine.setEndY(alaska.getCapitalY());
            seaConnections.getChildren().add(alaskaLine);

            Line kamchatkaLine = new Line();
            kamchatkaLine.setStrokeWidth(LINEWIDTH);
            kamchatkaLine.setStartX(kamchatka.getCapitalX());
            kamchatkaLine.setStartY(kamchatka.getCapitalY());
            kamchatkaLine.setEndX(SCREENWIDTH);
            kamchatkaLine.setEndY(kamchatka.getCapitalY());
            seaConnections.getChildren().add(kamchatkaLine);
        }

        root.getChildren().add(seaConnections);
        root.getChildren().add(world);

        return root;
    }

    /**
     * Help Method to draw a line between 2 Nation capitals
     * @param a
     * @param b
     * @return
     */
    private Line lineBetweenCapitals(Nation a, Nation b){
        if(a == null||b==null){
            System.err.println("Cannot draw line between 2 Capitals!");
            return null;
        }
        Line line = new Line();
        line.setStrokeWidth(LINEWIDTH);
        line.setStartX(a.getCapitalX());
        line.setStartY(a.getCapitalY());
        line.setEndX(b.getCapitalX());
        line.setEndY(b.getCapitalY());
        return line;
    }

    /**
     * delegates the mouse click on a nation to the corresponding controller method
     * @param me
     */
    private void mouseClickHandler(MouseEvent me){
        if(worldResponsive())  {
            if(me.getButton().toString().equals("PRIMARY")) controller.leftClickedOnNation(((Node)me.getSource()).getId());
            if(me.getButton().toString().equals("SECONDARY")) controller.rightClickedOnNation(((Node)me.getSource()).getId());
        }

    }

    /**
     * delegates the button click to the controller
     * @param me
     */
    private void onNextPhase(MouseEvent me){
        if(worldResponsive()) controller.clickedNext();
    }

    /**
     * Help method to tell if the background should be responsive (Click on nations or the next button)
     * @return
     */
    private boolean worldResponsive(){
        if(!troopSelector.isVisible() && !endscreen.isVisible() && !startscreen.isVisible()) return true;
        else return false;
    }

    // ------ Public Methods ------
    // Methods, which are defined in the gui interface

    /**
     * To be called from the controller whenever the player needs to be asked for the amount of troops
     * @param title Tell the player why you need an amount of troops (attack, defense, ...)
     * @param min
     * @param max
     */
    public void showTruppSelection(String title, int min, int max){
        troopSelector.setTitle(title);
        troopSelector.setSliderValues(min, max);
        troopSelector.setVisible(true);
        troopSelector.requestFocus();
    }

    /**
     * To be called from the controller when the game ends
     * @param Player1Win
     */
    public void showEndScreen(boolean Player1Win){
        endscreen.setText("Player 1",Player1Win);
        endscreen.setVisible(true);
    }

    /**
     * To be called from the Startscreen to set the chosen opponent in the controller
     * @param opponent
     */
    @Override
    public void setOpponent(NPC opponent) {
        if(opponent==null) System.err.println("Opponent konnte nicht festgestellt werden!");
        else controller.setComputerPlayer(opponent); //Should never occur!
    }





    // ----- main Method -----

    public static void main(String[] args) {
        if(args.length>0) {
            mapFile=args[0];
            //System.out.println(mapFile);
        }
        launch(args);
    }
}
