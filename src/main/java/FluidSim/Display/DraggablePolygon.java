package FluidSim.Display;

import FluidSim.Mesh.MeshCreator;
import FluidSim.Mesh.PolygonDistanceField;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;
import org.locationtech.jts.geom.Geometry;
import javafx.embed.swing.SwingFXUtils;



/**  a polygon with draggable corners and the option to create and render a Delaunay triangulation mesh
    Fields:
    -polygon: the rendered shape
    -anchorList: List of draggable anchor points which control the shape of the polygon
    -meshContainer: the jfx group into which the Delaunay triangulation mesh will be placed for rendering purposes
    -autoMesh: bool controlling whether to actually calculate the mesh */

public class DraggablePolygon {

    private Polygon polygon;
    private ObservableList<Anchor> anchorList = FXCollections.observableArrayList();
    private Group meshContainer;
    private boolean autoMesh;
    private double canvasWidth;
    private double canvasHeight;

    public void setCanvasSize(double width, double height){
        canvasHeight=height; canvasWidth=width;
    }

    public double[] getCanvasSize(){
        return new double[]{canvasWidth,canvasHeight};
    }

    public Group getMeshContainer() {
        return meshContainer;
    }

    public void setMeshContainer(Group meshContainer) {
        this.meshContainer=meshContainer;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
        setPolygonProperties();
        createControlAnchors();
    }

    public boolean isAutoMesh(){
        return autoMesh;
    }

    public void setAutoMesh(boolean b){
        autoMesh=b;
    }

    public ObservableList<Anchor> getAnchorList() {
        return anchorList;
    }

    /* Unused: public org.poly2tri.geometry.polygon.Polygon getTriangulatablePolygon() {
        List<org.poly2tri.geometry.polygon.PolygonPoint> points = new ArrayList<>(polygon.getPoints().size()/2);
        for (int i=0; i< polygon.getPoints().size(); i+=2) {
            points.add(new org.poly2tri.geometry.polygon.PolygonPoint(polygon.getPoints().get(i), polygon.getPoints().get(i+1)));
        }
        return new org.poly2tri.geometry.polygon.Polygon(points);
    }*/

    private void setPolygonProperties() {
        polygon.setStroke(Color.FORESTGREEN);
        polygon.setStrokeWidth(4);
        polygon.setStrokeLineCap(StrokeLineCap.ROUND);
        polygon.setFill(Color.CORNSILK.deriveColor(0, 1.2, 1, 0.6));
    }

    public DraggablePolygon() {
        polygon= new Polygon();
        setPolygonProperties();
        autoMesh=false;
    }

    public DraggablePolygon(double canvasWidth, double canvasHeight, double... points) {
        this.canvasHeight=canvasHeight;
        this.canvasWidth=canvasWidth;
        polygon = new Polygon(points);
        autoMesh=false;
        setPolygonProperties();
        createControlAnchors();
    }

    public void renderMesh() {
        if(this.autoMesh && this.meshContainer!=null) {
            if (!this.meshContainer.getChildren().isEmpty()) { this.meshContainer.getChildren().clear(); }
            PolygonDistanceField pdf = new PolygonDistanceField(canvasWidth, canvasHeight,1 , getPolygon()); /**TODO: put in own method*/
            Image image1 = SwingFXUtils.toFXImage(pdf.getImage(), null);
            ImageView imageView = new ImageView(image1);
            this.meshContainer.getChildren().add(imageView);
            Geometry tris = MeshCreator.doTriangulator(getPolygon(), pdf, canvasWidth, canvasHeight);
            for (int i = 0; i < tris.getNumGeometries(); i++) {
                this.meshContainer.getChildren().add(MeshCreator.JTSPolyToFXPoly(tris.getGeometryN(i)));
            }
        }
    }

    public void createControlAnchors(){
        ObservableList<Double> points = polygon.getPoints();
        for (int i = 0; i < points.size(); i+=2) {
            final int idx = i;

            DoubleProperty xProperty = new SimpleDoubleProperty(points.get(i));
            DoubleProperty yProperty = new SimpleDoubleProperty(points.get(i + 1));

            xProperty.addListener(new ChangeListener<Number>() {
                @Override public void changed(ObservableValue<? extends Number> ov, Number oldX, Number x) {
                    points.set(idx, (double) x);
                }
            });

            yProperty.addListener(new ChangeListener<Number>() {
                @Override public void changed(ObservableValue<? extends Number> ov, Number oldY, Number y) {
                    points.set(idx + 1, (double) y);
                }
            });

            anchorList.add(new Anchor(Color.GRAY, xProperty, yProperty));
        }
    }

    class Anchor extends Circle {
        private final DoubleProperty x, y;

        Anchor(Color color, DoubleProperty x, DoubleProperty y) {
            super(x.get(), y.get(), 5);
            setFill(color.deriveColor(1, 1, 1, 0.5));
            setStroke(color);
            setStrokeWidth(0.5);
            setStrokeType(StrokeType.OUTSIDE);

            this.x = x;
            this.y = y;

            x.bind(centerXProperty());
            y.bind(centerYProperty());
            enableDrag();
        }

        // make a node movable by dragging it around with the mouse.
        private void enableDrag() {
            final Delta dragDelta = new Delta();
            setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    // record a delta distance for the drag and drop operation.
                    dragDelta.x = getCenterX() - mouseEvent.getX();
                    dragDelta.y = getCenterY() - mouseEvent.getY();
                    getScene().setCursor(Cursor.MOVE);
                }
            });
            setOnMouseReleased(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    getScene().setCursor(Cursor.HAND);
                }
            });
            setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    double newX = mouseEvent.getX() + dragDelta.x;
                    double newY = mouseEvent.getY() + dragDelta.y;
                    if (newX > 0 && newX < getScene().getWidth()) {
                        setCenterX(newX);
                    }

                    if (newY > 0 && newY < getScene().getHeight()) {
                        setCenterY(newY);
                    }
                    renderMesh();
                }
            });
            setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if (!mouseEvent.isPrimaryButtonDown()) {
                        getScene().setCursor(Cursor.HAND);
                    }
                }
            });
            setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if (!mouseEvent.isPrimaryButtonDown()) {
                        getScene().setCursor(Cursor.DEFAULT);
                    }
                }
            });

        }
    }

    // records relative x and y co-ordinates.
    private class Delta { double x, y; }
}
