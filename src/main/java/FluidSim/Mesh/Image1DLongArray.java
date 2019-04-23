package FluidSim.Mesh;

import java.util.Arrays;


/** encapsulates a 2d image in 1D array form with methods for accessing pixels with x, y coordinates*/
public class Image1DLongArray {

    public long[] rawImageArray;
    public final int width;
    public final int height;

    Image1DLongArray(long[] rawImageArray, int width, int height ){
        this.rawImageArray=rawImageArray;
        this.width=width;
        this.height=height;
    }

    Image1DLongArray(int width, int height ){
        this.rawImageArray= new long[width*height];
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

    public long[] getPixelRow(int y){
        return Arrays.copyOfRange(rawImageArray, y*width, (y+1)*width);
    }

    public void setPixel(int x, int y, long value){
        if(x>this.width||y>this.height){
            throw new ArrayIndexOutOfBoundsException();
        }
        rawImageArray[y*width+x] = value;
    }

    public void setPixelRow(int y, long[] values){
        for( int i = 0; i < width; i++){
            setPixel(i, y, values[i]);
        }
    }

    public void setPixelColumn(int x, long[] values){
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

}

