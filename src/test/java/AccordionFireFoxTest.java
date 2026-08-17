import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.MainPage;

import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pages.MainPage.*;
import static data.FAQDataProvider.getFAQData;

public class AccordionFireFoxTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        driver = new FirefoxDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        driver.get("https://qa-scooter.education-services.ru/");
    }

    @Test
    void allQuestionsOpenCorrectAnswers() {
        // Закрываем баннер про куки — иначе он перекрывает последний вопрос и клик по нему перехватывается
        WebElement faqTitle = driver.findElement(MainPage.faqTitle);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", faqTitle);

        Map<String, String> faqData = getFAQData();

        for (Map.Entry<String, String> entry : faqData.entrySet()) {
            String questionText = entry.getKey();
            String expectedAnswer = entry.getValue();

            System.out.println("Проверяем вопрос: " + questionText);

            WebElement question = driver.findElement(
                    By.xpath("//div[contains(@class, 'accordion__heading') and contains(., '" + questionText + "')]")
            );

            WebElement accordionItem = question.findElement(
                    By.xpath("./ancestor::div[contains(@class, 'accordion__item')]")
            );

            WebElement button = accordionItem.findElement(toggeleButton);

            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", button);

            wait.until(ExpectedConditions.elementToBeClickable(button));

            button.click();

            WebElement panel = accordionItem.findElement(answerPanel);
            wait.until(ExpectedConditions.visibilityOf(panel));

            String actualText = panel.getText();
            assertEquals(expectedAnswer, actualText,
                    "Текст ответа не совпадает для вопроса: " + questionText);

            assertTrue(panel.isDisplayed(),
                    "Панель с ответом должна быть видна для вопроса: " + questionText);

            // Повторно закрывать вопрос не нужно: у react-accessible-accordion (accordion__button)
            // по умолчанию allowZeroExpanded=false — повторный клик по кнопке уже открытого вопроса
            // не сворачивает его. Следующая итерация сама откроет свой вопрос и автоматически
            // свернёт текущий (аккордеон одиночного выбора).

            System.out.println(" - Вопрос '" + questionText + "' успешно проверен");
        }
    }

    @AfterEach
    public void teardown() {
        driver.quit();
    }
}
