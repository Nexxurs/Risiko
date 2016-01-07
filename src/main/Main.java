package main;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Group root = new Group();

        FileReader fr = new FileReader("C:/Users/Paul/Dropbox/Informatik/2015-WS-PK/Abschlussbeispiel/world.map");
        BufferedReader br = new BufferedReader(fr);
        String zeile="";

        while( (zeile = br.readLine())!= null){
            System.out.println(zeile);
            String[] parts = zeile.split(" ");
            if(parts[0].equals("patch-of")){
                int i = 1;
                String name="";
                while(parts[i].matches("[A-Za-z]+")){
                    name+=parts[i];
                    i++;
                }

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
                int i = 1;
                String name="";
                while(parts[i].matches("[A-Za-z]+")){
                    name+=parts[i];
                    i++;
                }
                double x = Double.parseDouble(parts[i]);
                double y = Double.parseDouble(parts[i+1]);
                Circle dot = new Circle(x,y,1);
                root.getChildren().add(dot);
		    		/* Added Name Label
		    		Label nameLb = new Label(name);
		    		nameLb.setLayoutX(x);
		    		nameLb.setLayoutY(y);
		    		root.getChildren().add(nameLb);
		    		*/
            }

        }
        br.close();



        Scene scene = new Scene(root,1300,650);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
