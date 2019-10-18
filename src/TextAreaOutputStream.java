import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import javax.swing.*;
import javax.swing.text.*;


public class TextAreaOutputStream  extends OutputStream{
    private JEditorPane myTextArea;
    JScrollPane pane;
    Color c = Color.RED;
    static boolean readingColor = false;
    static String colorVal = "";

    public TextAreaOutputStream(JEditorPane myTextArea, JScrollPane pane) {
        this.myTextArea = myTextArea;
        this.pane = pane;
    }

    public void write(int b) throws IOException {
        char mychar = (char) b;
        if(b != 27 && !readingColor) {
            StyleContext sc = StyleContext.getDefaultStyleContext();
            //System.out.println("real color" + c.toString());
            AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
            //System.out.println(aset.toString());
            int len = myTextArea.getDocument().getLength();
            try {
                myTextArea.getDocument().insertString(len, String.valueOf(mychar), aset);
                pane.getVerticalScrollBar().setValue(pane.getVerticalScrollBar().getMaximum());
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }else{
            if(b == 27) {
                readingColor = true;
                colorVal = "";
            }
            colorVal += mychar;
            if(b == 109){
                readingColor = false;
                //System.out.println("" + colorVal + "Color" + colorVal.length() + " " + colorVal.charAt(1) + " " + colorVal.charAt(2) + " " + colorVal.charAt(3) + "\033[0m");
                if(colorVal.equals("\033[0m")){
                    c=Color.BLACK;
                } else if(colorVal.equals("\033[31m")){
                    c=Color.RED;
                    //System.out.println("Print red");
                } else if(colorVal.equals("\033[32m")){
                    c=Color.green;
                    //System.out.println("Print yellow");
                } else if(colorVal.equals("\033[33m")){
                    c=Color.YELLOW;
                    //System.out.println("Print green");
                }
            }
        }
    }
}
