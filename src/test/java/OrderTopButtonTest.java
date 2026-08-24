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

public class OrderTopButtonTest {
    private WebDriver driver;

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // каждый набор данных проверяется в каждом браузере
    static Stream<Arguments> browserAndOrderData() {
        return Stream.of("chrome", "firefox")
                .flatMap(browser -> Stream.of(
                        Arguments.of(browser, "Иван", "Петров", "ул. Ленина, д. 5", "89123456789",
                                "18.08.2026", "сутки", "чёрный жемчуг", "Приехать к 10:00"),
                        Arguments.of(browser, "Мария", "Иванова", "Невский пр., 10", "89221234567",
                                "20.08.2026", "двое суток", "серая безысходность", "Позвонить за час")
                ));
    }

    @ParameterizedTest(name = "{0}, Верхняя кнопка \"Заказать\": {1} {2}")
    @MethodSource("browserAndOrderData")
    void positiveOrderFlowTest(String browser, String firstName, String lastName, String address,
                                String phone, String date, String rentalPeriod, String color, String comment) {
        driver = DriverFactory.createDriver(browser);
        OrderPage orderPage = new OrderPage(driver);
        orderPage.open("https://qa-scooter.education-services.ru/");

        orderPage.clickTopOrderButton();
        String metroName = orderPage.fillCustomerForm(firstName, lastName, address, phone);
        System.out.println("Выбрана станция метро: " + metroName);
        orderPage.fillRentalForm(date, rentalPeriod, color, comment);
        orderPage.submitOrder();
        orderPage.confirmOrder();

        WebElement success = orderPage.waitForSuccessMessage();
        assertTrue(success.isDisplayed(), "Сообщение об успешном оформлении заказа не появилось!");
    }
}
