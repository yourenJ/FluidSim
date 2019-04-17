package poly2triextention;

import org.poly2tri.triangulation.util.PointGenerator;
import java.util.ArrayList;
import java.util.List;

import org.poly2tri.triangulation.TriangulationPoint;
import org.poly2tri.triangulation.point.TPoint;

public class PointGeneratorExtention extends PointGenerator {

    public static List<TriangulationPoint> uniformDistribution( int n, double xMin, double xMax, double yMin, double yMax )
    {
        ArrayList<TriangulationPoint> points = new ArrayList<TriangulationPoint>();
        for( int i=0; i<n; i++ )
        {
            points.add( new TPoint( (xMax-xMin)*Math.random()+xMin, (yMax-yMin)*Math.random()+yMin ));
        }
        return points;
    }

}
