package FluidSim.Mesh;



import javafx.scene.shape.Polygon;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class PolygonDistanceField {
    private Polygon polygon;
    private BufferedImage image;
    private double pixelsPerUnitDistance;
    private DomainCoordinate domainBounds;
    public Image1DArray D;

    public PolygonDistanceField(double domainWidth, double domainHeight, double pixelsPerUnitDistance, Polygon polygon){ /**TODO: big cleanup, make it extend abstract pixel-sampled domain*/
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
        Image1DArray B = new Image1DArray(A, image.getWidth(), image.getHeight());
        Image1DArray C = distanceTransform2D(B);

        D = new Image1DArray(DoubleStream.of(C.rawImageArray).map(d -> Math.round(255/(0.01*Math.pow(d,1.)+1))).toArray(), image.getWidth(), image.getHeight());
        //Image1DArray E = Dithering.doFloydSteinbergDithering(C);
        int[] F = DoubleStream.of(D.rawImageArray).mapToInt(d -> (int) Math.round(d)).toArray();

        image= new BufferedImage(getPixelWidth(), getPixelHeight(), BufferedImage.TYPE_BYTE_GRAY);
        image.getRaster().setPixels(0, 0, getPixelWidth(), getPixelHeight(), F);
    }

    public Image1DArray distanceTransform2D(Image1DArray func2D){
        Image1DArray distanceTransformedfunc2D = new Image1DArray(func2D.width, func2D.height);
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

    public BufferedImage getImage() {
        return image;
    }

    public DomainCoordinate getDomainBounds() {
        return domainBounds;
    }

    public PixelCoordinate getPixelBounds() {
        PixelCoordinate x = domainToPixelCoord(domainBounds);
        return domainToPixelCoord(domainBounds);
    }

    public int getPixelWidth(){
        return getPixelBounds().pixel_xCoord;
    }

    public int getPixelHeight(){
        int x = getPixelBounds().pixel_yCoord;
        return getPixelBounds().pixel_yCoord;
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

    public int domainToPixelCoord(double domainCoord){
        int x= (int) Math.ceil(pixelsPerUnitDistance*domainCoord);
        return x;
    }

    public double pixelToDomainCoord(int pixelCoord){
        double x= (pixelCoord+1./2.)/pixelsPerUnitDistance;
        return x;
    }


    public PixelCoordinate domainToPixelCoord(DomainCoordinate domainCoord){
        int x= (int) Math.ceil(pixelsPerUnitDistance*domainCoord.domain_xCoord);
        int y= (int) Math.ceil(pixelsPerUnitDistance*domainCoord.domain_yCoord);
        return new PixelCoordinate(x,y);
    }

    public DomainCoordinate pixelToDomainCoord(PixelCoordinate pixelCoord){
        double x= (pixelCoord.pixel_xCoord+1./2.)/pixelsPerUnitDistance;
        double y= (pixelCoord.pixel_yCoord+1./2.)/pixelsPerUnitDistance;
        return new DomainCoordinate(x, y);
    }


    class DomainCoordinate {
        double domain_xCoord, domain_yCoord;
        public DomainCoordinate(double domain_xCoord, double domain_yCoord){
            this.domain_xCoord=domain_xCoord;
            this.domain_yCoord=domain_yCoord;
        }
    }

    class PixelCoordinate {
        int pixel_xCoord, pixel_yCoord;
        public PixelCoordinate(int pixel_xCoord, int pixel_yCoord){
            this.pixel_xCoord=pixel_xCoord;
            this.pixel_yCoord=pixel_yCoord;
        }
    }

}

