package FluidSim.Mesh;

import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Array;
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


    }

    @Test
    public void setVertexList() {
    }

    @Test
    public void addVertex() {
    }


}