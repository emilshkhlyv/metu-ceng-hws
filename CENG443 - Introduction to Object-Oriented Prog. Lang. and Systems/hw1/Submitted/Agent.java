import java.awt.*;

public abstract class Agent extends Entity {
    /**
     * Instance fields of Agent Class
     */
    private State   agentState          = new Rest();
    private int     agentStolenMoney    = 0;
    private String  agentName           = "";
    private String  agentPhoto          = "";
    private Country country;

    // Methods which will be implemented in subclasses
    public abstract void step();
    public abstract void draw(Graphics2D g2d);

    /**
     * Constructor
     * @param x x position of Agent
     * @param y y position of Agent
     */
    public Agent(double x, double y) {
        super(x, y);
    }
    /**
     * Getter methods
     */
    public State getState() {
        return agentState;
    }
    public int getStolenMoney() {
        return agentStolenMoney;
    }
    public String getAgentName() {
        return agentName;
    }
    public String getAgentPhoto(){
        return agentPhoto;
    }
    public Country getCountry(){
        return country;
    }

    /**
     * Setter methods
     */
    public void setState(State state) { this.agentState = state; }
    public void setStolenMoney(int stolenMoney) { this.agentStolenMoney = stolenMoney; }
    public void setAgentName(String agentName) { this.agentName = agentName; }
    public void setAgentPhoto(String agentPhoto) { this.agentPhoto = agentPhoto; }
    public void setPosition(Position position) { this.position = position; }
    public void setAgentStolenMoney(int agentStolenMoney) { this.agentStolenMoney = agentStolenMoney; }
    public void setCountry(Country country) { this.country = country; }
}