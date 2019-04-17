package FluidSim.Mesh;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.poly2tri.Poly2Tri;
import org.poly2tri.triangulation.sets.PointSet;
import org.poly2tri.triangulation.util.PointGenerator;

public class FluidMesh {
    private ArrayList<FluidVertex> vertexList = new ArrayList<FluidVertex>();

    public FluidMesh() {

    }

    public ArrayList<FluidVertex> getVertexList() {
        return vertexList;
    }

    public void setVertexList(ArrayList<FluidVertex> vertexList) {
        this.vertexList = vertexList;
    }

    public void addVertex(FluidVertex fluidVertex) {
        vertexList.add(fluidVertex);
    }

    private Comparator<FluidVertex> compareXcoord = (FluidVertex fv1, FluidVertex fv2) ->  {
        if(fv1.getPositionX()==fv2.getPositionX()){return 0;}
        else{return fv1.getPositionX()>fv2.getPositionX()? 1:-1;}};

    public void sortByX(){
        vertexList.sort(compareXcoord);

    }


}
