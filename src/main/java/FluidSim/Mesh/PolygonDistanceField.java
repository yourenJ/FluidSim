package FluidSim.Mesh;



import javafx.scene.shape.Polygon;

import java.awt.*;
import java.awt.image.BufferedImage;
///
public class PolygonDistanceField {
    private Polygon polygon;
    private BufferedImage image;
    private double pixelsPerUnitDistance;
    private DomainCoordinate domainBounds;

    public PolygonDistanceField(double domainWidth, double domainHeight, double pixelsPerUnitDistance, Polygon polygon){
        domainBounds= new DomainCoordinate(domainWidth,domainHeight);
        this.polygon= polygon;

        image= new BufferedImage(getPixelWidth(), getPixelHeight(), BufferedImage.TYPE_BYTE_BINARY);
        Graphics2D graphics2D = image.createGraphics();
        graphics2D.setBackground(Color.WHITE);
        graphics2D.clearRect(0, 0 , image.getWidth(),image.getHeight());
        graphics2D.setColor(Color.BLACK);
        graphics2D.fill(JFXPolyToAWTPoly(polygon));

    }

    public PixelCoordinate getPixelBounds() {
        return domainToPixelCoord(domainBounds);
    }

    public int getPixelWidth(){
        return getPixelBounds().pixel_xCoord;
    }

    public int getPixelHeight(){
        return getPixelBounds().pixel_yCoord;
    }

    public java.awt.Polygon JFXPolyToAWTPoly(Polygon polygon){
        Double[] a= polygon.getPoints().toArray(new Double[0]);
        int[] xPoints = new int[a.length/2];
        int[] yPoints = new int[a.length/2];
        for (int i=0; i<a.length; i+=2 ){
            xPoints[i]=domainToPixelCoord(a[i]);
            yPoints[i+1]=domainToPixelCoord(a[i+1]);
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

