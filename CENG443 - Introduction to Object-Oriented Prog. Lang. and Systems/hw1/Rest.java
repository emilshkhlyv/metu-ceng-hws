import java.awt.*;

public class Rest extends State {
    // Instance fields of Rest class
    private Agent agent = null;
    private Order order = null;

    /**
     * Constructor
     */
    public Rest() {
        super();
        setStateName("Rest");
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

    // if agent and order met, appropriate calculations will be done and order will be deleted
    @Override
    public void step(Agent agent) {
        this.agent = agent;
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