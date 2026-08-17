import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import pages.OrderPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

// Тот же сценарий оформления заказа, что и в OrderTopButtonTest, но вход через нижнюю кнопку
// "Заказать" (после блока "Как это работает"), а не через верхнюю в шапке.
public class OrderBottomButtonTest {
    private WebDriver driver;
    private OrderPage orderPage;

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        orderPage = new OrderPage(driver);
        orderPage.open("https://qa-scooter.education-services.ru/");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @ParameterizedTest(name = "Нижняя кнопка \"Заказать\": {0} {1}")
    // delimiterString — см. комментарий в OrderTopButtonTest: адрес содержит запятую,
    // со стандартным разделителем-запятой CsvSource молча сдвигал все параметры.
    @CsvSource(delimiterString = ";", value = {
            // Имя; Фамилия; Адрес; Телефон; Дата; Срок аренды; Цвет; Комментарий
            "Иван; Петров; ул. Ленина, д. 5; 89123456789; 18.08.2026; сутки; чёрный жемчуг; Приехать к 10:00",
            "Мария; Иванова; Невский пр., 10; 89221234567; 20.08.2026; двое суток; серая безысходность; Позвонить за час"
    })
    void positiveOrderFlowFromBottomButtonTest(String firstName, String lastName, String address,
                                                String phone, String date,
                                                String rentalPeriod, String color, String comment) {
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
