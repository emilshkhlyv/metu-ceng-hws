import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Common {
    private static final String title = "Gold Wars";

    /**
     * I changed windowWidth and windowHeight for fitting to screen of my computer
     */
    private static final int windowWidth = 1370;
    private static final int windowHeight = 715;

    private static final GoldPrice  goldPrice = new GoldPrice(420, 50);

    private static final Random randomGenerator = new Random();
    private static final int upperLineY      = 65;

    private static final List<Country> countries;
    private static final List<Agent> agencies;
    private static final List<Order> orders;
    private static final List<Order> deleteList = new ArrayList<>();
    private static Common  common = null;
    static  {
        // Names of Countries
        List<String> countryNames = new ArrayList<>();
        countryNames.add("USA");
        countryNames.add("Israel");
        countryNames.add("Turkey");
        countryNames.add("Russia");
        countryNames.add("China");

        // Photos of Countries
        List<String> countryPhotos = new ArrayList<>();
        countryPhotos.add("images/usa.jpg");
        countryPhotos.add("images/israel.jpg");
        countryPhotos.add("images/turkey.jpg");
        countryPhotos.add("images/russia.jpg");
        countryPhotos.add("images/china.jpg");

        // Initials of Countries
        List<String> initsOfCountries = new ArrayList<>();
        initsOfCountries.add("US");
        initsOfCountries.add("IL");
        initsOfCountries.add("TR");
        initsOfCountries.add("RU");
        initsOfCountries.add("CN");

        // Initialization of Countries
        countries   = new ArrayList<>();
        double X = 100, Y = 500, pW = 100;
        int i = 0;
        for(String s: countryNames){
            Country country = new Country(X, Y);
            country.setName(s);
            country.setWorth(10000+50*(getGoldPrice().getCurrentPrice()));
            country.setPhoto(countryPhotos.get(i));
            country.setInitOfCountry(initsOfCountries.get(i));
            country.loadImage();
            countries.add(country);
            X += pW+156;
            ++i;
        }

        // Names of Agencies
        List<String> agentNames = new ArrayList<>();
        agentNames.add("CIA");
        agentNames.add("MOSSAD");
        agentNames.add("MIT");
        agentNames.add("SVR");
        agentNames.add("MSS");

        // Logos of Agent
        List<String> agentLogos = new ArrayList<>();
        agentLogos.add("images/cia.png");
        agentLogos.add("images/mossad.png");
        agentLogos.add("images/mit.png");
        agentLogos.add("images/svr.png");
        agentLogos.add("images/mss.png");

        // Initialization of Agents
        agencies    = new ArrayList<>();
        i = 0;
        int oX = 260, pX = 100, pY = 200;
        for(String name: agentNames){
            Agent basicAgent = new BasicAgent(name, agentLogos.get(i), pX, pY);
            // Initialization of decorator pattern classes
            Agent agent = new Expert(new Master(new Novice(basicAgent, basicAgent.getPosition().getIntX(), basicAgent.getPosition().getIntY()), basicAgent.getPosition().getIntX(), basicAgent.getPosition().getIntY()), basicAgent.getPosition().getIntX(), basicAgent.getPosition().getIntY());
            basicAgent.setCountry(countries.get(i));
            pX += oX;
            agencies.add(agent);
            ++i;
        }
        orders = new ArrayList<>();
    }

    // if Order hit by UpperLineY or agent, it will be deleted
    private static void deleteOrders(){
        for(Order order : orders){
            if(order.getHitPrice() != 0){
                deleteList.add(order);
            }
        }
        for(Order order: deleteList){
            orders.remove(order);
        }
    }

    /**
     * Getter methods
     */
    public static String        getTitle()              { return title; }
    public static int           getWindowWidth()        { return windowWidth; }
    public static int           getWindowHeight()       { return windowHeight; }
    public static GoldPrice     getGoldPrice()          { return goldPrice; }
    public static List<Order>   getDeleteList() {
        return deleteList;
    }
    public static Random        getRandomGenerator()    { return randomGenerator; }
    public static int           getUpperLineY()         { return upperLineY; }
    public static List<Country> getCountries()          { return countries; }
    public static List<Agent>   getAgencies()           { return agencies; }
    public static List<Order>   getOrders()             { return orders; }
    public static Common        getCommon()             {
        if(common == null) {
            common = new Common();
        }
        return common;
    }
    public static int           getOrderOfCountry(Country country){
        int j = 0;
        for (Order order : orders) {
            if (order.getOrderCountry() == country) {
                ++j;
            }
        }
        return j;
    }
    public static void          stepAllEntities()       {
        if (randomGenerator.nextInt(200) == 0) goldPrice.step();
        for (Country country : countries) {
            country.step();
        }
        for (Agent agent: agencies){
            agent.step();
        }
        deleteOrders();
        for(Order order: orders){
            order.step();
        }
    }
}