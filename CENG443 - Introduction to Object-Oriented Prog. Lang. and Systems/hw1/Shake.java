import java.awt.*;
import java.util.Random;

public class Shake extends State {
    // Instance fields for Shake class
    private Agent agent = null;
    private Order order = null;
    private Position initialPosition = null;

    /**
     * Constructor
     */
    public Shake(){
        super();
        setStateName("Shake");
    }

    // random integer generator
    int randomNum(int left){
        Random random = new Random();
        return random.nextInt(2 -left) + left;
    }

    // Find position of closest order to agent
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

    // Shake agent and if agent and order met appropriate calculations will be done and order will be deleted
    @Override
    public void step(Agent agent) {
        this.agent = agent;
        if (this.initialPosition == null) {
            this.initialPosition = agent.getPosition();
        }
        int Xoffset = randomNum(1), Yoffset = randomNum(1);
        Position position = null;
        position = getPosition(Xoffset, Yoffset, position);
        agent.setPosition(position);

        /*if(findPositionOfClosestOrder() != null){
            if(agent.getPosition().distanceTo(order.getPosition().getIntX(), order.getPosition().getIntY()) <= 40) {
                order.getOrderCountry().setCash((int) (order.getOrderCountry().getCash() - order.getAmount()* Common.getGoldPrice().getCurrentPrice()));
                agent.getCountry().setCash((int) (agent.getCountry().getCash() + order.getAmount() * Common.getGoldPrice().getCurrentPrice()));
                agent.setAgentStolenMoney((int) (agent.getStolenMoney() + order.getAmount() * Common.getGoldPrice().getCurrentPrice()));
                Common.getOrders().remove(order);
                Common.getDeleteList().add(order);
            }
        }*/
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

    // Calculate position for shaking
    private Position getPosition(int xoffset, int yoffset, Position position) {
        switch (randomNum(0)) {
            case 0:
                position = new Position(initialPosition.getIntX() + xoffset, initialPosition.getIntY() + yoffset);
                break;
            case 1:
                position = new Position(initialPosition.getIntX() - xoffset, initialPosition.getIntY() - yoffset);
                break;
        }
        return position;
    }

    @Override
    public void draw(int X, int Y, Graphics2D g2d) {
    }
}