import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

public class MainWindow {
    public static void main(String[] args){
        JFrame mainWindow = MainWindow.createMainWindow();
    }

    public static JFrame createMainWindow(){
        JFrame mainWindow = new JFrame();
        mainWindow.setTitle("Silent Mutation Calculator");
        mainWindow.setSize(new Dimension(800, 800));
        mainWindow.setLayout(new GridBagLayout());


        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridwidth = 2;
        c.gridheight = 1;

        c.gridx = 0;
        c.gridy = 1;

        JTextArea inField = new JTextArea("ATGTAA");
        inField.setLineWrap(true);
        JScrollPane scrollPaneINPUT = new JScrollPane (inField, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainWindow.add(scrollPaneINPUT, c);


        c.gridy = 3;
        JEditorPane outArea = new JEditorPane();
        outArea.setEditable(false);
        //outArea.setContentType("text/plain");
        JScrollPane scrollPane = new JScrollPane (outArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        /*scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                e.getAdjustable().setValue(e.getAdjustable().getMaximum());
            }
        });*/


        mainWindow.add(scrollPane, c);

        c.weighty = 0.1;
        c.gridy = 0;
        JLabel inLabel = new JLabel("Please enter your sequence here:");
        mainWindow.add(inLabel, c);

        c.gridy = 2;
        JLabel outLabel = new JLabel("Results (You might need to scroll down a bit when finished):");
        mainWindow.add(outLabel, c);

        c.gridwidth = 1;
        JCheckBox vocalcheck = new JCheckBox("vocal", true);
        c.gridy = 4;
        mainWindow.add(vocalcheck, c);

        JButton trigger = new JButton("Launch");
        trigger.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                new Thread( new Runnable() {
                    @Override
                    public void run() {
                        outArea.setText("");
                        CalculatorMain c = new CalculatorMain(inField.getText(), new BufferedWriter(new OutputStreamWriter(new TextAreaOutputStream(outArea, scrollPane))), vocalcheck.isSelected());
                        c.run();
                    }
                }).start();
            }
        });
        trigger.setBackground(Color.RED);
        c.gridx = 1;
        mainWindow.add(trigger, c);

        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setLocationRelativeTo(null);
        mainWindow.setVisible(true);
        return mainWindow;
    }
}
