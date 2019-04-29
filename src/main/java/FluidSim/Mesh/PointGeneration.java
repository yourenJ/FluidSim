package FluidSim.Mesh;



import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.triangulate.ConformingDelaunayTriangulator;
import org.locationtech.jts.triangulate.ConstraintVertex;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.DoubleStream;

public class PointGeneration {

    public static ArrayList<ConstraintVertex> cumulativeDistributionMethod(PixelSampledFunction probabilityDensityFunction, int numberOfPoints){
        Image1DDoubleArray functionAsPixelSamples = probabilityDensityFunction.functionAsPixelSamples;
        double maxValue = functionAsPixelSamples.getMaxPixelValue();
        DoubleUnaryOperator inputModulation = d -> d==maxValue? 0 : 0.5 + 3000/(d+500);//Math.pow(Math.pow(maxValue, 1./2.) - Math.pow(d, 1./2.),4) + 1; /*TODO: dithering should not be responsible for this?. Also this map is pixelToDomainRatio dependant*/
        Image1DDoubleArray probDensFuncArray = new Image1DDoubleArray(DoubleStream.of(functionAsPixelSamples.rawImageArray).map(inputModulation).toArray(),functionAsPixelSamples.width, functionAsPixelSamples.height);

        Image1DDoubleArray cumulativeDistribFuncArray = cumulativeDistributionFunction(probDensFuncArray);

        ArrayList<ConstraintVertex> generatedPoints = new ArrayList<>();

        while(generatedPoints.size()<numberOfPoints){
            double randomPointInCumulativeFunc = cumulativeDistribFuncArray.rawImageArray[cumulativeDistribFuncArray.rawImageArray.length-1]*Math.random();
            int chosenPixelIndex = binarySearchCumulativeDistributionArray(cumulativeDistribFuncArray.rawImageArray,randomPointInCumulativeFunc);
            int[] chosenPixelXYCoords = probabilityDensityFunction.functionAsPixelSamples.getXYCoordFromArrayIndex(chosenPixelIndex);
            double randomXCoord = probabilityDensityFunction.pixelToDomainCoord(chosenPixelXYCoords[0])+Math.random()-0.5;
            double randomYCoord = probabilityDensityFunction.pixelToDomainCoord(chosenPixelXYCoords[1])+Math.random()-0.5;
            generatedPoints.add(new ConstraintVertex( new Coordinate(randomXCoord, randomYCoord)));
        }
        return generatedPoints;
    }


    /** the resulting cumulativeDistributionFunction.rawImageArray[i] = sum(0->i){probabilityDistributionFunction.rawImageArray[i]}
     * therefore a value lying between cumulativeDistributionFunction.rawImageArray[i] and [i+1] corresponds to pixel i+1*/

    private static Image1DDoubleArray cumulativeDistributionFunction(Image1DDoubleArray probabilityDistributionFunction){
        Image1DDoubleArray cumulativeDistribFuncArray = new Image1DDoubleArray(probabilityDistributionFunction.width, probabilityDistributionFunction.height);
        cumulativeDistribFuncArray.rawImageArray[0] = probabilityDistributionFunction.rawImageArray[0];
        for(int i = 1; i < probabilityDistributionFunction.rawImageArray.length; i++){
            cumulativeDistribFuncArray.rawImageArray[i] = cumulativeDistribFuncArray.rawImageArray[i-1] + probabilityDistributionFunction.rawImageArray[i];
        }
        return cumulativeDistribFuncArray;
    }


    /** returns the index of the first array element above the valueToLocate*/

    private static int binarySearchCumulativeDistributionArray(double[] cumulativeDistributionArrayToSearch, double valueToLocate){
        int left = 0;
        int right = cumulativeDistributionArrayToSearch.length-1;
        int middle = 0;
        while(right>=left){
            middle = (right + left)/2;
            if(cumulativeDistributionArrayToSearch[(right + left)/2] < valueToLocate){
                left = middle + 1;
            } else {
                if (cumulativeDistributionArrayToSearch[(right + left) / 2] > valueToLocate) {
                    right = middle - 1;
                } else {
                    return middle;
                }
            }
        }
        return middle;
    }

    public static ConformingDelaunayTriangulator lloydRelaxation(ConformingDelaunayTriangulator cdt, Constraints constraints, double canvasX, double canvasY, PixelSampledFunction probabilityDensityFunction){

        Image1DDoubleArray functionAsPixelSamples = probabilityDensityFunction.functionAsPixelSamples;
        double maxValue = functionAsPixelSamples.getMaxPixelValue();
        DoubleUnaryOperator inputModulation = d -> d==maxValue? 0 : 0.5 + 3000/(d+500); /*TODO: lloydRelaxation should not be responsible for this?. Also this map is pixelToDomainRatio dependant*/
        Image1DDoubleArray probDensFuncArray = new Image1DDoubleArray(DoubleStream.of(functionAsPixelSamples.rawImageArray).map(inputModulation).toArray(),functionAsPixelSamples.width, functionAsPixelSamples.height);


        GeometryFactory genericFactory = new GeometryFactory();

        ArrayList<ConstraintVertex> startVerts = new ArrayList<>();
        
        java.util.List polyList = cdt.getSubdivision().getVoronoiCellPolygons(genericFactory);

        for(int i = 0; i<polyList.size(); i++) {

            Polygon poly = (Polygon) polyList.get(i);
            Coordinate polyCentroid = poly.getCentroid().getCoordinate();
            if((polyCentroid.x>0 && polyCentroid.x<canvasX) && (polyCentroid.y>0 && polyCentroid.y<canvasY)) {

                Coordinate[] polyCoord = poly.getCoordinates();

                double xMoment = 0;
                double yMoment = 0;
                double totalMass = 0;

                for (Coordinate c : polyCoord) {
                    PixelSampledFunction.PixelCoordinate p = probabilityDensityFunction.domainToPixelCoord(probabilityDensityFunction.new DomainCoordinate(c.x, c.y));
                    double pixelMassAtCoordinate = probDensFuncArray.getPixel(p.pixel_xCoord, p.pixel_yCoord);
                    totalMass += pixelMassAtCoordinate;
                    xMoment += pixelMassAtCoordinate * c.x;
                    yMoment += pixelMassAtCoordinate * c.y;
                }
                double centerOfMassX = xMoment / totalMass;
                double centerOfMassY = yMoment / totalMass;
                startVerts.add(new ConstraintVertex(new Coordinate(centerOfMassX, centerOfMassY)));
            }
        }
        ConformingDelaunayTriangulator cdt2= new ConformingDelaunayTriangulator(startVerts, 1);

        cdt2.setConstraints((List) constraints.allSegs.clone(), (List) constraints.allSegVerts.clone());
        cdt2.formInitialDelaunay();
        cdt2.enforceConstraints();

        return cdt2;

    }


}
