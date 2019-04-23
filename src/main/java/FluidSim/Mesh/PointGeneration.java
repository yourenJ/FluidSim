package FluidSim.Mesh;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.triangulate.ConstraintVertex;

import java.util.ArrayList;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.DoubleStream;

public class PointGeneration {
    public static ArrayList<ConstraintVertex> AcceptanceRejectionMethod2(PixelSampledFunction probabilityDensityFunction){
        Image1DDoubleArray functionAsPixelSamples = probabilityDensityFunction.functionAsPixelSamples;
        double maxValue = functionAsPixelSamples.getMaxPixelValue();
        DoubleUnaryOperator inputModulation = d -> 1./(0.1*Math.pow(d, 2.) + 1.) + 0.01; /*TODO: dithering should not be responsible for this?.*/
        Image1DDoubleArray probDensFuncArray = new Image1DDoubleArray(DoubleStream.of(functionAsPixelSamples.rawImageArray).map(inputModulation).toArray(),functionAsPixelSamples.width, functionAsPixelSamples.height);

        ArrayList<ConstraintVertex> generatedPoints = new ArrayList<>();

        while(generatedPoints.size()<20000){
            double randomXCoord = probabilityDensityFunction.getDomainBounds().domain_xCoord*Math.random();
            double randomYCoord = probabilityDensityFunction.getDomainBounds().domain_yCoord*Math.random();
            int randomXPixel = probabilityDensityFunction.domainToPixelCoord(randomXCoord);
            int randomYPixel = probabilityDensityFunction.domainToPixelCoord(randomYCoord);
            double acceptanceChance = Math.random();
            if(acceptanceChance>probDensFuncArray.getPixel(randomXPixel,randomYPixel)){
                generatedPoints.add(new ConstraintVertex( new Coordinate(randomXCoord, randomYCoord)));
            }
        }

        return generatedPoints;
    }

    public static ArrayList<ConstraintVertex> AcceptanceRejectionMethod(PixelSampledFunction probabilityDensityFunction){
        Image1DDoubleArray functionAsPixelSamples = probabilityDensityFunction.functionAsPixelSamples;
        double maxValue = functionAsPixelSamples.getMaxPixelValue();
        DoubleUnaryOperator inputModulation = d -> Math.round(maxValue - d + 1); /*TODO: dithering should not be responsible for this?.*/
        Image1DDoubleArray probDensFuncArray = new Image1DDoubleArray(DoubleStream.of(functionAsPixelSamples.rawImageArray).map(inputModulation).toArray(),functionAsPixelSamples.width, functionAsPixelSamples.height);

        Image1DDoubleArray cumulativeDistribFuncArray = cumulativeDistributionFunction(probDensFuncArray);

        ArrayList<ConstraintVertex> generatedPoints = new ArrayList<>();

        while(generatedPoints.size()<20000){
            double randomXCoord = probabilityDensityFunction.getDomainBounds().domain_xCoord*Math.random();
            double randomYCoord = probabilityDensityFunction.getDomainBounds().domain_yCoord*Math.random();
            int randomXPixel = probabilityDensityFunction.domainToPixelCoord(randomXCoord);
            int randomYPixel = probabilityDensityFunction.domainToPixelCoord(randomYCoord);
            double acceptanceChance = Math.random();
            if(acceptanceChance>probDensFuncArray.getPixel(randomXPixel,randomYPixel)){
                generatedPoints.add(new ConstraintVertex( new Coordinate(randomXCoord, randomYCoord)));
            }
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

    }
}
