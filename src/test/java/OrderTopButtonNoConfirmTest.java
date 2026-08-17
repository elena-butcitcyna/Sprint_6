import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import pages.OrderPage;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Негативный сценарий: вход через верхнюю кнопку "Заказать", в модалке подтверждения
// нажимается "Нет" — заказ не должен оформляться, форма должна остаться открытой.
public class OrderTopButtonNoConfirmTest {
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

    @Test
    void clickingNoDoesNotCreateOrderTest() {
        orderPage.clickTopOrderButton();
        orderPage.fillCustomerForm("Иван", "Петров", "ул. Ленина, д. 5", "89123456789");
        orderPage.fillRentalForm("18.08.2026", "сутки", "чёрный жемчуг", "Приехать к 10:00");
        orderPage.submitOrder();
        orderPage.declineOrder();

        assertFalse(orderPage.isSuccessMessageDisplayed(),
                "Заказ не должен считаться оформленным после отказа во всплывающем окне!");
        assertTrue(orderPage.isSubmitButtonDisplayed(),
                "После отказа от заказа форма должна оставаться открытой для редактирования!");
    }
}
