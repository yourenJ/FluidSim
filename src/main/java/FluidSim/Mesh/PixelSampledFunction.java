package FluidSim.Mesh;


public abstract class PixelSampledFunction {
    protected Image1DDoubleArray functionAsPixelSamples;
    protected double pixelsPerUnitDistance;
    protected DomainCoordinate domainBounds;


    public PixelSampledFunction(double domainWidth, double domainHeight, double pixelsPerUnitDistance){
        this.domainBounds = new DomainCoordinate(domainWidth,domainHeight);
        this.pixelsPerUnitDistance = pixelsPerUnitDistance;
    }

    public Image1DDoubleArray getFunctionAsPixelSamples() {
        return functionAsPixelSamples;
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


    public int domainToPixelCoord(double domainCoord){
        int x= (int) Math.floor(pixelsPerUnitDistance*domainCoord);
        return x;
    }

    public double pixelToDomainCoord(int pixelCoord){
        double x= (pixelCoord+1./2.)/pixelsPerUnitDistance;
        return x;
    }


    public PixelCoordinate domainToPixelCoord(DomainCoordinate domainCoord){
        int x= (int) Math.floor(pixelsPerUnitDistance*domainCoord.domain_xCoord);
        int y= (int) Math.floor(pixelsPerUnitDistance*domainCoord.domain_yCoord);
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

