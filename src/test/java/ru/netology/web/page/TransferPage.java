package ru.netology.web.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class TransferPage {
    public SelenideElement amount = $("[data-test-id=amount] input");
    public SelenideElement cardFrom = $("[data-test-id=from] input");
    public SelenideElement cardTo = $("[data-test-id=to] input");
    public SelenideElement depositButton = $("[data-test-id=action-transfer]");
    public SelenideElement messageError = $("[data-test-id=error-notification]");
    public SelenideElement cancelButton = $("[data-test-id=action-cancel]");

    public TransferPage() {
        $x("//*[contains(text(), 'Пополнение карты')]").shouldBe(Condition.visible);
    }

    public DashboardPage deposit(int depositAmount, String sourceCard) {
        setAmount(depositAmount);
        setSourceCard(sourceCard);
        depositButton.click();
        return new DashboardPage();
    }

    public void setSourceCard(String sourceCard) {
        cardFrom.sendKeys(Keys.CONTROL + "A");
        cardFrom.sendKeys(Keys.DELETE);
        cardFrom.setValue(sourceCard);
    }

    public void setAmount(int depositAmount) {
        amount.sendKeys(Keys.CONTROL + "A");
        amount.sendKeys(Keys.DELETE);
        amount.setValue(Integer.toString(depositAmount));
    }

    public void checkErrorVisible() {
        messageError.shouldBe(Condition.visible);
    }

    public void clickCancel() {
        cancelButton.click();
    }
}
