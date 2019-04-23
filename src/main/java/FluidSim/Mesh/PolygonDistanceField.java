package FluidSim.Mesh;

import javafx.scene.shape.Polygon;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class PolygonDistanceField extends PixelSampledFunction{
    private Polygon polygon;
    private BufferedImage image;

    public PolygonDistanceField(double domainWidth, double domainHeight, double pixelsPerUnitDistance, Polygon polygon){ /**TODO: big cleanup*/
        super(domainWidth, domainHeight, pixelsPerUnitDistance);

        domainBounds= new DomainCoordinate(domainWidth,domainHeight);
        this.polygon= polygon;
        this.pixelsPerUnitDistance= pixelsPerUnitDistance;
        image= new BufferedImage(getPixelWidth(), getPixelHeight(), BufferedImage.TYPE_BYTE_BINARY);
        Graphics2D graphics2D = image.createGraphics();
        graphics2D.setBackground(Color.WHITE);
        graphics2D.clearRect(0, 0 , image.getWidth(),image.getHeight());
        graphics2D.setColor(Color.BLACK);
        graphics2D.fill(JFXPolyToAWTPoly(polygon));
        double[] A = IntStream.of(image.getData().getPixels(0, 0,image.getWidth(),image.getHeight(), new int[image.getWidth()*image.getHeight()])).mapToDouble(d -> (double) d*Double.MAX_VALUE).toArray();
        Image1DDoubleArray B = new Image1DDoubleArray(A, image.getWidth(), image.getHeight());
        Image1DDoubleArray B2 = distanceTransform2D(B);
        double max = B2.getMaxPixelValue();
        functionAsPixelSamples = new Image1DDoubleArray(DoubleStream.of(B2.rawImageArray).map(d -> d==0? max+1 : d).toArray(), image.getWidth(), image.getHeight());
        Image1DDoubleArray C = new Image1DDoubleArray(DoubleStream.of(functionAsPixelSamples.rawImageArray).map(d -> Math.round(255*d/max)).toArray(), image.getWidth(), image.getHeight());
        //Image1DDoubleArray E = Dithering.doFloydSteinbergDithering(C);
        int[] F = DoubleStream.of(C.rawImageArray).mapToInt(d -> (int) Math.round(d)).toArray();

        image= new BufferedImage(getPixelWidth(), getPixelHeight(), BufferedImage.TYPE_BYTE_GRAY);
        image.getRaster().setPixels(0, 0, getPixelWidth(), getPixelHeight(), F);
    }

    public BufferedImage getImage() {
        return image;
    }

    public Image1DDoubleArray distanceTransform2D(Image1DDoubleArray func2D){
        Image1DDoubleArray distanceTransformedfunc2D = new Image1DDoubleArray(func2D.width, func2D.height);
        for ( int i = 0; i < func2D.height; i++) {
            distanceTransformedfunc2D.setPixelRow(i, distanceTransform1D(func2D.getPixelRow(i)));
        }
        for ( int i = 0; i < func2D.width; i++) {
            distanceTransformedfunc2D.setPixelColumn(i, distanceTransform1D(distanceTransformedfunc2D.getPixelColumn(i)));
        }

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

