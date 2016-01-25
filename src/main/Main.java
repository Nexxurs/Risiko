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
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    @Override
    public void start(Stage primaryStage) throws Exception{

        Pane root = initWorld();
        DataSystem data = DataSystem.getInstance();

        Label status = new Label();
        status.textProperty().bind(data.statusProperty());
        status.setLayoutX(400);
        status.setLayoutY(SCREENHEIGHT-(status.getHeight()+50));
        root.getChildren().add(status);

        Button nextPhase = new Button("Next");
        nextPhase.setLayoutX(SCREENWIDTH-(nextPhase.getWidth()+70));
        nextPhase.setLayoutY(SCREENHEIGHT-(nextPhase.getHeight()+50));
        nextPhase.setOnMouseClicked(this::onNextPhase);
        root.getChildren().add(nextPhase);

        controller = new Controller(this);

        troopSelector= new TroopSelector(controller);
        troopSelector.setVisible(false);
        troopSelector.setLayoutX((SCREENWIDTH-troopSelector.getPrefWidth())/2);
        troopSelector.setLayoutY((SCREENHEIGHT-troopSelector.getPrefHeight())/2);
        root.getChildren().add(troopSelector);

        endscreen = new Endscreen(SCREENWIDTH, 150);
        endscreen.setLayoutY((SCREENHEIGHT-endscreen.getPrefHeight())/2);
        endscreen.setVisible(false);
        root.getChildren().add(endscreen);

        startscreen=new Startscreen(300,150,this);
        startscreen.setLayoutY((SCREENHEIGHT-startscreen.getPrefHeight())/2);
        startscreen.setLayoutX((SCREENWIDTH-startscreen.getPrefWidth())/2);
        root.getChildren().add(startscreen);



        Scene scene = new Scene(root,SCREENWIDTH,SCREENHEIGHT);
        primaryStage.setTitle("All those Territories");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        startscreen.requestFocus();

    }

    private Pane initWorld() throws Exception{
        Pane world = new Pane();
        Pane root = new Pane();
        Pane seaConnections = new Pane();
        root.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE,null,null)));
        if(mapFile==null)mapFile="resources/world.map";
        FileReader fr = new FileReader(mapFile);
        BufferedReader br = new BufferedReader(fr);
        String zeile;

        DataSystem data = DataSystem.getInstance();
        Map<String,Nation> Nations = data.getNations();

        while( (zeile = br.readLine())!= null){
            //System.out.println(zeile);
            String[] parts = zeile.split(" ");

            int i = 1;
            String name="";
            while(parts[i].matches("[A-Za-z]+")){
                name+=parts[i]+" ";
                i++;
            }
            String nameNoSpace=name.replace(" ","");


            if (parts[0].equals("patch-of")) {
                Nation nation;
                if(Nations.containsKey(nameNoSpace)) {
                    nation = Nations.get(nameNoSpace);
                } else {
                    nation = new Nation(name);
                    Nations.put(nameNoSpace, nation);
                }


                Path path = new Path();
                MoveTo moveTo = new MoveTo();
                moveTo.setX(Double.parseDouble(parts[i]));
                moveTo.setY(Double.parseDouble(parts[i + 1]));
                path.getElements().add(moveTo);
                i = i + 2;

                for (; i < parts.length; i = i + 2) {
                    LineTo line = new LineTo();
                    line.setX(Double.parseDouble(parts[i]));
                    line.setY(Double.parseDouble(parts[i + 1]));
                    path.getElements().add(line);
                }
                path.fillProperty().bind(nation.getColorProperty());
                path.setId(nameNoSpace);
                path.setOnMouseClicked(this::mouseClickHandler);
                world.getChildren().add(path);



            } else if (parts[0].equals("capital-of")) {
                int x = Integer.parseInt(parts[i]);
                int y = Integer.parseInt(parts[i + 1]);

                Nation nation = Nations.get(nameNoSpace);
                nation.setCapitalX(x);
                nation.setCapitalY(y);

                Label count = new Label();
                count.setLayoutX(x);
                count.setLayoutY(y);
                count.textProperty().bind(nation.getTruppCounter().asString());
                count.setId(nameNoSpace);
                count.setOnMouseClicked(this::mouseClickHandler);
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
                    // Linien zwischen Hauptstädten, außer bei Alaska und Kamchatka - manuell gezeichnet!
                    if (!(nameNoSpace.equals("Alaska") || nameNoSpace.equals("Kamchatka"))) {
                        seaConnections.getChildren().add(lineBetweenCapitals(Nations.get(nameNoSpace), Nations.get(neighbor)));
                    }

                    //Nachbarn werden meist nur in eine Richtung angegeben. --> in beide Richtungen hinzufügen
                    nation.addNeighbor(neighbor);
                    Nations.get(neighbor).addNeighbor(nameNoSpace);

                    i++; //Aufzählungszeichen überspringen
                }
            } else if (parts[0].equals("continent")) {
                List<String> nations = new ArrayList<>();
                int addValue = Integer.parseInt(parts[i]);
                //addValue und ":" überspringen
                i = i + 2;

                while (i < parts.length) {
                    String nation = "";
                    while (i < parts.length && parts[i].matches("[A-Za-z]+")) {
                        nation += parts[i];
                        i++;
                    }
                    nations.add(nation);
                    i++; //Aufzählungszeichen überspringen
                }

                data.getContinents().put(nameNoSpace, new Continent(name, addValue, nations.toArray(new String[nations.size()])));

            } else {
                System.err.println("Unerwartete Zeile: " + zeile);
            }
        }
        br.close();

        //Verbindung zwischen Alaska und Kamchatka
        Line alaskaLine = new Line();
        alaskaLine.setStrokeWidth(LINEWIDTH);
        Nation alaska = Nations.get("Alaska");
        if(alaska!=null){
            alaskaLine.setStartY(alaska.getCapitalY());
            alaskaLine.setStartX(alaska.getCapitalX());
            alaskaLine.setEndX(0);
            alaskaLine.setEndY(alaska.getCapitalY());
            seaConnections.getChildren().add(alaskaLine);
        }


        Line kamchatkaLine = new Line();
        kamchatkaLine.setStrokeWidth(LINEWIDTH);
        Nation kamchatka = Nations.get("Kamchatka");
        if(kamchatka!=null){
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

    // ------ Public Methods ------

    public void showTruppSelection(String title, int min, int max){
        troopSelector.setTitle(title);
        troopSelector.setSliderValues(min, max);
        troopSelector.setVisible(true);
        troopSelector.requestFocus();
    }

    public void showEndScreen(boolean Player1Win){
        endscreen.setText("Player 1",Player1Win);
        endscreen.setVisible(true);
    }

    @Override
    public void setOpponent(NPC opponent) {
        if(opponent==null) System.err.println("Opponent konnte nicht festgestellt werden!");
        else controller.setComputerPlayer(opponent);
    }

    // ------ Private Methods

    private void mouseClickHandler(MouseEvent me){
        if(worldResponsive())  {
            if(me.getButton().toString().equals("PRIMARY")) controller.leftClickedOnNation(((Node)me.getSource()).getId());
            if(me.getButton().toString().equals("SECONDARY")) controller.rightClickedOnNation(((Node)me.getSource()).getId());
        }

    }

    private void onNextPhase(MouseEvent me){
        if(worldResponsive()) controller.clickedNext();
    }

    private boolean worldResponsive(){
        if(!troopSelector.isVisible() && !endscreen.isVisible() && !startscreen.isVisible()) return true;
        else return false;
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
