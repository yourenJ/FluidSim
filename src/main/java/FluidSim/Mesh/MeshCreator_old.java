package FluidSim.Mesh;


import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import org.poly2tri.Poly2Tri;
import org.poly2tri.geometry.polygon.Polygon;
import org.poly2tri.geometry.polygon.PolygonPoint;
import org.poly2tri.triangulation.delaunay.DelaunayTriangle;
import org.poly2tri.triangulation.sets.PointSet;
import org.poly2tri.triangulation.util.PointGenerator;
import poly2triextention.PointGeneratorExtention;

import org.locationtech.jts.triangulate.ConformingDelaunayTriangulationBuilder;

import java.util.Arrays;
import java.util.List;

public class MeshCreator_old {
    //public static void main(String[] args) { }
        // Prepare input data
    public static List<DelaunayTriangle> doTriangulation(Polygon polygonHole, double canvasSizeX, double canvasSizeY ){
        Polygon polygon = new Polygon(Arrays.asList(new PolygonPoint(0, 0),
                new PolygonPoint(canvasSizeX, 0),new PolygonPoint(canvasSizeX, canvasSizeY),new PolygonPoint(0, canvasSizeY)));

        //Polygon polygon = new Polygon(Arrays.asList(new PolygonPoint(0, 0),
        //        new PolygonPoint(10, 0), new PolygonPoint(10, 10), new PolygonPoint(0, 10)));

        //Polygon polygonhole = new Polygon(Arrays.asList(new PolygonPoint(8, 1),
        //        new PolygonPoint(6, 2),new PolygonPoint(2, 7),new PolygonPoint(1, 9)));

        PointSet ps = new PointSet(PointGeneratorExtention.uniformDistribution(50,0,canvasSizeX,0, canvasSizeY));
        polygon.addSteinerPoints(ps.getPoints());
        polygon.addHole(polygonHole);
        // Launch tessellation
        Poly2Tri.triangulate(polygon);
        // Gather triangles
        return polygon.getTriangles();
    }
    public static javafx.scene.shape.Polygon TriToFXPoly(DelaunayTriangle tri){
        javafx.scene.shape.Polygon fxPoly = new javafx.scene.shape.Polygon(tri.points[0].getX() ,tri.points[0].getY(),
                tri.points[1].getX() ,tri.points[1].getY(),tri.points[2].getX() ,tri.points[2].getY());
        fxPoly.setFill(Color.BLACK.deriveColor(0, 1.2, 1, 0.2));
        fxPoly.setStroke(Color.FORESTGREEN);
        fxPoly.setStrokeWidth(1);
        fxPoly.setStrokeLineCap(StrokeLineCap.ROUND);
        return fxPoly;
    }
}

