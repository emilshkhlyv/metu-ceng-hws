import java.awt.*;

public class Master extends AgentDecorator {
    /**
     * Constructor
     * @param agent Agent who has this decorator
     * @param x x position of agent
     * @param y x position of agent
     */
    public Master(Agent agent, double x, double y) {
        super(agent, x, y);
    }

    // Drawing yellow badge top of Agent
    @Override
    public void draw(Graphics2D g2d) {
        super.draw(g2d);
        if(agent.getStolenMoney() > 4000){
            g2d.setColor(Color.yellow);
            g2d.fillRect(agent.getPosition().getIntX()+20, agent.getPosition().getIntY()-45, 15, 15);
            super.setStolenMoney(agent.getStolenMoney());
        }
        agent.draw(g2d);
    }

    // Draw Logo of Agent itself
    @Override
    public void step() {
        super.step();
    }
}