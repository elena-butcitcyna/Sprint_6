package util;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class DriverFactory {

    public static WebDriver createDriver(String browser) {
        WebDriver driver = "firefox".equalsIgnoreCase(browser) ? new FirefoxDriver() : new ChromeDriver();
        // Маленькое дефолтное окно может переключать адаптивную вёрстку в мобильный режим
        // и ломать десктоп-локаторы
        driver.manage().window().maximize();
        return driver;
    }
}
