package FluidSim.Mesh;

import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.triangulate.ConformingDelaunayTriangulator;
import org.locationtech.jts.triangulate.ConstraintVertex;
import org.locationtech.jts.triangulate.Segment;
import org.locationtech.jts.triangulate.quadedge.QuadEdge;
import org.locationtech.jts.triangulate.quadedge.QuadEdgeSubdivision;
import org.locationtech.jts.triangulate.quadedge.Vertex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MeshCreator {

    public static ConformingDelaunayTriangulator doTriangulation(Constraints constraints, PolygonDistanceField probDensFunc, double canvasX, double canvasY){
        ArrayList<FluidVertex> generatedPoints = PointGeneration.cumulativeDistributionMethod(probDensFunc, 4000);
        ConformingDelaunayTriangulator cdt = new ConformingDelaunayTriangulator(generatedPoints, 2);

        cdt.setConstraints((List) constraints.allSegs.clone(), (List) constraints.allSegVerts.clone());
        cdt.formInitialDelaunay();
        cdt.enforceConstraints();

        return cdt;
    }

    public static  Constraints createConstraintSegmentsAndVertices(javafx.scene.shape.Polygon fxPolygon, double canvasX, double canvasY) {
        ObservableList<Double> holepoints=fxPolygon.getPoints();  //list x1, y1, x2, y2, x3, y3...

        ArrayList<Coordinate> holeCoords = new ArrayList<>(holepoints.size()/2);
        for(int i =0; i<holepoints.size()/2; i++){
            holeCoords.add(i, new Coordinate(holepoints.get(2*i),holepoints.get(2*i+1)));
        }
        ArrayList<Segment> holeSegs = new ArrayList<>(holeCoords.size());
        for(int i =0; i<holeCoords.size()-1; i++){
            holeSegs.add(i, new Segment(holeCoords.get(i),holeCoords.get(i+1)));
        }
        holeSegs.add(holeCoords.size()-1, new Segment(holeCoords.get(holeCoords.size()-1),holeCoords.get(0)));

        ArrayList<Coordinate> shellCoords= new ArrayList<Coordinate>(Arrays.asList(new Coordinate(0, 0), new Coordinate(canvasX, 0),
                new Coordinate(canvasX, canvasY), new Coordinate(0, canvasY)));
        ArrayList<Segment> shellSegs = new ArrayList<>(shellCoords.size());
        for(int i =0; i<shellCoords.size()-1; i++){
            shellSegs.add(i, new Segment(shellCoords.get(i),shellCoords.get(i+1)));
        }
        shellSegs.add(shellCoords.size()-1, new Segment(shellCoords.get(shellCoords.size()-1),shellCoords.get(0)));

        ArrayList<FluidVertex> shellSegVerts = new ArrayList<>(shellCoords.size());
        for(int i =0; i<shellCoords.size(); i++){
            shellSegVerts.add(i, new FluidVertex(shellCoords.get(i)));
        }
        ArrayList<FluidVertex> holeSegVerts = new ArrayList<>(holeCoords.size());
        for(int i=0; i<holeCoords.size(); i++){
            holeSegVerts.add(i, new FluidVertex(holeCoords.get(i)));
        }

        ArrayList<FluidVertex> allSegVerts = new ArrayList<>(shellSegVerts); allSegVerts.addAll(holeSegVerts);
        ArrayList<Segment> allSegs = new ArrayList<>(shellSegs); allSegs.addAll(holeSegs);

        Constraints constraints = new Constraints();
        constraints.allSegs = allSegs;
        constraints.allSegVerts = allSegVerts;
        return constraints;
    }

    public static javafx.scene.shape.Polygon convertJTSPolyToFXPoly(Geometry polygon){
        Coordinate[] polyCoords=polygon.getCoordinates();
        javafx.scene.shape.Polygon fxPoly = new javafx.scene.shape.Polygon();
        for(Coordinate i : polyCoords) {
            fxPoly.getPoints().addAll(i.getX(), i.getY());
        }
        fxPoly.setFill(Color.BLACK.deriveColor(0, 1.2, 1, 0.2));
        fxPoly.setStroke(Color.FORESTGREEN);
        fxPoly.setStrokeWidth(0.3);
        fxPoly.setStrokeLineCap(StrokeLineCap.ROUND);
        return fxPoly;
    }

    public static javafx.scene.shape.Line convertJTSQuadEdgeToFXLine(QuadEdge quadEdge){
        Coordinate start = quadEdge.orig().getCoordinate();
        Coordinate end = quadEdge.dest().getCoordinate();
        javafx.scene.shape.Line fxLine = new javafx.scene.shape.Line(start.x, start.y, end.x, end.y);
        fxLine.setStroke(Color.FORESTGREEN);
        fxLine.setStrokeWidth(0.3);
        fxLine.setStrokeLineCap(StrokeLineCap.ROUND);
        return fxLine;
    }


}
