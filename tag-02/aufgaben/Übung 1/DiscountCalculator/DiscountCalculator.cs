public class DiscountCalculator
{
    public decimal CalculateDiscount(decimal purchasePrice)
    {
        if (purchasePrice < 15000m)
            return 0m;

        if (purchasePrice <= 20000m)
            return 5m;

        if (purchasePrice < 25000m)
            return 7m;

        return 8.5m;
    }
}
