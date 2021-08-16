import java.awt.*;

// Decorator class for Agent class
public abstract class AgentDecorator extends Agent {
    // Protected Agent instance for reaching purposes of subclasses
    protected Agent agent;

    /**
     * Constructor
     * @param x x position of agent Decorator
     * @param y y position of agent Decorator
     */
    public AgentDecorator(Agent agent, double x, double y) {
        super(x, y);
        this.agent = agent;
    }

    @Override
    public void draw(Graphics2D g2d){this.agent.draw(g2d);}

    @Override
    public void step() {
        this.agent.step();
        setPosition(this.agent.getPosition());
    }
}