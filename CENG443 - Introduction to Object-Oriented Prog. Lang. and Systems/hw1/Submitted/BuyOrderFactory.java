public class BuyOrderFactory extends OrderFactory {
    // Function for Creating Buy Order instance for corresponding country
    @Override
    public Order createOrder(Country country) {
        return new BuyOrder(country, country.getPosition().getIntX(), country.getPosition().getIntY());
    }
}