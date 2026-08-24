package pages;

import org.openqa.selenium.By;


public class MainPage {

    // Заголовок раздела "Вопросы о важном"
    public static final By faqTitle = By.xpath("//div[contains(@class, 'Home_SubHeader') and text()='Вопросы о важном']");

    // Заголовок вопроса
    public static final By questionTitle = By.className("accordion__heading");

    // Кнопка-стрелочка, раскрывающая ответ
    public static final By toggeleButton = By.className("accordion__button");

    // Панель с ответом
    public static final By answerPanel = By.className("accordion__panel");
}
