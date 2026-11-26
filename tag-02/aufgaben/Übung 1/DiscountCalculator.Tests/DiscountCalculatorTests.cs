using Xunit;

public class DiscountCalculatorTests
{
    // Reihenfolge und IDs entsprechen den konkreten Testfällen in der README.
    public static TheoryData<decimal, decimal> BoundaryCases => new()
    {
        { 14999.99m, 0m }, // K01
        { 15000.00m, 5m }, // K02
        { 15000.01m, 5m }, // K03
        { 19999.99m, 5m }, // K04
        { 20000.00m, 5m }, // K05
        { 20000.01m, 7m }, // K06
        { 24999.99m, 7m }, // K07
        { 25000.00m, 8.5m }, // K08
        { 25000.01m, 8.5m } // K09
    };

    [Theory]
    [MemberData(nameof(BoundaryCases))]
    public void CalculateDiscount_Grenzwert_ErgibtErwartetenRabatt(
        decimal purchasePrice, decimal expectedDiscount)
    {
        // Arrange
        var calculator = new DiscountCalculator();

        // Act
        decimal actualDiscount = calculator.CalculateDiscount(purchasePrice);

        // Assert
        Assert.Equal(expectedDiscount, actualDiscount);
    }

    public static TheoryData<decimal, decimal> RepresentativeCases => new()
    {
        { 0m, 0m }, // K10
        { 10000m, 0m }, // K11
        { 17500m, 5m }, // K12
        { 22500m, 7m }, // K13
        { 30000m, 8.5m } // K14
    };

    [Theory]
    [MemberData(nameof(RepresentativeCases))]
    public void CalculateDiscount_RepraesentativerKaufpreis_ErgibtErwartetenRabatt(
        decimal purchasePrice, decimal expectedDiscount)
    {
        // Arrange
        var calculator = new DiscountCalculator();

        // Act
        decimal actualDiscount = calculator.CalculateDiscount(purchasePrice);

        // Assert
        Assert.Equal(expectedDiscount, actualDiscount);
    }
}
