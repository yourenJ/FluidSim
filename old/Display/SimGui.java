package Display;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SimGui extends JPanel implements ActionListener{
    private JButton print;
    private JLabel label;

    public SimGui(){
        print = new JButton("Print");
        label = new JLabel("Hit this button to print to the console");

        setPreferredSize(new Dimension(245, 136));
        setLayout(null);

        add(print);
        add(label);

        print.setBounds(70, 10, 100, 25);
        label.setBounds(20, 45, 210, 35);

        print.addActionListener(SimGui.this);
    }

    public void actionPerformed(ActionEvent e){
        if(e.getSource() == print){
            System.out.println("big");
        }
    }
}