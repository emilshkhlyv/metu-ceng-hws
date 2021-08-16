import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class BasicAgent extends Agent {
    private Graphics2D g2d;
    private BufferedImage bufferedImage = null;
    private int totalSteps = 0;

    /**
     * Constructor
     * @param agentName Name of Agent
     * @param agentPhoto Photo of Agent
     * @param x x position of Agent
     * @param y y position of Agent
     */
    public BasicAgent(String agentName, String agentPhoto, double x, double y) {
        super(x, y);
        setAgentPhoto(agentPhoto);
        loadAgentPhoto();
        setAgentName(agentName);
        setStolenMoney(0);
    }

    // Load Agent Logo from Directory
    private void loadAgentPhoto() {
        try {
            bufferedImage = ImageIO.read(new File(getAgentPhoto()));
            bufferedImage = scaledImage(bufferedImage, 60, 60);
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
        }
    }

    // Scale Logo for GUI
    public BufferedImage scaledImage(BufferedImage image, int IAw, int IAh) {
        BufferedImage scaledImage = new BufferedImage(IAw, IAh, BufferedImage.TRANSLUCENT);
        Graphics2D g2D = scaledImage.createGraphics();
        g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2D.drawImage(image, 0, 0, IAw, IAh, null);
        g2D.dispose();
        return scaledImage;
    }

    // Draw Logo of Agent to GUI
    private void drawImage(){
        if (bufferedImage != null) {
            g2d.drawImage(bufferedImage, getPosition().getIntX(), getPosition().getIntY(), null);
        }
    }

    // Write Texts about Agent like name, stolen money amount and State
    private void drawText(Graphics2D g2d){
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("default", Font.BOLD, 18));

        int namePlaceX = getPosition().getIntX(), namePlaceY = getPosition().getIntY()-10;
        g2d.drawString(getAgentName(), namePlaceX,  namePlaceY);

        int photoHeight = 60;
        int yOffset = 15;
        int statePlaceY = namePlaceY + photoHeight + 2* yOffset;

        g2d.setColor(Color.blue);
        g2d.drawString(String.valueOf(getState().getStateName()), namePlaceX, statePlaceY);

        int smPlaceY = statePlaceY + 3* yOffset /2;
        g2d.setColor(Color.red);
        g2d.drawString(String.valueOf(getStolenMoney()), namePlaceX, smPlaceY);
    }

    // Draw Logo of Agent and write necessary information to GUI
    @Override
    public void draw(Graphics2D g2d) {
        this.g2d = g2d;
        drawImage();
        drawText(g2d);
    }

    // Call Update State Type and do State's step methods
    @Override
    public void step() {
        updateStateType();
        getState().step(this);
    }

    // Updates State of Agent randomly per 200 steps
    private void updateStateType() {
        ++totalSteps;
        if (totalSteps % 500 == 0) {
            int newStateId = Common.getRandomGenerator().nextInt(4);
            switch (newStateId) {
                case 0:
                    setState(new Rest());
                    totalSteps = 0;
                    break;
                case 1:
                    setState(new Shake());
                    totalSteps = 0;
                    break;
                case 2:
                    setState(new GotoXY());
                    totalSteps = 0;
                    break;
                case 3:
                    setState(new ChaseClosest());
                    totalSteps = 0;
                    break;
                default:
                    break;
            }
        }
    }
}