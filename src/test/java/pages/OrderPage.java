package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Random;

public class OrderPage {

    // Верхняя кнопка "Заказать" (в шапке страницы)
    public static final By orderButtonTop = By.xpath("//button[contains(@class, 'Button_Button__ra12g') and text()='Заказать']");

    // Нижняя кнопка "Заказать" (внизу страницы)
    public static final By orderButtonBottom = By.xpath("//div[contains(@class, 'Home_FinishButton')]//button[text()='Заказать']");

    // Альтернативный вариант для кнопки заказа (если нужно найти все)
    public static final By orderButtonAll = By.xpath("//button[contains(@class, 'Button_Button__ra12g') and text()='Заказать']");

    // Заголовок первой формы. Реальный класс — Order_Title__3EKne
    public static final By formTitleFirst = By.xpath("//div[contains(@class, 'Order_Title') and contains(., 'Для кого самокат')]");

    // Поле "Имя"
    public static final By inputFirstName = By.xpath("//input[@placeholder='* Имя']");

    // Поле "Фамилия"
    public static final By inputLastName = By.xpath("//input[@placeholder='* Фамилия']");

    // Поле "Адрес: куда привезти заказ"
    public static final By inputAddress = By.xpath("//input[@placeholder='* Адрес: куда привезти заказ']");

    // Поле "Станция метро"
    public static final By inputMetro = By.xpath("//input[@placeholder='* Станция метро']");

    // Все варианты станций метро в выпадающем списке.
    // берём частичное совпадение класса без хэша, чтобы не ломаться на пересборке фронтенда.
    public static final By metroOptions = By.cssSelector("[class*='Order_SelectOption']");

    // Конкретная станция метро (по названию) - используется с параметром
    public static final String metroOptionByNameXpath = "//button[contains(@class, 'Order_SelectOption') and text()='%s']";

    // Поле "Телефон: на него позвонит курьер"
    public static final By inputPhone = By.xpath("//input[@placeholder='* Телефон: на него позвонит курьер']");

    // Кнопка "Далее" (переход ко второй форме)
    public static final By buttonNext = By.xpath("//button[text()='Далее']");

    // Заголовок второй формы. Реальный класс — Order_Title__3EKne
    public static final By formTitleSecond = By.xpath("//div[contains(@class, 'Order_Title') and contains(., 'Про аренду')]");

    // Поле "Когда привезти самокат"
    public static final By inputDate = By.xpath("//input[@placeholder='* Когда привезти самокат']");

    // Поле "Срок аренды" (выпадающий список)
    public static final By inputRentalPeriod = By.className("Dropdown-placeholder");

    // Все варианты срока аренды
    public static final By rentalPeriodOptions = By.xpath("//div[contains(@class, 'Dropdown-option')]");

    // Конкретный срок аренды (по тексту) - используется с параметром
    public static final By rentalPeriodByText = By.xpath("//div[contains(@class, 'Dropdown-option') and text()='%s']");

    // Чекбокс "чёрный жемчуг"
    public static final By colorBlack = By.xpath("//label[text()='чёрный жемчуг']");

    // Чекбокс "серая безысходность"
    public static final By colorGrey = By.xpath("//label[text()='серая безысходность']");

    // Поле "Комментарий для курьера"
    public static final By inputComment = By.xpath("//input[@placeholder='Комментарий для курьера']");

    // Кнопка "Заказать" (в конце второй формы)
    public static final By buttonOrderSubmit = By.xpath("//div[contains(@class, 'Order_Buttons')]/button[text()='Заказать']");

    // Заголовок модального окна "Хотите оформить заказ?"
    public static final By modalConfirmTitle = By.xpath("//div[contains(@class, 'Order_ModalHeader') and text()='Хотите оформить заказ?']");

    // Кнопка "Да" в модальном окне
    public static final By modalConfirmYes = By.xpath("//button[text()='Да']");

    // Кнопка "Нет" в модальном окне
    public static final By modalConfirmNo = By.xpath("//button[text()='Нет']");

    // Заголовок "Заказ оформлен"
    public static final By successMessage = By.xpath("//div[contains(@class, 'Order_ModalHeader') and contains(text(), 'Заказ оформлен')]");

    // Альтернативный вариант: полное сообщение об успехе
    public static final By successMessageFull = By.xpath("//div[contains(@class, 'Order_ModalHeader') and contains(text(), 'Заказ оформлен. Номер заказа')]");

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final Random random = new Random();

    public OrderPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void open(String url) {
        driver.get(url);
        // Закрываем баннер про куки — иначе он перекрывает элементы формы внизу страницы
        // и клики по ним падают с ElementClickIntercepted
        List<WebElement> cookieButton = driver.findElements(By.id("rcc-confirm-button"));
        if (!cookieButton.isEmpty()) {
            cookieButton.get(0).click();
        }
    }

    public void clickTopOrderButton() {
        wait.until(ExpectedConditions.elementToBeClickable(orderButtonTop)).click();
    }

    public void clickBottomOrderButton() {
        // Кнопка внизу страницы — её нужно доскроллить, иначе клик не долетает.
        WebElement bottomOrderButton = driver.findElement(orderButtonBottom);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", bottomOrderButton);
        wait.until(ExpectedConditions.elementToBeClickable(orderButtonBottom)).click();
    }

    // Заполняет первую форму ("Для кого самокат") целиком, включая рандомный выбор станции метро,
    // и переходит по кнопке "Далее". Возвращает название выбранной станции — на случай, если
    // тесту важно залогировать/проверить её.
    public String fillCustomerForm(String firstName, String lastName, String address, String phone) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(inputFirstName)).sendKeys(firstName);
        driver.findElement(inputLastName).sendKeys(lastName);
        driver.findElement(inputAddress).sendKeys(address);

        WebElement metroInput = driver.findElement(inputMetro);
        metroInput.click();
        List<WebElement> metroOptionsList = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(metroOptions)
        );
        WebElement selectedMetro = metroOptionsList.get(random.nextInt(metroOptionsList.size()));
        String metroName = selectedMetro.getText();
        selectedMetro.click();

        driver.findElement(inputPhone).sendKeys(phone);
        driver.findElement(buttonNext).click();
        return metroName;
    }

    // Заполняет вторую форму ("Про аренду") целиком: дату, срок аренды, цвет, комментарий.
    // Включает ожидание провалидированного состояния даты (Input_Filled) и срока аренды
    // (is-selected) — без этого форма визуально выглядит заполненной, но считается невалидной.
    public void fillRentalForm(String date, String rentalPeriod, String color, String comment) {
        WebElement dateInput = wait.until(ExpectedConditions.visibilityOfElementLocated(inputDate));
        dateInput.sendKeys(date);

        // Закрываем календарь кликом по уже подсвеченному (выбранному) дню
        driver.findElement(By.cssSelector(".react-datepicker__day--selected")).click();
        wait.until(d -> {
            String cls = dateInput.getAttribute("class");
            return cls != null && cls.contains("Input_Filled");
        });

        WebElement rentalPlaceholder = driver.findElement(inputRentalPeriod);
        rentalPlaceholder.click();
        String rentalPeriodXpath = String.format("//div[contains(@class, 'Dropdown-option') and text()='%s']", rentalPeriod);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(rentalPeriodXpath))).click();
        wait.until(d -> {
            String cls = rentalPlaceholder.getAttribute("class");
            return cls != null && cls.contains("is-selected");
        });

        String colorXpath = String.format("//label[text()='%s']", color);
        driver.findElement(By.xpath(colorXpath)).click();

        driver.findElement(inputComment).sendKeys(comment);
    }

    public void submitOrder() {
        WebElement submitButton = driver.findElement(buttonOrderSubmit);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", submitButton);
        wait.until(ExpectedConditions.elementToBeClickable(buttonOrderSubmit)).click();
    }

    public void confirmOrder() {
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(30));
        longWait.until(ExpectedConditions.visibilityOfElementLocated(modalConfirmTitle));
        longWait.until(ExpectedConditions.elementToBeClickable(modalConfirmYes)).click();
    }

    public void declineOrder() {
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(30));
        longWait.until(ExpectedConditions.visibilityOfElementLocated(modalConfirmTitle));
        longWait.until(ExpectedConditions.elementToBeClickable(modalConfirmNo)).click();
        longWait.until(ExpectedConditions.invisibilityOfElementLocated(modalConfirmTitle));
    }

    public WebElement waitForSuccessMessage() {
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(30));
        return longWait.until(ExpectedConditions.visibilityOfElementLocated(successMessage));
    }

    public boolean isSuccessMessageDisplayed() {
        return !driver.findElements(successMessage).isEmpty();
    }

    public boolean isSubmitButtonDisplayed() {
        return driver.findElement(buttonOrderSubmit).isDisplayed();
    }
}
