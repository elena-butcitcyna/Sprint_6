import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import pages.OrderPage;
import util.DriverFactory;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

// Тот же сценарий оформления заказа, что и в OrderTopButtonTest, но вход через нижнюю кнопку
// "Заказать" (после блока "Как это работает"), а не через верхнюю в шапке.
public class OrderBottomButtonTest {
    private WebDriver driver;

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    static Stream<Arguments> browserAndOrderData() {
        return Stream.of("chrome", "firefox")
                .flatMap(browser -> Stream.of(
                        Arguments.of(browser, "Иван", "Петров", "ул. Ленина, д. 5", "89123456789",
                                "18.08.2026", "сутки", "чёрный жемчуг", "Приехать к 10:00"),
                        Arguments.of(browser, "Мария", "Иванова", "Невский пр., 10", "89221234567",
                                "20.08.2026", "двое суток", "серая безысходность", "Позвонить за час")
                ));
    }

    @ParameterizedTest(name = "{0}, Нижняя кнопка \"Заказать\": {1} {2}")
    @MethodSource("browserAndOrderData")
    void positiveOrderFlowFromBottomButtonTest(String browser, String firstName, String lastName, String address,
                                                String phone, String date, String rentalPeriod, String color, String comment) {
        driver = DriverFactory.createDriver(browser);
        OrderPage orderPage = new OrderPage(driver);
        orderPage.open("https://qa-scooter.education-services.ru/");

        orderPage.clickBottomOrderButton();
        String metroName = orderPage.fillCustomerForm(firstName, lastName, address, phone);
        System.out.println("Выбрана станция метро: " + metroName);
        orderPage.fillRentalForm(date, rentalPeriod, color, comment);
        orderPage.submitOrder();
        orderPage.confirmOrder();

        WebElement success = orderPage.waitForSuccessMessage();
        assertTrue(success.isDisplayed(), "Сообщение об успешном оформлении заказа не появилось!");
    }
}
