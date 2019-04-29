package FluidSim.Display;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class MainFrame extends Application {

    public void start(Stage mainStage) {

        mainStage.setTitle("FluidSim");
        BorderPane mainLayoutPane= new BorderPane();
        Pane centralDisplayPane= new Pane();
        centralDisplayPane.setMinSize(800, 600);

        /*set up main display area*/
        Group centralDisplayContainer = new Group();
        Group meshContainer = new Group();

        DraggablePolygon draggablePolygon = new DraggablePolygon(800, 600, 340.0, 290.0, 290.0, 20.0, 180.0, 270.0, 120.0, 290.0);
        draggablePolygon.setAutoMesh(true);
        draggablePolygon.setMeshContainer(meshContainer);

        centralDisplayContainer.getChildren().add(draggablePolygon.getPolygon());
        centralDisplayContainer.getChildren().addAll(draggablePolygon.getAnchorList());
        centralDisplayContainer.getChildren().add(meshContainer);

        meshContainer.toBack();

        centralDisplayPane.getChildren().add(centralDisplayContainer);
        centralDisplayPane.toBack();
        mainLayoutPane.setCenter(centralDisplayPane);
        /**/

        /*set up top menu*/
        HBox topMenu= new HBox(1);
        Button topMenuGenerateMeshButton= new Button("Generate Mesh");
        topMenuGenerateMeshButton.setTranslateX(5);
        topMenuGenerateMeshButton.setTranslateY(5);
        Button topMenuRelaxMeshButton= new Button("Relax Mesh");
        topMenuRelaxMeshButton.setTranslateX(5);
        topMenuRelaxMeshButton.setTranslateY(5);
        topMenu.getChildren().addAll(topMenuGenerateMeshButton, topMenuRelaxMeshButton);
        topMenu.toFront();
        mainLayoutPane.setTop(topMenu);
        /**/

        /*Assign button functionality*/
        topMenuGenerateMeshButton.setOnAction(event -> {
            draggablePolygon.createInitialMesh();
            draggablePolygon.renderMesh();
        });
        topMenuRelaxMeshButton.setOnAction(event -> {
            draggablePolygon.relaxMesh();
            draggablePolygon.renderMesh();
        });
        /**/

        /*finalize*/
        Scene mainScene = new Scene(mainLayoutPane, 800, 600); //creates the main scene of the program, adding the layout as a child
        mainStage.setScene(mainScene);
        mainStage.show();
        /**/
    }
}

