public abstract class OrderFactory {
    /**
     * Constructor
     */
    public OrderFactory() { ; }

    // abstract createOrder method for subclasses
    public abstract Order createOrder(Country country);
}