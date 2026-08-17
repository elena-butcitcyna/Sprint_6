import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.MainPage;

import java.time.Duration;
import java.util.List;
import java.util.stream.Stream;

import static data.FAQDataProvider.getFAQData;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pages.MainPage.*;

public class AccordionTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        // Маленькое дефолтное окно ChromeDriver может переключать адаптивную вёрстку
        // в мобильный режим и ломать десктоп-локаторы
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("https://qa-scooter.education-services.ru/");

        // Закрываем баннер про куки — иначе он иногда перекрывает последний вопрос
        // и клик по его стрелочке падает с ElementClickIntercepted
        List<WebElement> cookieButton = driver.findElements(By.id("rcc-confirm-button"));
        if (!cookieButton.isEmpty()) {
            cookieButton.get(0).click();
        }
    }

    @AfterEach
    public void teardown() {
        driver.quit();
    }

    static Stream<Arguments> faqData() {
        return getFAQData().entrySet().stream()
                .map(entry -> Arguments.of(entry.getKey(), entry.getValue()));
    }

    @ParameterizedTest(name = "Вопрос: {0}")
    @MethodSource("faqData")
    void accordionOpensCorrectAnswer(String questionText, String expectedAnswer) {
        WebElement faqTitle = wait.until(ExpectedConditions.presenceOfElementLocated(MainPage.faqTitle));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", faqTitle);

        System.out.println("Проверяем вопрос: " + questionText);

        // Находим вопрос по тексту. contains(., ...) берёт текст со всех потомков —
        // сам текст вопроса лежит не прямо в accordion__heading, а во вложенной кнопке,
        // поэтому contains(text(), ...) вопрос не находил.
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

        System.out.println(" - Вопрос '" + questionText + "' успешно проверен");
    }
}
