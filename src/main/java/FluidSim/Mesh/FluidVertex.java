package FluidSim.Mesh;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.triangulate.ConstraintVertex;
import org.locationtech.jts.triangulate.quadedge.QuadEdge;
import org.locationtech.jts.triangulate.quadedge.Vertex;

import java.util.ArrayList;

/** fluid vertex for lattice boltzmann method*/

public class FluidVertex extends ConstraintVertex {
    /*Grid directions*/
    static private final int CENTER = 0, RIGHT = 1, UP = 2, LEFT = 3, DOWN = 4;
    static private final int UP_RIGHT = 5, UP_LEFT = 6, DOWN_LEFT = 7, DOWN_RIGHT = 8;

    /*Fluid state variables here:*/
    DiscretisedVelocityDistributionDirection[] f = new DiscretisedVelocityDistributionDirection[9];

    private ArrayList<QuadEdge> containingQuadEdges = new ArrayList<>();



    public FluidVertex(Coordinate _p) {
        super(_p);
        initialiseVelocityDistribution();
    }

    public void initializeStreamingArrays(){
        for(int i=0; i<f.length; i++){
            f[i].streamingArray = new double[containingQuadEdges.size()];
        }
    }

    public static void findContainingQuadEdges(ArrayList<QuadEdge> listToSearch){
        for(QuadEdge Q : listToSearch){
            if(Q.orig() instanceof FluidVertex) {
                FluidVertex fluidVertex = (FluidVertex) Q.orig();
                fluidVertex.getContainingQuadEdges().add(Q);
            }
        }
    }

    public void orderContainingQuadEdges(){
        QuadEdge quadEdge = containingQuadEdges.get(0);
        for(int i = 0; i < containingQuadEdges.size(); i++){

        }
    }

    public ArrayList<QuadEdge> getContainingQuadEdges() {
        return containingQuadEdges;
    }

    private void initialiseVelocityDistribution(){
        f[CENTER] = new DiscretisedVelocityDistributionDirection(0,0,1, 4./9.);
        f[RIGHT] = new DiscretisedVelocityDistributionDirection(1,0,0, 1./9.);
        f[UP] = new DiscretisedVelocityDistributionDirection(0,1,0, 1./9.);
        f[LEFT] = new DiscretisedVelocityDistributionDirection(-1,0,0, 1./9.);
        f[DOWN] = new DiscretisedVelocityDistributionDirection(0,-1,0, 1./9.);
        f[UP_RIGHT] = new DiscretisedVelocityDistributionDirection(1,1,0, 1./36.);
        f[UP_LEFT] = new DiscretisedVelocityDistributionDirection(-1,1,0, 1./36.);
        f[DOWN_LEFT] = new DiscretisedVelocityDistributionDirection(-1,-1,0, 1./36.);
        f[DOWN_RIGHT] = new DiscretisedVelocityDistributionDirection(1,-1,0, 1./36.);
    }



    class SubTrianglePair{
        private QuadEdge quadEdge;
        private Vertex edgeMidpoint;
        private Vertex triMidPoint;
        private double[] NormalPlus;
        private double[] NormalMinus;
        private double area;

        public SubTrianglePair(QuadEdge quadEdge){
            this.quadEdge=quadEdge;
            calculateGeometry();
        }

        public void calculateGeometry(){
            this.edgeMidpoint = quadEdge.dest().midPoint(quadEdge.orig());
            this.triMidPoint = new Vertex(new Coordinate((quadEdge.orig().getX()+quadEdge.dest().getX()+quadEdge.oNext().dest().getX())/3., (quadEdge.orig().getY()+quadEdge.dest().getY()+quadEdge.oNext().dest().getY())/3.))
            
        }
    }


    /**encapsulates data about a particular discretised velocity in the lattice boltzmann method for a vertex*/
    class DiscretisedVelocityDistributionDirection {
        private double xVelocity;
        private double yVelocity;
        private double weight;
        public double probabilityDensity;
        double[] streamingArray;

        DiscretisedVelocityDistributionDirection(double xVel, double yVel, double probabilityDensity, double weight){
            this.probabilityDensity= probabilityDensity;
            this.weight = weight;
            xVelocity =xVel;
            yVelocity =yVel;

        }

        public double getWeight() {
            return weight;
        }

        public double XVel() {
            return xVelocity;
        }

        public double YVel() {
            return yVelocity;
        }



    }
}
