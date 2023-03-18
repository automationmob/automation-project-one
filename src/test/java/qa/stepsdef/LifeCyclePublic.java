package qa.stepsdef;

import qa.pages.HelperPage;
import org.apache.logging.log4j.ThreadContext;
import org.jbehave.core.annotations.AfterStories;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.BeforeStories;
import utils.DriverManager;
import utils.GlobalParams;
import utils.ServerManager;
import utils.TestUtils;

import java.io.IOException;

public class LifeCyclePublic extends HelperPage{
    GlobalParams params=new GlobalParams();
     ServerManager serverManager=  new ServerManager();
    TestUtils utils=new TestUtils();
    DriverManager driverManager=new DriverManager();





    @BeforeStories
    public void beforeStories() throws IOException {

        ThreadContext.put("ROUTINGKEY", params.getPlatformName() + "_" + params.getDeviceName());


        if (params.getPlatformName().equalsIgnoreCase("iOS")) click("allowDevicePermission");

        if (params.getPlatformName().equalsIgnoreCase("iOS")) click("loginUsernameMyStoreBtn");

        if (params.getPlatformName().equalsIgnoreCase("iOS")) click("allowDevicePermission");
    }

    @BeforeScenario
    public void beforeScenario(){
        relaunchApp();
    }


    @AfterStories
    public void afterStories(){
        if(driverManager.getDriver() != null){
            driverManager.getDriver().quit();
            utils.log().info("quit appium Driver");
            driverManager.setDriver(null);
        }
        if(serverManager.getServer() != null){
            serverManager.getServer().stop();
            utils.log().info("stopped appium server");
        }
    }


}
