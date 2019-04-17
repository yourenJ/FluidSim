package Display;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SimImage extends JPanel {
    private static final int DEFAULT_WIDTH = 1000;
    private static final int DEFAULT_HEIGHT = 800;
    private int myTimerDelay=160; //ms
    private final Timer myTimer;
    private int[] strCoord = {10,20};

    public SimImage(){
        //super();
        setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        setBackground(Color.GRAY);
        myTimer= new Timer(myTimerDelay, simFrameTimer);
        myTimer.start();
    }

    ActionListener simFrameTimer = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent theEvent) {
            System.out.print("test");
            strCoord[0]+=10;
            //SimImage.this.redraw();

        }
    };
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.setColor(Color.GREEN);
        g.drawString("Custom String", strCoord[0], strCoord[1]);


    }
    public void redraw(){
        this.repaint();
    }
}

/*implements ActionListener{
    private JButton print;
    private JLabel label;

    public SimImage(){
        print = new JButton("Print");
        label = new JLabel("Hit this button to print to the console");

        setPreferredSize(new Dimension(245, 136));
        setLayout(null);

        add(print);
        add(label);

        print.setBounds(70, 10, 100, 25);
        label.setBounds(20, 45, 210, 35);

        print.addActionListener(SimImage.this);
    }

    public void actionPerformed(ActionEvent e){
        if(e.getSource() == print){
            System.out.println("big");
        }
    }
}
*/