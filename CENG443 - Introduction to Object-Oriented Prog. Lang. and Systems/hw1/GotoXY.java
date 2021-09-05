import java.awt.*;
import java.util.Random;

public class GotoXY extends State {
    // Instance fields for GotoXY class
    private Agent agent = null;
    private Order order = null;
    private Position positionDestination = null;
    private double speed = -1.0;

    /**
     * Constructor
     */
    public GotoXY() {
        super();
        setStateName("GotoXY");
    }

    // random integer generator
    private int  randomNum(int left, int right){
        Random random = new Random();
        return random.nextInt(right-left) + left;
    }

    // Find closest object to agent
    private Order findPositionOfClosestOrder() {
        double closestDist = 10000000.0;
        for(int i = 0; i < Common.getOrders().size(); ++i) {
            double dist = Common.getOrders().get(i).getPosition().distanceTo(agent.getPosition().getX(), agent.getPosition().getY());
            if(dist < closestDist) {
                closestDist = dist;
                order = Common.getOrders().get(i);
            }
        }
        return order;
    }

    // Find random position and if order and agent met order will be deleted
    @Override
    public void step(Agent agent) {
        this.agent = agent;
        if (positionDestination == null || positionDestination.distanceTo(agent.getPosition().getX(), agent.getPosition().getY()) <= 0.01) {
            int destX = randomNum(0, Common.getWindowWidth()-60);
            int destY = randomNum(Common.getUpperLineY(), Common.getCountries().get(0).getPosition().getIntY()-50);
            positionDestination = new Position(destX, destY);
        }
        if (speed == -1) {
            speed = randomNum(1, 2);
        }

        Position position = agent.getPosition();
        double newX = position.getX() + (positionDestination.getX()-position.getX()) / 500.0 * speed;
        double newY = position.getY() + (positionDestination.getY()-position.getY()) / 500.0 * speed;
        position.setX(newX);
        position.setY(newY);
        agent.setPosition(position);

        if(findPositionOfClosestOrder() != null){
            if(findPositionOfClosestOrder().getOrderCountry() != agent.getCountry()) {
                if (agent.getPosition().distanceTo(order.getPosition().getIntX(), order.getPosition().getIntY()) <= 30) {
                    if(!order.isBuyOrSell() && order.getOrderCountry().getCash() - order.getAmount()*Common.getGoldPrice().getCurrentPrice() >= 0){
                        agent.setAgentStolenMoney((int) (agent.getStolenMoney() + Common.getGoldPrice().getCurrentPrice() * order.getAmount()));
                        order.getOrderCountry().setCash((int) (order.getOrderCountry().getCash() - order.getAmount() * Common.getGoldPrice().getCurrentPrice()));
                        agent.getCountry().setCash((int) (agent.getCountry().getCash() + order.getAmount() * Common.getGoldPrice().getCurrentPrice()));
                    }
                    else if(order.isBuyOrSell() && order.getOrderCountry().getGold() - order.getAmount() >= 0){
                        agent.setAgentStolenMoney((int) (agent.getStolenMoney() + Common.getGoldPrice().getCurrentPrice() * order.getAmount()));
                        order.getOrderCountry().setGold(order.getOrderCountry().getGold() - order.getAmount());
                        agent.getCountry().setGold(agent.getCountry().getGold() + order.getAmount());
                    }
                    agent.getCountry().calculateWorth();
                    order.getOrderCountry().calculateWorth();
                    Common.getOrders().remove(order);
                }
            }
        }
    }

    @Override
    public void draw(int X, int Y, Graphics2D g2d) { }
}