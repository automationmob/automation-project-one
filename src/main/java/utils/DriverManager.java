package utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;

import java.io.IOException;
import java.net.URL;

public class DriverManager {
    private static ThreadLocal<AppiumDriver> driver=new ThreadLocal<>();



    public AppiumDriver getDriver(){
        return driver.get();
    }

    public void setDriver(AppiumDriver driver2){
       driver.set(driver2);
    }


    public void initializeDriver() throws IOException {
        AppiumDriver driver=null;

        GlobalParams params = new GlobalParams();


        TestUtils utils=new TestUtils();
        if(driver == null){
            try{
                utils.log().info("initializing Appium driver");
                switch(params.getPlatformName()){
                    case "Android":
                         driver = new AndroidDriver(new ServerManager().getServer().getUrl(), new CapabilitiesManager().getCaps());
                        break;
                    case "iOS":
                         driver = new IOSDriver(new ServerManager().getServer().getUrl(), new CapabilitiesManager().getCaps());
                        break;
                }
                if(driver == null){
                    throw new Exception("driver is null. ABORT!!!");
                }
                utils.log().info("Driver is initialized");
                setDriver(driver);
            } catch (IOException e) {
                e.printStackTrace();
                utils.log().fatal("Driver initialization failure. ABORT !!!!" + e.toString());
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }


    public void initializeDriver(String deviceName, URL serverURL, String systemPort, String chromeDriverPort
            , String wdaLocalPort, String webkitDebugProxyPort) throws IOException {
        AppiumDriver driver=null;
        GlobalParams params = new GlobalParams();

        TestUtils utils=new TestUtils();
        if(driver == null){
            try{
                utils.log().info("initializing Appium driver");
                switch(params.getPlatformName()){
                    case "Android":
                        driver = new AndroidDriver(serverURL, new CapabilitiesManager().getCaps2(deviceName,
                                systemPort,chromeDriverPort,wdaLocalPort,webkitDebugProxyPort));
                        break;
                    case "iOS":
                        driver = new IOSDriver(serverURL, new CapabilitiesManager().getCaps2(deviceName,
                                systemPort,chromeDriverPort,wdaLocalPort,webkitDebugProxyPort));
                        break;
                }
                if(driver == null){
                    throw new Exception("driver is null. ABORT!!!");
                }
                utils.log().info("Driver is initialized");
                this.driver.set(driver);
                setDriver(driver);
            } catch (IOException e) {
                e.printStackTrace();
                utils.log().info("Driver initialization failure. ABORT !!!!" + e.toString());
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }
}
