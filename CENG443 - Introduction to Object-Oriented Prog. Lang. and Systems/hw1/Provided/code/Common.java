import java.util.Random;

public class Common {
    private static final String title = "Gold Wars";
    private static final int windowWidth = 1650;
    private static final int windowHeight = 1000;

    private static final GoldPrice goldPrice = new GoldPrice(588, 62);

    private static final Random randomGenerator = new Random(1234);
    private static final int upperLineY = 100;

    static  {
        // TODO: Here, you can initialize the fields you have declared
    }

    // getters
    public static String getTitle() { return title; }
    public static int getWindowWidth() { return windowWidth; }
    public static int getWindowHeight() { return windowHeight; }

    // getter
    public static GoldPrice getGoldPrice() { return goldPrice; }

    // getters
    public static Random getRandomGenerator() { return randomGenerator; }
    public static int getUpperLineY() { return upperLineY; }

    public static void stepAllEntities() {
        if (randomGenerator.nextInt(200) == 0) goldPrice.step();
        // TODO
    }
}