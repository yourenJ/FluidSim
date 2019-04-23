package FluidSim.Mesh;

import java.util.Arrays;


/** encapsulates a 2d image in 1D array form with methods for accessing pixels with x, y coordinates*/
public class Image1DDoubleArray {

    public double[] rawImageArray;
    public final int width;
    public final int height;

    Image1DDoubleArray(double[] rawImageArray, int width, int height ){
        this.rawImageArray=rawImageArray;
        this.width=width;
        this.height=height;
    }

    Image1DDoubleArray(int width, int height ){
        this.rawImageArray= new double[width*height];
        this.width=width;
        this.height=height;
    }

    public double getPixel(int x, int y){
        if(x>this.width||y>this.height){
            throw new ArrayIndexOutOfBoundsException();
        }
        return rawImageArray[y*width+x];
    }

    public double[] getPixelColumn(int x){
        double[] column = new double[height];
        for( int i = 0; i < height; i++){
            column[i]= getPixel(x, i);
        }
        return column;
    }

    public double[] getPixelRow(int y){
        return Arrays.copyOfRange(rawImageArray, y*width, (y+1)*width);
    }

    public void setPixel(int x, int y, double value){
        if(x>this.width||y>this.height){
            throw new ArrayIndexOutOfBoundsException();
        }
        rawImageArray[y*width+x] = value;
    }

    public void setPixelRow(int y, double[] values){
        for( int i = 0; i < width; i++){
            setPixel(i, y, values[i]);
        }
    }

    public void setPixelColumn(int x, double[] values){
        for( int i = 0; i < height; i++){
            setPixel(x, i, values[i]);
        }
    }

    public double getMaxPixelValue(){
        double max = rawImageArray[0];
        for(double pixel: rawImageArray){
            max = Math.max(max, pixel);
        }
        return max;
    }

    public double getMinPixelValue(){
        double min = rawImageArray[0];
        for(double pixel: rawImageArray){
            min = Math.min(min, pixel);
        }
        return min;
    }

    public int[] getXYCoordFromArrayIndex(int arrayIndex){
        return new int[]{arrayIndex%width,arrayIndex/width};
    }

}

