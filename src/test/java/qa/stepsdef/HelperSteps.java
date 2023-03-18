package qa.stepsdef;

import com.qa.pages.HelperPage;
import org.assertj.core.api.SoftAssertions;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import utils.GlobalParams;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HelperSteps extends HelperPage {

    @When("User check if the $element display")
    @Given("User check if the $element display")
    @Then("User check if the $element display")
    public void iCheckElement(String element) {
        // Write code here that turns the phrase above into concrete actions
        waitForVisibility(element);
    }

    @When("User click on $element")
    @Then("User click on $element")
    public void userClickOn(String element) {
        // Write code here that turns the phrase above into concrete actions
        click(element);
    }

    @When("User move $direction until to find $element")
    @Then("User move $direction until to find $element")
    public void multisimuserMoveTO(@Named("direction") String direction, @Named("element") String element) throws Exception {
        scrollToElement(element, direction);
    }

    //User check if the viewYourProfileButton equal to the text View your profile
    @When("User check if the $element equal to the text $text")
    @Then("User check if the $element equal to the text $text")
    public void userCheckIfTextEqual(@Named("element") String e, @Named("text") String text) {
        assertTrue(e + " not contains equal " + text, getText(e, "the " + e + " has not been return " + text).equalsIgnoreCase(text));

    }

    @When("User move down until to check $element not contains $text")
    @Then("User move down until to check $element not contains $text")
    public void userCheckIfTextNotContain(@Named("element") String e, @Named("text") String text) {
        assertFalse(e + " contains equal " + text, getText(e, "the " + e + " has been return " + text).equalsIgnoreCase(text));

    }
    @When("User scroll $direction for $Number time/s")
    @Then("User scroll $direction for $Number time/s")
    public void scrollMethod(@Named("direction") String direction, @Named("$Number") int number) throws InterruptedException {
        scrollToDirection(direction, number);

    }


    @When("User check if value $attribute of the $element is $expected")
    @Then("User check if value $attribute of the $element is $expected")
    public void userCheckIfAttributeTrue(@Named("element") String e, @Named("attribute") String attribute, @Named("expected") String expected) {
        assertTrue("The " + e + "" + "not contains value" + expected + " at " + attribute + " attribute", getAttribute(e, attribute).equals(expected));
    }

    @When("User check if the $element contains $text")
    @Then("User check if the $element contains $text")
    public void userCheckIfTextContain(@Named("element") String e, @Named("text") String text) {
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(getText(e, "the " + e + " has not been return " + text).equalsIgnoreCase(text)).isTrue();
    }


    @When("User fill in $element with $text")
    @Then("User fill in $element with $text")
    public void userFillWithText(@Named("element") String e, @Named("text") String text) {
        sendKeys(e, text);
    }


    @When("User at $expectedUI page click on $element")
    @Then("User at $expectedUI page click on $element")
    public void userFindExpectedPage(@Named("element") String e, @Named("expectedUI") String expectedUI) throws IOException {
        if (find(expectedUI, 3)) {
            click(e);
        }
    }


    @When("user at $element page click on $isExpectedBoolean")
    @Then("user at $element page click on $isExpectedBoolean")
    public void changeAttribute(@Named("element") String e, @Named("isExpectedBoolean") boolean isExpectedBoolean) throws IOException {
        GlobalParams params = new GlobalParams();
        if(params.getPlatformName().equalsIgnoreCase("Android")){
            boolean isActuallyBoolean = Boolean.parseBoolean(getAttribute(e, "checked"));
            if (isExpectedBoolean != isActuallyBoolean) {
                click(e);
            }
        }else {
            click(e);
        }
    }

    @When("wait for $num second(s)")
    @Then("wait for $num second(s)")
    public static void sleepTime(@Named("num") int num) throws InterruptedException {
        Thread.currentThread().sleep(num*1000);
    }

    @When("User hides the keyboard")
    @Then("User hides the keyboard")
    @Given("User hides the keyboard")
    public void hideKeyboard() {
        hideKeyboardByDriver();
    }

    @When("User at map to $action for $number time")
    @Then("User at map to $action for $number time")
    @Given("User at map to $action for $number time")
    public void zoomMethod(@Named("touchActivity") String touchActivity
            , @Named("number") int number) throws InterruptedException {
        zoom(touchActivity, number);

    }
    @When("User swipe $element to $direction")
    @Then("User swipe $element to $direction")
    public void swipeMethod(@Named("element") String e
            , @Named("direction") String direction) throws InterruptedException {
        swipe(e,direction,1000);
    }

    @When("User switch to $pageSourceContains")
    @Then("User switch to $pageSourceContains")
    @Given("User switch to $pageSourceContains")
    public void getPageSource(@Named("pageSourceContains")String pageSourceContains) {
        assertTrue("this not contain "+ pageSourceContains,isSourcePageContain(pageSourceContains));
    }
}
