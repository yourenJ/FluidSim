package FluidSim.Mesh;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.triangulate.ConstraintVertex;
import org.locationtech.jts.triangulate.quadedge.QuadEdge;
import org.locationtech.jts.triangulate.quadedge.Vertex;

import java.util.ArrayList;
import java.util.List;

/** fluid vertex for lattice boltzmann method*/

public class FluidVertex extends ConstraintVertex {
    /*Grid directions*/
    static private final int CENTER = 0, RIGHT = 1, UP = 2, LEFT = 3, DOWN = 4;
    static private final int UP_RIGHT = 5, UP_LEFT = 6, DOWN_LEFT = 7, DOWN_RIGHT = 8;

    /*Fluid state variables here:*/
    DiscretisedVelocityDistributionPoint[] f = new DiscretisedVelocityDistributionPoint[9];

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

    public ArrayList<QuadEdge> getContainingQuadEdges() {
        return containingQuadEdges;
    }

    private void initialiseVelocityDistribution(){
        f[CENTER] = new DiscretisedVelocityDistributionPoint(0,0,1, 4./9.);
        f[RIGHT] = new DiscretisedVelocityDistributionPoint(1,0,0, 1./9.);
        f[UP] = new DiscretisedVelocityDistributionPoint(0,1,0, 1./9.);
        f[LEFT] = new DiscretisedVelocityDistributionPoint(-1,0,0, 1./9.);
        f[DOWN] = new DiscretisedVelocityDistributionPoint(0,-1,0, 1./9.);
        f[UP_RIGHT] = new DiscretisedVelocityDistributionPoint(1,1,0, 1./36.);
        f[UP_LEFT] = new DiscretisedVelocityDistributionPoint(-1,1,0, 1./36.);
        f[DOWN_LEFT] = new DiscretisedVelocityDistributionPoint(-1,-1,0, 1./36.);
        f[DOWN_RIGHT] = new DiscretisedVelocityDistributionPoint(1,-1,0, 1./36.);
    }



    class SubTrianglePair{
        private QuadEdge quadEdge;
        private Coordinate edgeMidpoint;
        private Coordinate triMidPoint;
        private double[] NormalPlus;
        private double[] NormalMinus;
        private double area;

    }


    /**encapsulates data about a particular discretised velocity in the lattice boltzmann method for a vertex*/
    class DiscretisedVelocityDistributionPoint{
        private double representedXVelocity;
        private double representedYVelocity;
        private double weight;
        double probabilityDensity;
        double[] streamingArray;

        DiscretisedVelocityDistributionPoint(double xVel, double yVel, double probabilityDensity, double weight){
            this.probabilityDensity= probabilityDensity;
            this.weight = weight;
            representedXVelocity=xVel;
            representedYVelocity=yVel;

        }

        public double getWeight() {
            return weight;
        }

        public double XVel() {
            return representedXVelocity;
        }

        public double YVel() {
            return representedYVelocity;
        }

    }
}
