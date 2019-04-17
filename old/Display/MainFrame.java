package Display;

import javax.swing.*;

public class MainFrame extends JFrame {

    private SimImage mainSimImage = new SimImage();

    public MainFrame(String title) {
        super(title);
        start();
    }
    public void start(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        buildGUI();
        pack();
        setVisible(true);
    }
    private void buildGUI(){
        JPanel masterPanel = new JPanel();
        masterPanel.add(mainSimImage);
        add(masterPanel);

    }
}
