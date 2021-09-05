import java.awt.*;

public class Novice extends AgentDecorator {
    /**
     * Constructor
     * @param agent Agent who has this decorator
     * @param x x position of agent
     * @param y x position of agent
     */
    public Novice(Agent agent, double x, double y) {
        super(agent, x, y);
    }

    // Drawing white badge top of Agent
    @Override
    public void draw(Graphics2D g2d) {
        super.draw(g2d);
        if(agent.getStolenMoney() > 2000){
            g2d.setColor(Color.white);
            g2d.fillRect(agent.getPosition().getIntX(), agent.getPosition().getIntY()-45, 15, 15);
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