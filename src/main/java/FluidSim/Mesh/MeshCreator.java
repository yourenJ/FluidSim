package FluidSim.Mesh;

import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.triangulate.ConformingDelaunayTriangulator;
import org.locationtech.jts.triangulate.ConstraintVertex;
import org.locationtech.jts.triangulate.Segment;
import org.locationtech.jts.triangulate.quadedge.QuadEdgeSubdivision;
import org.locationtech.jts.triangulate.quadedge.Vertex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MeshCreator {

    public static ConformingDelaunayTriangulator doTriangulator(javafx.scene.shape.Polygon fxPolygon, PolygonDistanceField probDensFunc, double canvasX, double canvasY){

        ArrayList<ConstraintVertex> generatedPoints = PointGeneration.cumulativeDistributionMethod(probDensFunc, 2000);
        Constraints constraints = createConstrainSegmentsAndVertices(fxPolygon, canvasX, canvasY);
        GeometryFactory factory = new GeometryFactory();
        ConformingDelaunayTriangulator cdt = new ConformingDelaunayTriangulator(generatedPoints, 1);
        cdt.setConstraints(constraints.allSegs, constraints.allSegVerts);
        cdt.formInitialDelaunay();
        cdt.enforceConstraints();

        return cdt;
    }

    public static  Constraints createConstrainSegmentsAndVertices(javafx.scene.shape.Polygon fxPolygon, double canvasX, double canvasY) {
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

        ArrayList<ConstraintVertex> shellSegVerts = new ArrayList<>(shellCoords.size());
        for(int i =0; i<shellCoords.size(); i++){
            shellSegVerts.add(i, new ConstraintVertex(shellCoords.get(i)));
        }
        ArrayList<ConstraintVertex> holeSegVerts = new ArrayList<>(holeCoords.size());
        for(int i=0; i<holeCoords.size(); i++){
            holeSegVerts.add(i, new ConstraintVertex(holeCoords.get(i)));
        }
        ArrayList<ConstraintVertex> allSegVerts = new ArrayList<>(shellSegVerts); allSegVerts.addAll(holeSegVerts);

        ArrayList<Segment> allSegs = new ArrayList<>(shellSegs); allSegs.addAll(holeSegs);

        Constraints constraints = new Constraints();
        constraints.allSegs = allSegs;
        constraints.allSegVerts = allSegVerts;
        return constraints;
    }

    public static javafx.scene.shape.Polygon JTSPolyToFXPoly(Geometry polygon){
        Coordinate[] polyCoords=polygon.getCoordinates();
        javafx.scene.shape.Polygon fxPoly = new javafx.scene.shape.Polygon();
        for(Coordinate i : polyCoords) {
            fxPoly.getPoints().addAll(i.getX(), i.getY());
        }
        fxPoly.setFill(Color.BLACK.deriveColor(0, 1.2, 1, 0.2));
        fxPoly.setStroke(Color.FORESTGREEN);
        fxPoly.setStrokeWidth(1);
        fxPoly.setStrokeLineCap(StrokeLineCap.ROUND);
        return fxPoly;
    }

    /*old, unused:*/
    public static ConformingDelaunayTriangulator chews(ConformingDelaunayTriangulator cdt, ArrayList<Segment> allSegs,
                             ArrayList<ConstraintVertex> allSegVerts, GeometryFactory factory,
                             double canvasX, double canvasY, double angularThreshold) {
        double a = angularThreshold;
        //cdt.setConstraints(allSegs, allSegVerts);
        //cdt.formInitialDelaunay();
        //cdt.enforceConstraints();
        double x;
        QuadEdgeSubdivision tris = cdt.getSubdivision();
        /*List<QuadEdge[]> triVerts= tris.getTriangleEdges(false);
        for(QuadEdge[] i: triVerts ){
            x =i[0].orig().circumRadiusRatio(i[1].orig(),i[2].orig());
            if (i[0].orig().circumRadiusRatio(i[1].orig(),i[2].orig())>1) {
                if (i[0].getLength()>i[1].getLength()){
                    cdt.insertSite(i[0].orig().midPoint(i[0].dest()).getCoordinate());
                }
                else{
                    if(i[1].getLength()>i[2].getLength()){
                    cdt.insertSite(i[1].orig().midPoint(i[1].dest()).getCoordinate());
                    }
                    else{
                        cdt.insertSite(i[2].orig().midPoint(i[2].dest()).getCoordinate());
                    }
                }
            }
        }*/
        //LineSegment l0 = new LineSegment(); LineSegment l1 = new LineSegment(); LineSegment l2 = new LineSegment();
        ArrayList<ConstraintVertex> newVerts = new ArrayList<>();
        List<Vertex[]> triVerts = tris.getTriangleVertices(false);
        for (Vertex[] i : triVerts) {
            //l0.setCoordinates(i[0].getCoordinate(), i[1].getCoordinate());
            //l1.setCoordinates(i[1].getCoordinate(), i[2].getCoordinate());
            //l2.setCoordinates(i[2].getCoordinate(), i[0].getCoordinate());
            //x =i[0].circumRadiusRatio(i[1],i[2]);

            if (i[0].circumRadiusRatio(i[1], i[2]) > 1) {
                Coordinate Coord = i[0].circleCenter(i[1], i[2]).getCoordinate();
                double X = i[0].circleCenter(i[1], i[2]).getCoordinate().getX();
                double Y = i[0].circleCenter(i[1], i[2]).getCoordinate().getY();
                if (X > 0 && X < canvasX) {
                    if (Y < 0) {
                        Coord.setY(0);
                        newVerts.add(new ConstraintVertex(Coord));
                    } else {
                        if (Y > canvasY) {
                            Coord.setY(canvasY);
                            newVerts.add(new ConstraintVertex(Coord));
                        } else {
                            newVerts.add(new ConstraintVertex(Coord));
                        }
                    }
                } else {
                    if (Y > 0 && Y < canvasY) {
                        if (X < 0) {
                            Coord.setX(0);
                            newVerts.add(new ConstraintVertex(Coord));
                        } else {
                            if (X > canvasX) {
                                Coord.setX(canvasX);
                                newVerts.add(new ConstraintVertex(Coord));
                            }
                        }
                    }
                }








                /*if (l0.getLength()>l1.getLength()){
                    if (l0.getLength()>l2.getLength()) {
                        cdt.insertSite(l0.midPoint());
                    }else{
                        cdt.insertSite(l2.midPoint());
                    }
                }else{
                    if(l1.getLength()>l2.getLength()){
                        cdt.insertSite(l1.midPoint());
                    }
                    else{
                        cdt.insertSite(l2.midPoint());
                    }
                }*/
            }
        }
        newVerts.addAll(cdt.getInitialVertices());
        return new ConformingDelaunayTriangulator(newVerts, 1);
    }

}
