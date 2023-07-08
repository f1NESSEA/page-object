package ru.netology.web.test;

import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.DashboardPage;
import ru.netology.web.page.LoginPage;
import ru.netology.web.page.TransferPage;

import static com.codeborne.selenide.Selenide.open;

class MoneyTransferTest {
    DashboardPage dashboardPage;

    @BeforeEach
    public void successAuth() {
        open("http://localhost:9999");
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getValidAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getValidVerificationCodeFor();
        dashboardPage = verificationPage.validVerify(verificationCode);
        setInitialBalances(dashboardPage);
    }

    public void setInitialBalances(DashboardPage dashboardPage) {

        int currentBalance = dashboardPage.getFirstCardBalance();
        int depositBalance = 10000 - currentBalance;
        if (depositBalance > 0) {
            dashboardPage.depositFirstCard().deposit(depositBalance, DataHelper.secondCardNumber());
        } else if (depositBalance < 0) {
            dashboardPage.depositSecondCard().deposit(-depositBalance, DataHelper.firstCardNumber());
        }
    }

    @Test
    void PositiveFirstCardToSecond() {
        int actual1 = dashboardPage.depositFirstCard().deposit(4155, DataHelper.secondCardNumber())
                .getFirstCardBalance();
        Assertions.assertEquals(14155, actual1);
        int actual2 = dashboardPage.getSecondCardBalance();
        Assertions.assertEquals(5845, actual2);
    }

    @Test
    void PositiveSecondCardToFirst() {
        int actual = dashboardPage.depositSecondCard().deposit(6578, DataHelper.firstCardNumber())
                .getSecondCardBalance();
        Assertions.assertEquals(16578, actual);
        int actual2 = dashboardPage.getFirstCardBalance();
        Assertions.assertEquals(3422, actual2);

    }

    @Test
    void transferOfAllFromFirstCard() {
        int actual1 = dashboardPage.depositFirstCard().deposit(10000, DataHelper.secondCardNumber())
                .getSecondCardBalance();
        Assertions.assertEquals(0, actual1);
        int actual2 = dashboardPage.getFirstCardBalance();
        Assertions.assertEquals(20000, actual2);
    }

    @Test
    void transferOfAllFromSecondCard() {
        int actual1 = dashboardPage.depositSecondCard().deposit(10000, DataHelper.firstCardNumber())
                .getFirstCardBalance();
        Assertions.assertEquals(0, actual1);
        int actual2 = dashboardPage.getSecondCardBalance();
        Assertions.assertEquals(20000, actual2);
    }

    @Test
    void upperBoundFromFirstCard() {
        int actual1 = dashboardPage.depositFirstCard().deposit(9999, DataHelper.secondCardNumber())
                .getSecondCardBalance();
        Assertions.assertEquals(1, actual1);
        int actual2 = dashboardPage.getFirstCardBalance();
        Assertions.assertEquals(19999, actual2);
    }

    @Test
    void lowerBoundFromFirstCard() {
        int actual1 = dashboardPage.depositFirstCard().deposit(1, DataHelper.secondCardNumber())
                .getSecondCardBalance();
        Assertions.assertEquals(9999, actual1);
        int actual2 = dashboardPage.getFirstCardBalance();
        Assertions.assertEquals(10001, actual2);
    }

    @Test
    void transferZeroFromFirstCard() {
        int actual1 = dashboardPage.depositFirstCard().deposit(0, DataHelper.secondCardNumber())
                .getSecondCardBalance();
        Assertions.assertEquals(10000, actual1);
        int actual2 = dashboardPage.getFirstCardBalance();
        Assertions.assertEquals(10000, actual2);
    }

    @Test
    void upperBoundFromSecondCard() {
        int actual1 = dashboardPage.depositSecondCard().deposit(9999, DataHelper.firstCardNumber())
                .getFirstCardBalance();
        Assertions.assertEquals(1, actual1);
        int actual2 = dashboardPage.getSecondCardBalance();
        Assertions.assertEquals(19999, actual2);
    }

    @Test
    void lowerBoundFromSecondCard() {
        int actual1 = dashboardPage.depositSecondCard().deposit(1, DataHelper.firstCardNumber())
                .getFirstCardBalance();
        Assertions.assertEquals(9999, actual1);
        int actual2 = dashboardPage.getSecondCardBalance();
        Assertions.assertEquals(10001, actual2);
    }

    @Test
    void transferZeroFromSecondCard() {
        int actual1 = dashboardPage.depositSecondCard().deposit(0, DataHelper.firstCardNumber())
                .getSecondCardBalance();
        Assertions.assertEquals(10000, actual1);
        int actual2 = dashboardPage.getFirstCardBalance();
        Assertions.assertEquals(10000, actual2);
    }

    @Test
    void emptyFromForFirstCard() {
        dashboardPage.depositFirstCard().deposit(500, "");
        new TransferPage().checkErrorVisible();
    }

    @Test
    void emptyFromForSecondCard() {
        dashboardPage.depositSecondCard().deposit(500, "");
        new TransferPage().checkErrorVisible();
    }

    @Test
    void clickCancel() {
        TransferPage transferPage = dashboardPage.depositFirstCard();
        transferPage.setAmount(300);
        transferPage.setSourceCard(DataHelper.secondCardNumber());
        new TransferPage().clickCancel();
        int actual1 = dashboardPage.getFirstCardBalance();
        Assertions.assertEquals(10000, actual1);
        int actual2 = dashboardPage.getSecondCardBalance();
        Assertions.assertEquals(10000, actual2);
    }

    @Test
    void invalidCardNumber() {
        dashboardPage.depositSecondCard().deposit(1000, DataHelper.invalidCardNumber());
        new TransferPage().checkErrorVisible();
    }

    @Test
    void specialSymbolAndLettersInAmountField() {
        TransferPage transferPage = dashboardPage.depositFirstCard();
        transferPage.amount.sendKeys(Keys.CONTROL + "A");
        transferPage.amount.sendKeys(Keys.DELETE);
        transferPage.amount.setValue("-+/").shouldBe(Condition.empty);
        transferPage.amount.setValue("asdf").shouldBe(Condition.empty);
    }

    @Test
    void transferFromFirstCardToTheSame() {
        int actual1 = dashboardPage.depositFirstCard().deposit(2000, DataHelper.firstCardNumber())
                .getFirstCardBalance();
        Assertions.assertEquals(10000, actual1);
        int actual2 = dashboardPage.getSecondCardBalance();
        Assertions.assertEquals(10000, actual2);
    }

    @Test
    void transferFromSecondCardToTheSame() {
        int actual1 = dashboardPage.depositSecondCard().deposit(2000, DataHelper.secondCardNumber())
                .getSecondCardBalance();
        Assertions.assertEquals(10000, actual1);
        int actual2 = dashboardPage.getSecondCardBalance();
        Assertions.assertEquals(10000, actual2);
    }

    @Test
    void transferAmountMoreFirstCard() {
        int actual1 = dashboardPage.depositFirstCard().deposit(15000, DataHelper.secondCardNumber())
                .getFirstCardBalance();
        int actual2 = dashboardPage.getSecondCardBalance();
        Assertions.assertEquals(10000, actual2);
        Assertions.assertEquals(10000, actual1);

    }

    @Test
    void transferAmountMoreSecondCard() {
        int actual1 = dashboardPage.depositSecondCard().deposit(20000, DataHelper.firstCardNumber())
                .getSecondCardBalance();
        int actual2 = dashboardPage.getSecondCardBalance();
        Assertions.assertEquals(10000, actual2);
        Assertions.assertEquals(10000, actual1);

    }


}

