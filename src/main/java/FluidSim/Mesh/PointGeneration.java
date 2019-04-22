package FluidSim.Mesh;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.triangulate.ConstraintVertex;

import java.util.ArrayList;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.DoubleStream;

public class PointGeneration {
    public static ArrayList<ConstraintVertex> AcceptanceRejectionMethod(PolygonDistanceField probabilityDensityFunction){
        DoubleUnaryOperator inputModulation = d -> d/probabilityDensityFunction.D.getMaxPixelValue(); /*TODO: dithering should not be responsible for this?.*/

        Image1DArray probDensFuncArray = new Image1DArray(DoubleStream.of(probabilityDensityFunction.D.rawImageArray).map(inputModulation).toArray(),probabilityDensityFunction.D.width, probabilityDensityFunction.D.height);

        ConstraintVertex A= new ConstraintVertex(new Coordinate(1,2 ));
        ArrayList<ConstraintVertex> generatedPoints = new ArrayList<>();
        while(generatedPoints.size()<50){
            double randomXCoord = probabilityDensityFunction.getDomainBounds().domain_xCoord*Math.random();
            double randomYCoord = probabilityDensityFunction.getDomainBounds().domain_yCoord*Math.random();
            int randomXPixel = probabilityDensityFunction.domainToPixelCoord(randomXCoord);
            int randomYPixel = probabilityDensityFunction.domainToPixelCoord(randomYCoord);
            double acceptanceChance = Math.random();
            if(acceptanceChance>probDensFuncArray.getPixel(randomXPixel,randomYPixel)){
                generatedPoints.add(new ConstraintVertex( new Coordinate(randomXCoord, randomYCoord)));
            }
        }



    }
}
