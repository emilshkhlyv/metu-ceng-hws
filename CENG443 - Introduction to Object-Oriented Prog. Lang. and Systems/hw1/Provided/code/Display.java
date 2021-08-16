import javax.swing.*;
import java.awt.*;

public class Display extends JPanel {
    public Display() { this.setBackground(new Color(180, 180, 180)); }

    @Override
    public Dimension getPreferredSize() { return super.getPreferredSize(); }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Common.getGoldPrice().draw((Graphics2D) g);
        g.drawLine(0, Common.getUpperLineY(), Common.getWindowWidth(), Common.getUpperLineY());
        // TODO
    }
}