package hotel;

import static hotel.Utils.BASE_URL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static hotel.Utils.getNewWindowHandle;
import static hotel.Utils.sleep;
import static org.junit.jupiter.api.Assertions.assertAll;
import static hotel.Utils.BASE_URL;
import static hotel.Utils.getNewWindowHandle;
import static hotel.Utils.sleep;
import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import hotel.pages.TopPage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.WebDriver;
import hotel.pages.ReservePage;
import java.time.LocalDate;
import hotel.pages.ReservePage;
import hotel.pages.ReservePage.Contact;
import hotel.pages.RoomPage;
import hotel.pages.TopPage;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


import java.time.format.DateTimeFormatter;
import java.util.Date;


@TestMethodOrder(OrderAnnotation.class)
@DisplayName("課題")
class myTest {

    private static WebDriver driver;

    private static final DateTimeFormatter SHORT_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    private static final DateTimeFormatter LONG_FORMATTER = DateTimeFormatter.ofPattern("yyyy年M月d日");

    @BeforeAll
    static void initAll() {
        driver = Utils.createWebDriver();
    }

    @AfterEach
    void tearDown() {
        driver.manage().deleteAllCookies();
    }

    @AfterAll
    static void tearDownAll() {
        driver.quit();
    }

    @Test
    @Order(1)
    @DisplayName("課題1")
    void testCase1() {
        driver.get(BASE_URL);
        var topPage = new TopPage(driver);

//  1. プレミアム会員でログインする
        var loginPage = topPage.goToLoginPage();
        var myPage = loginPage.doLogin("ichiro@example.com", "password");

        assertEquals("マイページ", myPage.getHeaderText());

//  2. 「宿泊予約」ボタンをタップ する
        var plansPage = myPage.goToPlansPage();

        assertEquals("宿泊プラン一覧", myPage.getHeaderText());

    }
}