package FluidSim.Mesh;

import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

import static org.junit.Assert.*;

public class FluidMeshTest {

    @Test
    public void getVertexList() {

        BufferedImage image= new BufferedImage(30,30, BufferedImage.TYPE_BYTE_BINARY);

        Graphics2D graphics2D = image.createGraphics();
        graphics2D.setBackground(Color.WHITE);
        graphics2D.clearRect(0, 0 , 30, 30);
        graphics2D.setColor(Color.BLACK);
        graphics2D.fill(new Polygon(new int[]{10, 20, 20, 10},new int[]{10, 10, 20, 20}, 4));
        int[] B=image.getData().getPixels(0, 0,30,30, new int[900]);

    }

    @Test
    public void setVertexList() {
    }

    @Test
    public void addVertex() {
    }

    @Test
    public void sortByX() {
        FluidMesh meshtest = new FluidMesh();
        FluidVertex fv1 = new FluidVertex(1,2);
        FluidVertex fv2 = new FluidVertex(2,2);
        FluidVertex fv3 = new FluidVertex(3,2);
        meshtest.addVertex( new FluidVertex(2,2));
        meshtest.addVertex( new FluidVertex(1,2));
        meshtest.addVertex( new FluidVertex(3,2));
        FluidVertex fv4 = meshtest.getVertexList().get(1);
        meshtest.sortByX();
        List<FluidVertex> L1 = new ArrayList<>();
        L1.add(0,fv1); L1.add(1,fv2); L1.add(2,fv3);

        assertTrue(meshtest.getVertexList().equals(L1));
    }
}