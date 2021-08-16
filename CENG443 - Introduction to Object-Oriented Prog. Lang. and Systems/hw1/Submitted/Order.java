import java.awt.*;
import java.util.Random;

public abstract class Order extends Entity {
    // Instance fields for Order class
    private Country         orderCountry        = null;
    private Color           orderColor          = null;
    private final int       orderSpeed          = randomNum(1, 2);
    private final int       amount              = randomNum(1, 6);
    private final Common    common              = Common.getCommon();
    private final Position  finalPosition       = Destination();
    private double          hitPrice = 0;
    private Graphics2D      g2d;
    private boolean         BuyOrSell;


    /**
     * Constructor
     * @param x position
     * @param y position
     */
    public Order(double x, double y) {
        super(x, y);
    }

    // select random final position for order
    private Position Destination(){
        Position destiny = new Position(0, 0);
        destiny.setX(randomNum(0, Common.getWindowWidth()));
        destiny.setY(Common.getUpperLineY());
        return destiny;
    }

    // abstract ordering method for subclasses
    public abstract void ordering();

    // move order and check UpperLineY hit
    @Override
    public  void step() {
        move();
        upperLineY();
    }

    // draw order to GUI
    @Override
    public  void draw(Graphics2D g2d) {
        this.g2d = g2d;
        drawer(g2d);
    }

    // Draw order and color of order and write necessary information as amount of order, initial of country
    public  void drawer(Graphics2D g2d){
        g2d.setColor(orderColor);
        g2d.fillOval(getPosition().getIntX(), getPosition().getIntY(), 20, 20);
        g2d.setFont(new Font("default", Font.BOLD, 14));
        g2d.drawString(orderCountry.getInitOfCountry(), getPosition().getIntX()+3, getPosition().getIntY()-6);
        g2d.setColor(Color.black);
        g2d.drawString(String.valueOf(amount), getPosition().getIntX()+4, getPosition().getIntY()+12);
    }

    // If order reached to upperLineY assign current gold price to hitPrice
    private void upperLineY(){
        if(getPosition().getIntY() <= Common.getUpperLineY()){
            hitPrice    = Common.getGoldPrice().getCurrentPrice();
        }
    }

    // set new position values of order
    private void move(){
        int speedHorizontal = ((Math.abs(getPosition().getIntX() - finalPosition.getIntX()) / orderSpeed) == 0) ? Math.abs(finalPosition.getIntX() - getPosition().getIntX()) : orderSpeed;
        int speedVertical   = ((Math.abs(getPosition().getIntY() - finalPosition.getIntY()) / orderSpeed) == 0) ? Math.abs(finalPosition.getIntY() - getPosition().getIntY()) : orderSpeed;
        int positX = (getPosition().getIntX() > finalPosition.getIntX()) ? getPosition().getIntX() - speedHorizontal : getPosition().getIntX() + speedHorizontal;
        int positY = (getPosition().getIntY() > finalPosition.getIntY()) ? getPosition().getIntY() - speedVertical   : getPosition().getIntY();
        getPosition().setX(positX);
        getPosition().setY(positY);
    }

    // Random integer generator
    private int  randomNum(int left, int right){
        Random random = new Random();
        return random.nextInt(right-left) + left;
    }

    /**
     * Getter methods
     */
    public int      getAmount() {
        return amount;
    }
    public double   getHitPrice() {
        return hitPrice;
    }
    public Common   getCommon() {
        return common;
    }
    public Country  getOrderCountry() {
        return orderCountry;
    }
    public Graphics2D getG2d() {
        return g2d;
    }

    public boolean isBuyOrSell() {
        return BuyOrSell;
    }

    /**
     * Setter methods
     */
    public void     setOrderCountry(Country orderCountry) {
        this.orderCountry = orderCountry;
    }
    public void     setOrderColor(Color orderColor) {
        this.orderColor = orderColor;
    }
    public void     setG2d(Graphics2D g2d) {
        this.g2d = g2d;
    }

    public void setBuyOrSell(boolean buyOrSell) {
        BuyOrSell = buyOrSell;
    }
}