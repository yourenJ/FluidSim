package FluidSim.Mesh;

import java.util.ArrayList;
import java.util.List;

public class FluidVertex {
    private double positionX;
    private double positionY;
    /*Fluid state variables here:*/
    //
    //

    private List<FluidVertex> neighbours = new ArrayList<FluidVertex>();

    public FluidVertex(double x, double y){
        positionX=x;
        positionY=y;
    }

    /*getters & setters*/
    public double getPositionX() {
        return positionX;
    }

    public void setPositionX(double positionX) {
        this.positionX = positionX;
    }

    public double getPositionY() {
        return positionY;
    }

    public void setPositionY(double positionY) {
        this.positionY = positionY;
    }

    public List<FluidVertex> getNeighbours() {
        return neighbours;
    }

    public void setNeighbours(List<FluidVertex> neighbours) {
        this.neighbours = neighbours;
    }
    /**/

    public void addNeighbour(FluidVertex neighbour){
        this.neighbours.add(neighbour);
    }
}
