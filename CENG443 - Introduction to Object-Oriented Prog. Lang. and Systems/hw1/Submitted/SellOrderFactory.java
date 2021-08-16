public class SellOrderFactory extends OrderFactory {
    // Function for Creating Sell Order instance for corresponding country
    @Override
    public Order createOrder(Country country) {
        return new SellOrder(country, country.getPosition().getIntX(), country.getPosition().getIntY());
    }
}