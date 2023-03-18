package qa.pages;

import io.appium.java_client.*;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.DriverManager;
import utils.GlobalParams;
import utils.LocatorsAndTestDataManager;
import utils.TestUtils;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Properties;

public class HelperPage {

     AppiumDriver<?> driver;
    TestUtils utils = new TestUtils();

    GlobalParams params;

   public static Properties prop;
    public static Properties testData;

    private static String platformName;
    private static String appPackage;
    private static String bundleId;





    public HelperPage()  {
        if (this.driver==null) {
            this.driver = new DriverManager().getDriver();
        }
            params=new GlobalParams();
            try {

                if (prop==null) prop=new LocatorsAndTestDataManager(params.getPlatformName()).getLocatorProps();
                if (testData==null) testData=new LocatorsAndTestDataManager(params.getPlatformName()).getTestDataProps();
                    platformName=params.getPlatformName();
                    appPackage=params.getAppPackage();
                    bundleId=params.getBundleId();

                utils.log().info(params.getPlatformName()+" locators and test data is initialized");
            } catch (IOException e) {
                utils.log().info(params.getPlatformName()+" locators and test data can not initialized");
                throw new RuntimeException(e);
            }
//        PageFactory.initElements(new AppiumFieldDecorator(this.driver), this);
    }


    //Capabilities {app: /Users/aziyadeh/Documents/G..., appActivity: sa.com.stc.ui.landing.Splas..., appPackage: com.stc, appWaitActivity: o.PageFetcherSnapshotState$..., appWaitDuration: 99999, autoGrantPermissions: true, automationName: Appium, avd: Pixel_3a_API_30_x86, avdLaunchTimeout: 99999, avdReadyTimeout: 15000, chromeDriverPort: 11022, databaseEnabled: false, desired: {app: /Users/aziyadeh/Documents/G..., appActivity: sa.com.stc.ui.landing.Splas..., appPackage: com.stc, appWaitActivity: o.PageFetcherSnapshotState$..., appWaitDuration: 99999, autoGrantPermissions: true, automationName: Appium, avd: Pixel_3a_API_30_x86, avdLaunchTimeout: 99999, avdReadyTimeout: 15000, chromeDriverPort: 11022, deviceName: Pixel_3a_API_30_x86, newCommandTimeout: 10000, platformName: android, systemPort: 10022}, deviceApiLevel: 31, deviceManufacturer: Google, deviceModel: sdk_gphone64_arm64, deviceName: emulator-5554, deviceScreenDensity: 440, deviceScreenSize: 1080x2280, deviceUDID: emulator-5554, javascriptEnabled: true, locationContextEnabled: false, networkConnectionEnabled: true, newCommandTimeout: 10000, pixelRatio: 2.75, platform: LINUX, platformName: Android, platformVersion: 12, statBarHeight: 66, systemPort: 10022, takesScreenshot: true, viewportRect: {height: 2082, left: 0, top: 66, width: 1080}, warnings: {}, webStorageEnabled: false}
    //20269ff1-0c6f-4cce-bf9c-8d9c90a7df96

    public void waitForVisibility(String e)  {

        WebDriverWait wait = new WebDriverWait(driver, TestUtils.WAIT);

        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(prop.getProperty(e))));
        } catch (RuntimeException runtimeException) {
                String screenshotValue=driver.getScreenshotAs(OutputType.BASE64);
                String screenshotKey= Long.toString(System.nanoTime());
                utils.setImageBase64(screenshotKey,screenshotValue);
                throw  onTestFailure(screenshotKey,e);
            // Handle the exception here
        }

    }

    public MobileElement convertEToMobileElement(String e)  {
        try {
            WebDriverWait wait = new WebDriverWait(driver, TestUtils.WAIT);
            MobileElement eToMobileElement= (MobileElement) wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(prop.getProperty(e))));
            return eToMobileElement;
        } catch (RuntimeException runtimeException) {

            String screenshotValue=driver.getScreenshotAs(OutputType.BASE64);
            String screenshotKey= Long.toString(System.nanoTime());
            utils.setImageBase64(screenshotKey,screenshotValue);
            throw  onTestFailure(screenshotKey,e);
        }

    }

    public void waitForVisibility(By e) {

        WebDriverWait wait = new WebDriverWait(driver, TestUtils.WAIT);
        wait.until(ExpectedConditions.presenceOfElementLocated(e));


    }

    public void clear(String e) {
        convertEToMobileElement(e).clear();
    }

    public void click(String e) {
        convertEToMobileElement(e).click();
    }



    public void sendKeys(String e, String txt) {
        convertEToMobileElement(e).sendKeys(isTestDataFound(txt));
    }



    public String getAttribute(String e, String attribute) {
        return convertEToMobileElement(e).getAttribute(attribute);
    }

    public String getAttribute(By e, String attribute) {
        waitForVisibility(e);
        return driver.findElement(e).getAttribute(attribute);
    }

    public String getText(String e, String msg) {
        String txt;
        switch(platformName){
            case "Android":
                txt = getAttribute(e, "text");
                break;
            case "iOS":
                txt = getAttribute(e, "label");
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + new GlobalParams().getPlatformName());
        }
        utils.log().info(msg + txt);
        return txt;
    }

    public String getText(By e, String msg) {
        String txt;
        switch(new GlobalParams().getPlatformName()){
            case "Android":
                txt = getAttribute(e, "text");
                break;
            case "iOS":
                txt = getAttribute(e, "label");
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + new GlobalParams().getPlatformName());
        }
        utils.log().info(msg + txt);
        return txt;
    }

    public void closeApp() {
        ((InteractsWithApps) driver).closeApp();
    }

    public void launchApp() {
        ((InteractsWithApps) driver).launchApp();
    }

    public MobileElement andScrollToElementUsingUiScrollable(String childLocAttr, String childLocValue) {
        return (MobileElement) ((FindsByAndroidUIAutomator) driver).findElementByAndroidUIAutomator(
                "new UiScrollable(new UiSelector()" + ".scrollable(true)).scrollIntoView("
                        + "new UiSelector()."+ childLocAttr +"(\"" + childLocValue + "\"));");
    }

    public MobileElement iOSScrollToElementUsingMobileScroll(MobileElement e) {
        RemoteWebElement element = e;
        String elementID = element.getId();
        HashMap<String, String> scrollObject = new HashMap<String, String>();
        scrollObject.put("element", elementID);
        scrollObject.put("toVisible", "sdfnjksdnfkld");
        driver.executeScript("mobile:scroll", scrollObject);
        return e;
    }

    public By iOSScrollToElementUsingMobileScrollParent(MobileElement eParent, String predicateString) {
        RemoteWebElement parent = (RemoteWebElement) eParent;
        String parentID = parent.getId();
        HashMap<String, String> scrollObject = new HashMap<String, String>();
        scrollObject.put("element", parentID);
        scrollObject.put("predicateString", predicateString);
        driver.executeScript("mobile:scroll", scrollObject);
        By m = MobileBy.iOSNsPredicateString(predicateString);
        System.out.println("Mobilelement is " + m);
        return m;
    }

    public void scrollToElement(String e, String direction) throws Exception {
        Dimension size = driver.manage().window().getSize();
        int startX = (int) (size.width * 0.5);
        int endX = (int) (size.width * 0.5);
        int startY = 0;
        int endY = 0;
        boolean isFound = false;

        switch (direction) {
            case "up":
                endY = (int) (size.height * 0.4);
                startY = (int) (size.height * 0.6);
                break;

            case "down":
                endY = (int) (size.height * 0.6);
                startY = (int) (size.height * 0.4);
                break;
        }

        for (int i = 0; i < 3; i++) {
            if (find(prop.getProperty(e), 3)) {
                isFound = true;
                break;
            } else {
                swipe("down", 1000);
            }
        }
        if(!isFound){

            String screenshotValue=driver.getScreenshotAs(OutputType.BASE64);
            String screenshotKey= Long.toString(System.nanoTime());
            utils.setImageBase64(screenshotKey,screenshotValue);
            throw  onTestFailure(screenshotKey,e);
//            throw new Exception("Element not found");
        }

    }

    private RuntimeException onTestFailure(String screenshotKey, String e){

        return new RuntimeException(String.format("SCREEN EXCEPTION Start: %s EXCEPTION Text Start element not found %s \nPlatform: %s v%s \nDevice Name: %s $$$$"
                , screenshotKey,e,driver.getPlatformName(),driver.getCapabilities().getCapability("platformVersion")
        ,driver.getCapabilities().getCapability("avd")));
    }
    public By scrollToElement(By e, String direction) throws Exception {
        Dimension size = driver.manage().window().getSize();
        int startX = (int) (size.width * 0.5);
        int endX = (int) (size.width * 0.5);
        int startY = 0;
        int endY = 0;
        boolean isFound = false;

        switch (direction) {
            case "up":
                endY = (int) (size.height * 0.4);
                startY = (int) (size.height * 0.6);
                break;

            case "down":
                endY = (int) (size.height * 0.6);
                startY = (int) (size.height * 0.4);
                break;
        }

        for (int i = 0; i < 3; i++) {
            if (find(e, 3)) {
                isFound = true;
                break;
            } else {
                swipe("down", 1000);
            }
        }
        if(!isFound){
            throw new RuntimeException(String.format("SCREEN EXCEPTION Start: %s EXCEPTION Text Start", driver.getScreenshotAs(OutputType.BASE64)));

//            throw new Exception("Element not found");
        }
        return e;
    }

    public boolean find(String e, int timeout) throws IOException {

        try {
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            return wait.until(new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver driver1) {
                    driver1=driver;
                    if (driver1.findElement(By.xpath(e)).isDisplayed()) {
                        return true;
                    }
                    return false;
                }
            });
        } catch (Exception e1) {

            return false;
        }
    }

    public boolean find(final By element, int timeout) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            return wait.until(new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver driver1) {
                    driver1=driver;
                    if (driver1.findElement(element).isDisplayed()) {
                        return true;
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            return false;
        }
    }

    public void swipe(String duration, int millis)
            throws InterruptedException {
        Dimension dim = driver.manage().window().getSize();
        int width = dim.getWidth() / 2;
        TouchAction action = new TouchAction(driver);
        int startY = (int) (dim.getHeight() * 0.7);
        int endY = (int) (dim.getHeight() * 0.35);
        switch (duration){
            case "down":
                action.press(PointOption.point(width, startY))
                        .waitAction(WaitOptions.waitOptions(Duration.ofMillis(millis)))
                        .moveTo(PointOption.point(width, endY)).release().perform();
                Thread.currentThread().sleep(1000);
                break;
            case "up":
                action.press(PointOption.point(width, endY))
                        .waitAction(WaitOptions.waitOptions(Duration.ofMillis(millis)))
                        .moveTo(PointOption.point(width, startY)).release().perform();
                Thread.currentThread().sleep(1000);
                break;
        }
    }


    public void relaunchApp(){
        if(platformName.equalsIgnoreCase("iOS")){
            driver.terminateApp(bundleId);
            driver.activateApp(bundleId);
        }else{
            driver.closeApp();
            driver.activateApp(appPackage);
        }
    }

    private String isTestDataFound(String data){
        String isData=testData.getProperty(data);
        if(testData.getProperty(data)==null){
            return data;
        }else {
            return isData;
        }

    }

    public void scrollToDirection(String Direction, int number) throws InterruptedException {
        for (int i = 0; i < number; i++) {
                swipe(Direction, 1000);
            }
        }

    public void hideKeyboardByDriver() {
        try {
            driver.hideKeyboard();
        } catch (Exception e) {

        }
    }

    public void zoom(String touchActivity, int zoomNumberTime) throws InterruptedException {
        int count = 0;
        MultiTouchAction topAction = new MultiTouchAction(driver);
        TouchAction firstAction = new TouchAction(driver);
        TouchAction secondAction = new TouchAction(driver);
        Thread.currentThread().sleep(2000);
        while (zoomNumberTime > count) {
            switch (touchActivity) {
                case "zoom-out":
                    firstAction.press(PointOption.point(530, 627)).press(PointOption.point(530, 627)).release();
                    secondAction.moveTo(PointOption.point(500, 1400)).release();
                    topAction.add(firstAction).add(secondAction).perform();
                    count++;
                    break;

                case "zoom-in":
                    firstAction.press(PointOption.point(500, 1114)).press(PointOption.point(500, 1114)).release();
                    topAction.add(firstAction).add(firstAction).perform();
                    count++;
                    break;
                default:
                    break;
            }

        }

    }

    public void swipe(String e,String direction, int millis)
            throws InterruptedException {
        WebElement thisElement=convertEToMobileElement(e);

        TouchAction action = new TouchAction(driver);
        int startX=thisElement.getSize().getHeight();
        int endX=thisElement.getSize().getHeight()+thisElement.getSize().getWidth();
        int startY=thisElement.getLocation().getY();
        int endY=thisElement.getLocation().getY()+thisElement.getSize().getHeight();

        switch (direction){
            case "left":
                action.press(PointOption.point(0, endX))
                        .waitAction(WaitOptions.waitOptions(Duration.ofMillis(millis)))
                        .moveTo(PointOption.point(0, startX)).release().perform();
                break;
            case "right":
                action.press(PointOption.point(0, startX))
                        .waitAction(WaitOptions.waitOptions(Duration.ofMillis(millis)))
                        .moveTo(PointOption.point(0, endX)).release().perform();
                break;
            case "bottom":
                action.press(PointOption.point(0, startY))
                        .waitAction(WaitOptions.waitOptions(Duration.ofMillis(millis)))
                        .moveTo(PointOption.point(0, endY)).release().perform();
                break;
            case "top":
                action.press(PointOption.point(0, endY))
                        .waitAction(WaitOptions.waitOptions(Duration.ofMillis(millis)))
                        .moveTo(PointOption.point(0, startY)).release().perform();
                break;
        }
    }

    public boolean isSourcePageContain(String pageSourceContains) {
        if (params.getPlatformName().equalsIgnoreCase("IOS")) {
            return driver.getPageSource().contains(isTestDataFound("ios" + pageSourceContains));
        } else {
            return driver.getPageSource().contains(isTestDataFound("android" + pageSourceContains));
        }

    }

}
