import java.awt.*;

public class BuyOrder extends Order {
    /**
     * Constructor
     * @param country owner of order
     * @param x position
     * @param y position
     */
    public BuyOrder(Country country, double x, double y) {
        super(x, y);
        setOrderCountry(country);
        setBuyOrSell(false);
        setOrderColor(Color.green);
    }

    // Draw Order Class
    @Override
    public void draw(Graphics2D g2d) {
        this.setG2d(g2d);
        super.draw(g2d);
    }

    // If Buy Order is hit to upper Y line, if country has enough money, it can buy gold
    @Override
    public void ordering() {
        if (getHitPrice() != 0) {
            if (getAmount() * getHitPrice() <= getOrderCountry().getCash()) {
                getOrderCountry().setCash((int) (getOrderCountry().getCash() - getAmount() * getHitPrice()));
                getOrderCountry().setGold(getOrderCountry().getGold() + getAmount());
                getOrderCountry().calculateWorth();
            }
        }
    }

    // It calls ordering method
    @Override
    public void step() {
        super.step();
        ordering();
    }
}