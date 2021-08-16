import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Country extends Entity{
    /**
     * Instance fields of Country class
     */
    private String      initOfCountry = "";
    private String      name    = "";
    private String      photo   = "";
    private double      worth;
    private int         cash = 10000;
    private int         gold = 50;
    private Graphics2D  g2d;
    BufferedImage       bufferedImage = null;
    private Common      common = Common.getCommon();
    private int         again = 0;

    /**
     * Getter methods
     */
    public String getInitOfCountry() {
        return initOfCountry;
    }
    public double getWorth() {
        return worth;
    }
    public int getCash() {
        return cash;
    }
    public int getGold() {
        return gold;
    }
    public Common getCommon() {
        return common;
    }

    /**
     * Setter methods
     */
    public void setInitOfCountry(String initOfCountry) {
        this.initOfCountry = initOfCountry;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setPhoto(String photo) {
        this.photo = photo;
    }
    public void setWorth(double worth) {
        this.worth = worth;
    }
    public void setCash(int cash) {
        this.cash = cash;
    }
    public void setGold(int gold) {
        this.gold = gold;
    }
    public void setG2d(Graphics2D g2d) {
        this.g2d = g2d;
    }
    public void setCommon(Common common) {
        this.common = common;
    }

    /**
     * Constructor
     * @param x X position of Country
     * @param y Y position of Country
     */
    public Country(double x, double y) {
        super(x, y);
    }

    // Loading image from Directory
    public void loadImage() {
        try {
            bufferedImage = ImageIO.read(new File(photo));
            bufferedImage = scaledImage(bufferedImage,140, 80);
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
        }
    }

    // Scale Photo of Country for GUI
    public BufferedImage scaledImage (BufferedImage image, int imageW, int imageH) {
        BufferedImage scaledImage = new BufferedImage(imageW, imageH, BufferedImage.TRANSLUCENT);
        Graphics2D g2D = scaledImage.createGraphics();
        g2D.drawImage(image, 0, 0, imageW, imageH, null);
        g2D.dispose();
        return scaledImage;
    }

    // Draw Photo of Country to GUI
    private void drawImage (int X, int Y) {
            g2d.drawImage(bufferedImage, X, Y, null);
    }

    // Write necessary information about country as name of country, how much gold, cash, worth it has
    private void writeText (){
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Verdana", Font.BOLD, 25));
        g2d.drawString(name, getPosition().getIntX(),getPosition().getIntY()+105);
        g2d.setColor(Color.YELLOW);
        g2d.drawString(gold + " gold",getPosition().getIntX(),getPosition().getIntY()+135);
        g2d.setColor(Color.GREEN);
        g2d.drawString(cash + " cash",getPosition().getIntX(),getPosition().getIntY()+165);
        g2d.setColor(Color.BLUE);
        g2d.drawString("Worth: " + (int) (cash + gold * Common.getGoldPrice().getCurrentPrice()),getPosition().getIntX(),getPosition().getIntY()+195);
    }

    // Draw Image
    @Override
    public void draw(Graphics2D g2d) {
        this.g2d=g2d;
        int X = getPosition().getIntX(), Y = getPosition().getIntY();
        drawImage(X, Y);
        writeText();
        creator();
    }

    // Creating Orders
    public Order orderCreation() {
        int random = randomNum();
        OrderFactory orderFactory;
        switch (random){
            case 0:
                orderFactory = new BuyOrderFactory();
                break;
            case 1:
                orderFactory = new SellOrderFactory();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + random);
        }
        return orderFactory.createOrder(this);
    }

    // random number generator
    int randomNum(){
        Random random = new Random();
        return random.nextInt(2);
    }

    // Creating multiple orders
    public void creator(){
        for(int i = Common.getOrderOfCountry(this); i < 3 && again % 100 == 0; ++i){
            Order order = orderCreation();
            order.getPosition().setX(this.getPosition().getIntX() + 50);
            order.getPosition().setY(this.getPosition().getIntY() - 10);
            Common.getOrders().add(order);
            again = 0;
        }
    }

    // Calculate Worth Dynamically
    public void calculateWorth(){
        this.worth = this.getGold() * Common.getGoldPrice().getCurrentPrice() + getCash();
    }

    // call creator method and increase again for counting how many steps are passed
    @Override
    public void step() {
        creator();
        calculateWorth();
        ++again;
    }
}