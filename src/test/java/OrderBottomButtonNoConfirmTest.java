import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.WebDriver;
import pages.OrderPage;
import util.DriverFactory;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Негативный сценарий: вход через нижнюю кнопку "Заказать", в модалке подтверждения
// нажимается "Нет" — заказ не должен оформляться, форма должна остаться открытой.
public class OrderBottomButtonNoConfirmTest {
    private WebDriver driver;

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"chrome", "firefox"})
    void clickingNoDoesNotCreateOrderTest(String browser) {
        driver = DriverFactory.createDriver(browser);
        OrderPage orderPage = new OrderPage(driver);
        orderPage.open("https://qa-scooter.education-services.ru/");

        orderPage.clickBottomOrderButton();
        orderPage.fillCustomerForm("Иван", "Петров", "ул. Ленина, д. 5", "89123456789");
        orderPage.fillRentalForm("18.08.2026", "сутки", "серая безысходность", "Позвонить за час");
        orderPage.submitOrder();
        orderPage.declineOrder();

        assertFalse(orderPage.isSuccessMessageDisplayed(),
                "Заказ не должен считаться оформленным после отказа во всплывающем окне!");
        assertTrue(orderPage.isSubmitButtonDisplayed(),
                "После отказа от заказа форма должна оставаться открытой для редактирования!");
    }
}
