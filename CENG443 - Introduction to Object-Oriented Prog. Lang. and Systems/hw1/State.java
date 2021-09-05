import java.awt.*;

public abstract class State {
    private String stateName;
    private Common common = Common.getCommon();

    /**
     * Constructor
     */
    public State(){;}

    // abstract methods for subclasses
    public abstract void step(Agent agent);
    public abstract void draw(int X, int Y, Graphics2D g2d);

    /**
     * Setter methods
     */
    public void setStateName(String stateName)  { this.stateName = stateName; }
    public void setCommon(Common common)        { this.common = common; }

    /**
     * Getter methods
     */
    public String getStateName()    { return stateName; }
    public Common getCommon()       { return common; }
}