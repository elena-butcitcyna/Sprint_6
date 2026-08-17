import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import pages.OrderPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderTopButtonTest {
    private WebDriver driver;
    private OrderPage orderPage;

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        // Без этого окно открывается маленьким (дефолт ChromeDriver), сайт адаптивный
        // и на узкой ширине переключается в мобильную вёрстку — локаторы под десктоп
        // перестают что-либо находить, и форма визуально "не открывается и не заполняется".
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

    @ParameterizedTest(name = "Верхняя кнопка \"Заказать\": {0} {1}")
    // delimiterString переключает разделитель колонок с "," на ";" — адрес содержит
    // запятую ("ул. Ленина, д. 5"), и со стандартным разделителем-запятой CsvSource молча
    // растаскивал её на лишнюю колонку, сдвигая все последующие параметры на одну позицию
    // (phone получал "д. 5", date — телефон, rentalPeriod — дату и т.д.) без единой ошибки.
    @CsvSource(delimiterString = ";", value = {
            // Имя; Фамилия; Адрес; Телефон; Дата; Срок аренды; Цвет; Комментарий
            "Иван; Петров; ул. Ленина, д. 5; 89123456789; 18.08.2026; сутки; чёрный жемчуг; Приехать к 10:00",
            "Мария; Иванова; Невский пр., 10; 89221234567; 20.08.2026; двое суток; серая безысходность; Позвонить за час"
    })
    void positiveOrderFlowTest(String firstName, String lastName, String address,
                               String phone, String date,
                               String rentalPeriod, String color, String comment) {
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
