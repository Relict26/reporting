package ru.netology.delivery.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.*;

class DeliveryTest {

    @BeforeEach
    void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDown() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }

    @Test
    @DisplayName("Should successful plan and replan meeting")
    void shouldSuccessfulPlanAndReplanMeeting() {
        var validUser  = DataGenerator.Registration.generateUser ("ru");
        var daysToAddForFirstMeeting = 3;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        var daysToAddForSecondMeeting = 7;
        var secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);
        String city = DataGenerator.generateCity("ru");
        String name = DataGenerator.generateName("ru");
        String phone = DataGenerator.generatePhone("ru");

        Configuration.holdBrowserOpen = true;

        // Запланировать первую встречу
        $("span[data-test-id=city]").click();
        $("input[placeholder=Город]").setValue(city);
        $("span[class=menu-item__control]").click();
        $x("//input[@placeholder='Дата встречи']").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $x("//input[@placeholder='Дата встречи']").setValue(firstMeetingDate);
        $("input[name=name]").setValue(name);
        $("span[data-test-id=phone]").click();
        $("input[name=phone]").setValue(phone);
        $("label[data-test-id=agreement]").click();
        $(byText("Запланировать")).click();

        // Проверка успешного сообщения
        $("div.notification").should(appear, Duration.ofSeconds(11));
        $("div.notification").shouldHave(text("Успешно!")).shouldBe(visible);
        $("div.notification").shouldHave(text(firstMeetingDate)).shouldBe(visible);

        refresh();

        // Запланировать вторую встречу
        $("span[data-test-id=city]").click();
        $("input[placeholder=Город]").setValue(city);
        $("span[class=menu-item__control]").click();
        $x("//input[@placeholder='Дата встречи']").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $x("//input[@placeholder='Дата встречи']").setValue(secondMeetingDate);
        $("input[name=name]").setValue(name);
        $("span[data-test-id=phone]").click();
        $("input[name=phone]").setValue(phone);
        $("label[data-test-id=agreement]").click();
        $(byText("Запланировать")).click();

        // Проверка сообщения о перепланировании
        $x("//*[contains(text(), 'У вас уже запланирована встреча на другую дату. Перепланировать?')]").should(appear, Duration.ofSeconds(11)).shouldBe(visible);
        $x("//span[contains(text(), 'Перепланировать')]").click();

        // Проверка успешного сообщения после перепланирования
        $("div.notification").should(appear, Duration.ofSeconds(11));
        $("div.notification").shouldHave(text("Успешно!")).shouldBe(visible);
        $("div.notification").shouldHave(text(secondMeetingDate)).shouldBe(visible);
    }
}
