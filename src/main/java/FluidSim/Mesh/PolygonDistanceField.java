package FluidSim.Mesh;

import javafx.scene.shape.Polygon;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class PolygonDistanceField extends PixelSampledFunction{
    private Polygon polygon;
    private BufferedImage image;

    public PolygonDistanceField(double domainWidth, double domainHeight, double pixelsPerUnitDistance, Polygon polygon){ /**TODO: big cleanup*/
        super(domainWidth, domainHeight, pixelsPerUnitDistance);
        this.domainBounds= new DomainCoordinate(domainWidth,domainHeight);
        this.polygon= polygon;
        this.pixelsPerUnitDistance= pixelsPerUnitDistance;
        BufferedImage workingImage = new BufferedImage(getPixelWidth()+1, getPixelHeight()+1, BufferedImage.TYPE_BYTE_BINARY);

        Graphics2D imageEditor = workingImage.createGraphics();
        imageEditor.setBackground(Color.WHITE);
        imageEditor.clearRect(0, 0 , workingImage.getWidth(),workingImage.getHeight());
        imageEditor.setColor(Color.BLACK);
        imageEditor.fill(JFXPolyToAWTPoly(polygon));

        double[] WorkingImageAsRawDouble = IntStream.of(workingImage.getData().getPixels(0, 0,workingImage.getWidth(),workingImage.getHeight(), new int[workingImage.getWidth()*workingImage.getHeight()])).mapToDouble(d -> (double) d*Double.MAX_VALUE).toArray();
        Image1DDoubleArray workingImageDistanceTransform = distanceTransform2D(new Image1DDoubleArray(WorkingImageAsRawDouble, workingImage.getWidth(), workingImage.getHeight()));
        double maxPixelValue = workingImageDistanceTransform.getMaxPixelValue();
        this.functionAsPixelSamples = new Image1DDoubleArray(DoubleStream.of(workingImageDistanceTransform.rawImageArray).map(d -> d==0? maxPixelValue+1 : d).toArray(), workingImage.getWidth(), workingImage.getHeight());
        Image1DDoubleArray distanceTransformRemapped = new Image1DDoubleArray(DoubleStream.of(functionAsPixelSamples.rawImageArray).map(d -> Math.round(255*Math.sqrt(d/maxPixelValue))).toArray(), workingImage.getWidth(), workingImage.getHeight());
        int[] distanceTransformAsInt = DoubleStream.of(distanceTransformRemapped.rawImageArray).mapToInt(d -> (int) Math.round(d)).toArray();

        this.image= new BufferedImage(workingImage.getWidth(), workingImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        this.image.getRaster().setPixels(0, 0, getPixelWidth()+1, getPixelHeight()+1, distanceTransformAsInt);
    }

    public BufferedImage getImage() {
        return image;
    }

    public Image1DDoubleArray distanceTransform2D(Image1DDoubleArray func2D){
        Image1DDoubleArray distanceTransformedfunc2D = new Image1DDoubleArray(func2D.width, func2D.height);
        ExecutorService executor = Executors.newFixedThreadPool(8);
        for ( int i = 0; i < func2D.height; i++) {
            final int x = i;
            executor.execute(()->distanceTransformedfunc2D.setPixelRow(x, distanceTransform1D(func2D.getPixelRow(x))));
            //distanceTransformedfunc2D.setPixelRow(i, distanceTransform1D(func2D.getPixelRow(i)));
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        } catch (InterruptedException e) {}
        executor = Executors.newFixedThreadPool(8);
        for ( int i = 0; i < func2D.width; i++) {
            final int x = i;
            executor.execute(()-> distanceTransformedfunc2D.setPixelColumn(x, distanceTransform1D(distanceTransformedfunc2D.getPixelColumn(x))));
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        } catch (InterruptedException e) {}
        return distanceTransformedfunc2D;
    }


    public double[] distanceTransform1D(double[] func){
        double[] distanceTransform = new double[func.length];
        int k = 0;
        int[] vertices = new int[func.length];
        double[] intersections = new double[func.length+1];
        vertices[0]=0;
        intersections[0]=Double.NEGATIVE_INFINITY;
        intersections[1]=Double.POSITIVE_INFINITY;
        for (int q=1; q<func.length; q++){
            double s = parabolaIntersection(q, func[q], vertices[k],func[vertices[k]]);
            if (s<=intersections[k]) {
                k--;
                q--;
            } else {
                k++;
                vertices[k]=q;
                intersections[k]=s;
                intersections[k+1]=Double.POSITIVE_INFINITY;
            }
        }
        k=0;
        for (int q=0; q<func.length; q++) {
            while (intersections[k+1]<q){
                k++;
            }
            distanceTransform[q] = ( q - vertices[k] )*( q - vertices[k] ) + func[vertices[k]];
        }
        return distanceTransform;
    }

    private double parabolaIntersection(int vertexX1, double vertexY1,int vertexX2, double vertexY2){
        return ( (vertexY1 + vertexX1*vertexX1) - (vertexY2 + vertexX2*vertexX2) )/( 2.0*(vertexX1 - vertexX2) );
    }

    public java.awt.Polygon JFXPolyToAWTPoly(Polygon polygon){
        Double[] a= polygon.getPoints().toArray(new Double[0]);
        int[] xPoints = new int[a.length/2];
        int[] yPoints = new int[a.length/2];
        for (int i=0; i<a.length; i+=2 ){
            xPoints[i/2]=domainToPixelCoord(a[i]);
            yPoints[i/2]=domainToPixelCoord(a[i+1]);
        }
        return new java.awt.Polygon(xPoints,yPoints,a.length/2);
    }

}

