package qa.stepsdef;

import qa.pages.HelperPage;
import org.apache.logging.log4j.ThreadContext;
import org.jbehave.core.annotations.*;
import utils.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class LifeCyclePrivate extends HelperPage{
    GlobalParams params=new GlobalParams();
     ServerManager serverManager=  new ServerManager();
    TestUtils utils=new TestUtils();
    DriverManager driverManager=new DriverManager();
    JsonToHtml newStory= new JsonToHtml();
    static File folder ;
    static File[] files ;






    @BeforeStories
    public void beforeStories() throws IOException {
        newStory.executionStartTime=System.nanoTime();
        ThreadContext.put("ROUTINGKEY", params.getPlatformName() + "_" + params.getDeviceName());


        if (params.getPlatformName().equalsIgnoreCase("iOS")) click("allowDevicePermission");

        sendKeys("usernameInput", "usernameSTC");
        click("continueButton");
        sendKeys("passwordInput", "s12345678");
        click("signinBtn");
        utils.sleepTime(1);
        if (params.getPlatformName().equalsIgnoreCase("iOS")) click("allowDevicePermission");
        if (params.getPlatformName().equalsIgnoreCase("iOS")) click("changeSelectedNumberButton");
        if (params.getPlatformName().equalsIgnoreCase("iOS")) click("allowDevicePermission");
        if (params.getPlatformName().equalsIgnoreCase("iOS")) click("firstNumber");
    }
    @BeforeStory
    public void beforeScenario(){
        newStory.startTime=System.nanoTime();

    }

    @AfterStory
    public void afterStory(){
        System.out.println("Afterrrrrrr");
        folder = new File("/Users/aziyadeh/Documents/GitHub/mystcTAEproject/target/jbehave");
        files = folder.listFiles((dir, name) -> name.startsWith("stories") & name.endsWith(".json"));
        if (Arrays.stream(files).count()!=0 && files!=null){
            Arrays.sort(files, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
            newStory.parsingStory(Arrays.stream(files).findFirst().toString().replace("Optional[","").replace("]",""));
            newStory.writeHtmlFile("outPutDataAlpha7.html");

        }
        relaunchApp();


    }



    @AfterScenario
    public void afterScenaario(){
        relaunchApp();

    }


    @AfterStories
    public void afterStories(){

        if (files!=null) {
            Arrays.sort(files, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
        }

        if(driverManager.getDriver() != null){
            driverManager.getDriver().quit();
            utils.log().info("quit appium Driver");
            driverManager.setDriver(null);
        }
    }


}
