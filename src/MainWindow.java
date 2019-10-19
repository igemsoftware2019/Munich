import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class MainWindow {
    public static void main(String[] args){
        JFrame mainWindow = MainWindow.createMainWindow();
    }

    public static JFrame createMainWindow(){
        MainWindow window = new MainWindow();
        JFrame mainWindow = new JFrame();
        mainWindow.setTitle("Silentcode");
        mainWindow.setSize(new Dimension(800, 800));
        JPanel content = new JPanel();
        content.setLayout(new GridBagLayout());


        JLabel inLabel = new JLabel("Please enter your sequence here (in frame):");

        JTextArea inField = new JTextArea("ATGTAA");
        inField.setLineWrap(true);
        JScrollPane scrollPaneINPUT = new JScrollPane (inField, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel inSect = new JPanel();
        inSect.setLayout(new BorderLayout());
        inSect.setBackground(new Color(140, 196, 242));
        inSect.setOpaque(true);
        inSect.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        inSect.add(inLabel, BorderLayout.NORTH);
        inSect.add(scrollPaneINPUT, BorderLayout.CENTER);


        JLabel outLabel = new JLabel("Results (You might need to scroll down a bit when finished):");

        JEditorPane outArea = new JEditorPane();
        outArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane (outArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel outSect = new JPanel();
        outSect.setLayout(new BorderLayout());
        outSect.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        outSect.add(outLabel, BorderLayout.NORTH);
        outSect.add(scrollPane, BorderLayout.CENTER);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridwidth = 2;
        c.gridheight = 1;

        c.gridx = 0;
        c.gridy = 2;
        content.add(inSect, c);

        c.gridy = 3;
        content.add(outSect, c);

        c.weighty = 0.1;
        c.gridy = 0;
        //JLabel img = new JLabel(new ImageIcon("./sup/SilentcodeLogo.png"));
        try {
            JLabel img = new JLabel(new ImageIcon(ImageIO.read(window.getClass().getResource("SilentcodeLogo.png"))));
            content.add(img, c);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        c.gridy = 1;
        JLabel subTitle = new JLabel("Calculate the silent mutation encoding potential of your sequence", SwingConstants.CENTER);
        content.add(subTitle, c);

        c.insets = new Insets(2,10,2,10);
        c.gridwidth = 1;
        JCheckBox vocalcheck = new JCheckBox("vocal", true);
        c.gridy = 4;
        content.add(vocalcheck, c);

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
        content.add(trigger, c);

        mainWindow.add(content);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setLocationRelativeTo(null);
        mainWindow.setVisible(true);
        return mainWindow;
    }
}
