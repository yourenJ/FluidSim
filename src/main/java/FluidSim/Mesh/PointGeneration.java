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

    public static ArrayList<ConstraintVertex> AcceptanceRejectionMethod(PixelSampledFunction probabilityDensityFunction, int numberOfPoints){
        Image1DDoubleArray functionAsPixelSamples = probabilityDensityFunction.functionAsPixelSamples;
        double maxValue = functionAsPixelSamples.getMaxPixelValue();
        DoubleUnaryOperator inputModulation = d -> Math.pow(Math.pow(maxValue, 1./2.) - Math.pow(d, 1./2.),4) + 1; /*TODO: dithering should not be responsible for this?.*/
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


}
