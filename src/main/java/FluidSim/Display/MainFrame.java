package FluidSim.Display;

import FluidSim.Mesh.MeshCreator;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.locationtech.jts.geom.Geometry;


public class MainFrame extends Application {

    public void start(Stage mainStage) {
        mainStage.setTitle("FluidSim");
        BorderPane mainPane= new BorderPane();
        Pane centerPane= new Pane();
        centerPane.setMinSize(800, 600);


        /*set up top menu*/
        HBox topMenu= new HBox(1);
        Button buttonA= new Button("Gen Mesh"); buttonA.setTranslateX(5); buttonA.setTranslateY(5);
        Button buttonB= new Button("null"); buttonB.setTranslateX(5); buttonB.setTranslateY(5);
        topMenu.getChildren().addAll(buttonA, buttonB);
        mainPane.setTop(topMenu);
        /**/


        /*set up main display area*/
        Group root = new Group();
        Group meshContainer = new Group();
        //root.setTranslateY(20);

        Canvas mainCanvas = new Canvas(800, 600);

        DraggablePolygon draggablePolygon = new DraggablePolygon(meshContainer,800, 600,
                true, 340.0, 290.0, 290.0, 20.0, 180.0, 270.0, 120.0, 290.0);



        root.getChildren().add(draggablePolygon.getPolygon());
        root.getChildren().addAll(draggablePolygon.getAnchorList());
        root.getChildren().add(meshContainer);
        meshContainer.toBack();
        buttonA.setOnAction(event -> {
            if (!meshContainer.getChildren().isEmpty()) {meshContainer.getChildren().clear();}
            Geometry tris = MeshCreator.doTriangulator(draggablePolygon.getPolygon(),800,600);
            for (int i =0; i<tris.getNumGeometries(); i++) {
                meshContainer.getChildren().add(MeshCreator.JTSPolyToFXPoly(tris.getGeometryN(i)));
            }});

        //root.getChildren().add(mainCanvas);
        centerPane.getChildren().add(root);
        mainPane.setCenter(centerPane);
        /**/

        /*finalize*/
        Scene mainScene = new Scene(mainPane, 800, 600); //creates the main scene of the program, adding the layout as a child
        mainStage.setScene(mainScene);
        mainStage.show();
        /**/
    }
}

