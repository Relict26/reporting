package ru.netology.delivery.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

class DeliveryTest {

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
        Configuration.browserSize = "1280x800";
        Configuration.timeout = 10000; // Увеличиваем таймаут до 10 секунд
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
        // Явная проверка загрузки страницы
        $("[data-test-id=city]").shouldBe(visible);
    }

    @Test
    @DisplayName("Should successful plan and replan meeting")
    void shouldSuccessfulPlanAndReplanMeeting() {
        var user = DataGenerator.Registration.generateUser("ru");
        var firstMeetingDate = DataGenerator.generateDate(4);
        var secondMeetingDate = DataGenerator.generateDate(7);

        // Шаг 1: Заполнение формы первый раз
        fillForm(user, firstMeetingDate);

        // Проверка успешного уведомления
        checkSuccessNotification(firstMeetingDate);

        // Шаг 2: Перепланирование встречи
        replanMeeting(secondMeetingDate);

        // Проверка успешного перепланирования
        checkSuccessNotification(secondMeetingDate);
    }

    private void fillForm(DataGenerator.UserInfo user, String meetingDate) {
        // Заполнение города
        $("[data-test-id=city] input").setValue(user.getCity().substring(0, 2));
        $$(".menu-item").findBy(text(user.getCity())).click();

        // Заполнение даты
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(meetingDate);

        // Заполнение остальных полей
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $("[data-test-id=agreement]").click();
        $(".button").click();
    }

    private void checkSuccessNotification(String date) {
        $("[data-test-id=success-notification]")
                .shouldBe(visible, Duration.ofSeconds(15))
                .shouldHave(text("Успешно!"))
                .shouldHave(text(date));
    }

    private void replanMeeting(String newDate) {
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(newDate);
        $(".button").click();

        $("[data-test-id=replan-notification]")
                .shouldBe(visible, Duration.ofSeconds(15))
                .shouldHave(text("Необходимо подтверждение"));

        $("[data-test-id=replan-notification] button").click();
    }
}