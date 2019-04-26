package FluidSim.Mesh;


import java.util.function.DoubleUnaryOperator;
import java.util.stream.DoubleStream;


/** this is unused*/
public class Dithering {

    public static Image1DDoubleArray doFloydSteinbergDithering(Image1DDoubleArray inputImage){

        DoubleUnaryOperator inputModulation = d -> Math.round(251/(0.01*Math.pow(d, 0.8) + 1) + 4); /*TODO: dithering should not be responsible for this.*/

        Image1DDoubleArray outputImage = new Image1DDoubleArray(DoubleStream.of(inputImage.rawImageArray).map(inputModulation).toArray(), inputImage.width, inputImage.height);
        double maxPixel = outputImage.getMaxPixelValue();
        double A = 1.;

        for(int y=0; y<inputImage.height; y++){
            if(y%2==0) {
                for (int x = 0; x < inputImage.width; x++) {
                    double oldPixel = outputImage.getPixel(x, y);
                    double newPixel = maxPixel * Math.round(0.3 + oldPixel / maxPixel * 0.4);
                    outputImage.setPixel(x, y, newPixel);
                    double quantError = oldPixel - newPixel;
                    int a, b;

                    if (x != inputImage.width - 1) {
                        a = x + 1; b = y;
                        outputImage.setPixel(a, b, outputImage.getPixel(a, b) + A * quantError * 7.0 / 16);
                    }
                    if (y != inputImage.height - 1 && x != 0) {
                        a = x - 1; b = y + 1;
                        outputImage.setPixel(a, b, outputImage.getPixel(a, b) + A * quantError * 3.0 / 16);
                    }
                    if (y != inputImage.height - 1) {
                        a = x; b = y + 1;
                        outputImage.setPixel(a, b, outputImage.getPixel(a, b) + A * quantError * 5.0 / 16);
                    }
                    if (y != inputImage.height - 1 && x != inputImage.width - 1) {
                        a = x + 1; b = y + 1;
                        outputImage.setPixel(a, b, outputImage.getPixel(a, b) + A * quantError * 1.0 / 16);
                    }
                }
            }

            else {
                for (int x = inputImage.width-1; x >= 0; x--) {
                    double oldPixel = outputImage.getPixel(x, y);
                    double newPixel = maxPixel * Math.round(0.3 + Math.pow(oldPixel / maxPixel, 1. / 2) * 0.4);
                    outputImage.setPixel(x, y, newPixel);
                    double quantError = oldPixel - newPixel;
                    int a, b;

                    if (x != 0) {
                        a = x - 1; b = y;
                        outputImage.setPixel(a, b, outputImage.getPixel(a, b) + A * quantError * 7.0 / 16);
                    }
                    if (y != inputImage.height - 1 && x != inputImage.width - 1) {
                        a = x + 1; b = y + 1;
                        outputImage.setPixel(a, b, outputImage.getPixel(a, b) + A * quantError * 3.0 / 16);
                    }
                    if (y != inputImage.height - 1) {
                        a = x; b = y + 1;
                        outputImage.setPixel(a, b, outputImage.getPixel(a, b) + A * quantError * 5.0 / 16);
                    }
                    if (y != inputImage.height - 1 && x != 0) {
                        a = x - 1; b = y + 1;
                        outputImage.setPixel(a, b, outputImage.getPixel(a, b) + A * quantError * 1.0 / 16);
                    }
                }
            }

        }

        return outputImage;
    }


}
