import java.awt.*;
import java.util.Random;

public class ChaseClosest extends State {
    /**
     * Instance fields of State
     */
    private Agent agent = null;
    private Order order = null;
    private double speed = -1.0;

    /**
     * Constructor
     */
    public ChaseClosest() {
        super();
        setStateName("ChaseClosest");
    }

    // Find Position of Closest Order for pursuing
    private Position findPositionOfClosestOrder() {
        double closestDist = 10000000.0;
        Position bestPosition = null;
        for(int i = 0; i < Common.getOrders().size(); ++i) {
            double dist = Common.getOrders().get(i).getPosition().distanceTo(agent.getPosition().getX(), agent.getPosition().getY());
            if(dist < closestDist) {
                bestPosition = Common.getOrders().get(i).getPosition();
                closestDist = dist;
                order = Common.getOrders().get(i);
            }
        }
        return bestPosition;
    }

    // Random integer generator
    private int  randomNum(int left, int right){
        Random random = new Random();
        return random.nextInt(right-left) + left;
    }

    // In this overridden step function if do corresponding order and agent meet calculations
    // and delete order and calculate the new position of agent
    @Override
    public void step(Agent agent) {
        this.agent = agent;
        Position currentPositionToGo = findPositionOfClosestOrder();

        if(speed == -1) {
            speed = randomNum(1, 2);
        }

        if(findPositionOfClosestOrder() != null){
            if(order.getOrderCountry() != agent.getCountry()) {
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
        Position position = agent.getPosition();
        double newX = position.getX() + (currentPositionToGo.getX()-position.getX()) / 100.0 * speed;
        double newY = position.getY() + (currentPositionToGo.getY()-position.getY()) / 100.0 * speed;
        position.setX(newX);
        position.setY(newY);
        agent.setPosition(position);
    }

    @Override
    public void draw(int X, int Y, Graphics2D g2d){}
}