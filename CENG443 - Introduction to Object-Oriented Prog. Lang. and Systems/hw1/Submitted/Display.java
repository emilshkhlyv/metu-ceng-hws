import javax.swing.*;
import java.awt.*;

public class Display extends JPanel {
    public Display() {
        this.setBackground(new Color(180, 180, 180));
    }

    @Override
    public Dimension getPreferredSize() {
        return super.getPreferredSize();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Common.getGoldPrice().draw((Graphics2D) g);
        g.drawLine(0, Common.getUpperLineY(), Common.getWindowWidth(), Common.getUpperLineY());

        for(int i = 0; i < Common.getCountries().size(); ++i)   Common.getCountries().get(i).draw((Graphics2D) g);
        for(int i = 0; i < Common.getAgencies().size(); ++i)    Common.getAgencies().get(i).draw((Graphics2D) g);
        for(int i = 0; i < Common.getOrders().size(); ++i)      Common.getOrders().get(i).draw((Graphics2D) g);
    }
}