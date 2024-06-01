package hotel;

import static hotel.Utils.BASE_URL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static hotel.Utils.getNewWindowHandle;
import static hotel.Utils.sleep;
import static org.junit.jupiter.api.Assertions.assertAll;
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
import hotel.pages.ReservePage.Contact;

import java.time.LocalDate;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@TestMethodOrder(OrderAnnotation.class)
@DisplayName("課題")
class MyTest {

    private static final DateTimeFormatter SHORT_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static WebDriver driver;
    private static WebDriverWait wait;
    private String originalHandle;

    @BeforeAll
    static void initAll() {
        driver = Utils.createWebDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @BeforeEach
    void init() {
        originalHandle = driver.getWindowHandle();
    }

    @AfterEach
    void tearDown() {
        if (driver.getWindowHandles().size() > 1) {
            driver.close();
        }
        driver.switchTo().window(originalHandle);
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

        //  3. テーマパーク優待プランの「このプランで予約」ボタンをタップする
        var originalHandles = driver.getWindowHandles();
        plansPage.openPlanByTitle("テーマパーク優待プラン");
        sleep(500);
        var newHandles = driver.getWindowHandles();
        var newHandle = getNewWindowHandle(originalHandles, newHandles);
        driver.switchTo().window(newHandle);
        var reservePage = new ReservePage(driver);

        var tomorrow = SHORT_FORMATTER.format(LocalDate.now().plusDays(1));

        assertAll("初期表示値",
                () -> assertEquals("テーマパーク優待プラン", reservePage.getPlanName()),
                () -> assertEquals(tomorrow, reservePage.getReserveDate()),
                () -> assertEquals("1", reservePage.getReserveTerm()),
                () -> assertEquals("1", reservePage.getHeadCount()),
                () -> assertEquals("山田一郎", reservePage.getUsername()),
                () -> assertFalse(reservePage.isEmailDisplayed()),
                () -> assertFalse(reservePage.isTelDisplayed())
        );
        reservePage.setContact(Contact.メールでのご連絡);
        assertAll("メール連絡選択時",
                () -> assertTrue(reservePage.isEmailDisplayed()),
                () -> assertFalse(reservePage.isTelDisplayed()),
                () -> assertEquals("ichiro@example.com", reservePage.getEmail())
        );
        reservePage.setContact(Contact.電話でのご連絡);
        assertAll("電話連絡選択時",
                () -> assertFalse(reservePage.isEmailDisplayed()),
                () -> assertTrue(reservePage.isTelDisplayed()),
                () -> assertEquals("01234567891", reservePage.getTel())
        );

        //  4. 「宿泊予約」画面に下記のデータを入力する
        reservePage.setReserveDate(SHORT_FORMATTER.format(LocalDate.parse("2024/07/15", SHORT_FORMATTER)));
        reservePage.setReserveTerm("3");
        reservePage.setHeadCount("2");
        reservePage.setBreakfastPlan(true);
        reservePage.setContact(Contact.電話でのご連絡);
        reservePage.setTel("00011112222");

        //  5. 合計は上記のデータにより正しく表示されているのを確認する 不会
        assertAll("合計金額",
                () -> assertEquals("66,000円", reservePage.getTotalBill())
        );

        //  6. 「予約を確認する」をタップする
        var confirmPage = reservePage.goToConfirmPage();

        //　7. 「宿泊予約確認」画面に前の画面に入力したデータが正しく表示されているのを確認する
        assertAll("予約確認",
                () -> assertEquals("合計 66,000円（税込み）", confirmPage.getTotalBill()),
                () -> assertEquals("テーマパーク優待プラン", confirmPage.getPlanName()),
                () -> assertEquals("2024年7月15日 〜 2024年7月18日 3泊", confirmPage.getTerm()),
                () -> assertEquals("2名様", confirmPage.getHeadCount()),
                () -> assertTrue(confirmPage.getPlans().contains("朝食バイキング")),
                () -> assertEquals("山田一郎様", confirmPage.getUsername()),
                () -> assertEquals("電話：00011112222", confirmPage.getContact()),
                () -> assertEquals("なし", confirmPage.getComment())
        );

        //　8. 「この内容で予約する」ボタンをタップする
        confirmPage.doConfirm();

        //　9. 「予約を完了しました」というポップアップメッセージが表示されているのを確認する
        assertEquals("ご来館、心よりお待ちしております。", confirmPage.getModalMessage());

        //  10. 「閉じる」ボタンをタップする
        confirmPage.close();

        //  11. 「宿泊予約」スクリーンに戻るのを確認する
        assertTrue(wait.until(ExpectedConditions.numberOfWindowsToBe(1)));

    }
}