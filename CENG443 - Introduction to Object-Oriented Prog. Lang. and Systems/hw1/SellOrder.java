import java.awt.*;

public class SellOrder extends Order {
    /**
     * Constructor
     * @param country Order Country
     * @param x x position of SellOrder
     * @param y y position of SellOrder
     */
    public SellOrder(Country country, double x, double y) {
        super(x, y);
        setOrderColor(Color.pink);
        setBuyOrSell(true);
        setOrderCountry(country);
    }

    // draw Order
    @Override
    public void draw(Graphics2D g2d) {
        this.setG2d(g2d);
        super.draw(g2d);
    }

    // if Sell order hit to UpperLineY, so appropriate calculations will be done for corresponding Country
    @Override
    public void ordering() {
        if(getHitPrice() != 0){
            if(getAmount() <= getOrderCountry().getGold()){
                getOrderCountry().setCash((int) (getOrderCountry().getCash() + getAmount() * getHitPrice()));
                getOrderCountry().setGold(getOrderCountry().getGold() - getAmount());
                getOrderCountry().calculateWorth();
            }
        }
    }

    // call ordering and do Superclass step()
    @Override
    public void step() {
        super.step();
        ordering();
    }
}