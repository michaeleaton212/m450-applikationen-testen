using Xunit;

public class FehlerTests
{
    private const double BasePrice = 100;
    private const double SpecialPrice = 50;
    private const double ExtraPrice = 5;

    [Fact]
    public void CalculatePrice_KeinRabatt_ErgibtDenVollenPreis()
    {
        // Arrange
        int extras = 0;
        double discount = 0;

        // Act
        double actual = Fehler.CalculatePrice(BasePrice, SpecialPrice, ExtraPrice, extras, discount);

        // Assert
        Assert.Equal(155.00, actual, precision: 2);
    }

    [Fact]
    public void CalculatePrice_NormalerRabatt_GiltFuerGrundpreisUndExtras()
    {
        // Arrange
        int extras = 0;
        double discount = 2;

        // Act
        double actual = Fehler.CalculatePrice(BasePrice, SpecialPrice, ExtraPrice, extras, discount);

        // Assert
        Assert.Equal(152.90, actual, precision: 2);
    }

    [Fact]
    public void CalculatePrice_DreiExtras_ErhaltenZehnProzentRabatt()
    {
        // Arrange
        int extras = 3;
        double discount = 0;

        // Act
        double actual = Fehler.CalculatePrice(BasePrice, SpecialPrice, ExtraPrice, extras, discount);

        // Assert
        Assert.Equal(154.50, actual, precision: 2);
    }

    [Fact]
    public void CalculatePrice_FuenfExtras_ErhaltenFuenfzehnProzentRabatt()
    {
        // Arrange
        int extras = 5;
        double discount = 0;

        // Act
        double actual = Fehler.CalculatePrice(BasePrice, SpecialPrice, ExtraPrice, extras, discount);

        // Assert
        Assert.Equal(154.25, actual, precision: 2);
    }

    [Fact]
    public void CalculatePrice_HoehererRabatt_HatVorrangBeimExtrapreis()
    {
        // Arrange
        int extras = 5;
        double discount = 20;

        // Act
        double actual = Fehler.CalculatePrice(BasePrice, SpecialPrice, ExtraPrice, extras, discount);

        // Assert
        Assert.Equal(134.00, actual, precision: 2);
    }
}
