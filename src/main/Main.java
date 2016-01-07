package main;

import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableIntegerValue;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        //Group root = new Group();
        Pane root = new Pane();
        root.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE,null,null)));

        FileReader fr = new FileReader("C:/Users/Paul/Dropbox/Informatik/2015-WS-PK/Abschlussbeispiel/world.map");
        BufferedReader br = new BufferedReader(fr);
        String zeile="";

        DataSystem system = DataSystem.getInstance();
        Map<String,Nation> Nations = system.getNations();

        while( (zeile = br.readLine())!= null){
            System.out.println(zeile);
            String[] parts = zeile.split(" ");

            int i = 1;
            String name="";
            String nameNoSpace="";
            while(parts[i].matches("[A-Za-z]+")){
                name+=parts[i]+" ";
                i++;
            }
            nameNoSpace=name.replace(" ","");

            if(parts[0].equals("patch-of")){
                //With Path (nicht so schön)
                Path path = new Path();
                MoveTo moveTo = new MoveTo();
                moveTo.setX(Double.parseDouble(parts[i]));
                moveTo.setY(Double.parseDouble(parts[i+1]));
                path.getElements().add(moveTo);
                i=i+2;

                for(;i<parts.length;i=i+2){
                    LineTo line = new LineTo();
                    line.setX(Double.parseDouble(parts[i]));
                    line.setY(Double.parseDouble(parts[i+1]));
                    path.getElements().add(line);
                    i=i+2;
                }
                path.setFill(Color.GRAY);
                path.setId(nameNoSpace);
                path.setOnMouseClicked(me -> mouseClickHandler(me));
                root.getChildren().add(path);

		    		/* Mit Line und Group statt Path, Karte schaut schöner aus, aber Füllung wird schwer
		    		int i = 1;
		    		String name="";
		    		while(parts[i].matches("[A-Za-z]+")){
		    			name+=parts[i];
		    			i++;
		    		}
		    		Group nation = new Group();
		    		int oldX=0,oldY=0;
		    		for(;i<parts.length;i=i+2){
		    			if(oldX!=0&&oldY!=0){
		    				Line line = new Line();
		    				line.setStartX(oldX);
		    				line.setStartY(oldY);
		    				line.setEndX(Integer.parseInt(parts[i]));
		    				line.setEndY(Integer.parseInt(parts[i+1]));
		    				nation.getChildren().add(line);
		    			}
		    			oldX = Integer.parseInt(parts[i]);
		    			oldY = Integer.parseInt(parts[i+1]);
		    		}
		    		root.getChildren().add(nation);
		    		*/
            }
            else if(parts[0].equals("capital-of")){
                Nation nation = new Nation(name);

                double x = Double.parseDouble(parts[i]);
                double y = Double.parseDouble(parts[i+1]);

                Label count = new Label();
                count.setLayoutX(x);
                count.setLayoutY(y);
                count.textProperty().bind(nation.getTruppCounter().asString());
                count.setId(nameNoSpace);
                count.setOnMouseClicked(me -> mouseClickHandler(me));
                system.getNations().put(nameNoSpace,nation);
                root.getChildren().add(count);
            }
            else if(parts[0].equals("neighbors-of")){
                Nation nation = system.getNations().get(nameNoSpace);
                List<String> neighbors = new ArrayList<>();

                //":" Symbol überspringen
                i++;

                while(i<parts.length){
                    String neighbor="";
                    while(i<parts.length&&parts[i].matches("[A-Za-z]+")){
                        neighbor+=parts[i];
                        i++;
                    }
                    neighbors.add(neighbor);
                    i++; //Aufzählungszeichen überspringen
                }
                nation.setNeighbors(neighbors.toArray(new String[neighbors.size()]));
            }
            else if(parts[0].equals("continent")){
                List<String> nations = new ArrayList<>();
                int addValue = Integer.parseInt(parts[i]);
                //addValue und ":" überspringen
                i=i+2;

                while(i<parts.length){
                    String nation="";
                    while(i<parts.length&&parts[i].matches("[A-Za-z]+")){
                        nation+=parts[i];
                        i++;
                    }
                    nations.add(nation);
                    i++; //Aufzählungszeichen überspringen
                }

                system.getContinents().put(nameNoSpace,new Continent(name,addValue,nations.toArray(new String[nations.size()])));
            }
        }
        br.close();

        System.out.println("Continent Size: "+system.getContinents().size());
        System.out.println("Nations Size: "+system.getNations().size());

        Scene scene = new Scene(root,1300,650);
        primaryStage.setTitle("All those Territories");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void mouseClickHandler(MouseEvent me){
        System.out.println(((Node)me.getSource()).getId());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
