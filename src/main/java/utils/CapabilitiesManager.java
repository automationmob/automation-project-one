package utils;

import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.IOException;

public class CapabilitiesManager {

    TestUtils utils = new TestUtils();

    public DesiredCapabilities getCaps() throws IOException {
        GlobalParams params=new GlobalParams();

        try{
            utils.log().info("getting capabilities");
            DesiredCapabilities caps = new DesiredCapabilities();
            caps.setCapability(MobileCapabilityType.PLATFORM_NAME, params.getPlatformName());
            caps.setCapability(MobileCapabilityType.DEVICE_NAME, params.getDeviceName());

            switch(params.getPlatformName()){
                case "Android":
                    caps.setCapability(MobileCapabilityType.AUTOMATION_NAME, "UiAutomator2");
                    caps.setCapability("appPackage", params.getAppPackage());
                    caps.setCapability("appWaitActivity", params.getAppWaitActivity());
                    caps.setCapability("appActivity", params.getAppActivity());
                    caps.setCapability("appWaitDuration",10000);
                    caps.setCapability("newCommandTimeout",10000);
                    if (params.getUDID()!=null){
                        caps.setCapability("udid", params.getUDID());
                    }else{

                        caps.setCapability("avd", params.getAVD());
                        caps.setCapability("avdLaunchTimeout",99999);
                        caps.setCapability("avdReadyTimeout",15000);
                    }
                    caps.setCapability("systemPort", params.getSystemPort());
                    caps.setCapability("chromeDriverPort", params.getChromeDriverPort());
                    caps.setCapability("autoGrantPermissions","true");

                    //ios app path src/test/resources/
                    String androidAppUrl = System.getProperty("user.dir") + File.separator + "src"+File.separator+"test"
                            +File.separator+"resources" +File.separator +"apps" +
                            File.separator + params.getApp();
                    utils.log().info("appUrl is" + androidAppUrl);
                    caps.setCapability("app", androidAppUrl);
                    break;
                case "iOS":
                    caps.setCapability(MobileCapabilityType.AUTOMATION_NAME,"XCUITest");
                    //String iOSAppUrl = getClass().getResource(props.getProperty("iOSAppLocation")).getFile();
                    String iOSAppUrl = System.getProperty("user.dir") + File.separator + "src"+File.separator+"test"
                            +File.separator+"resources" +File.separator +"apps" +
                            File.separator  + params.getApp();
                    utils.log().info("appUrl is" + iOSAppUrl);
                    caps.setCapability("app", iOSAppUrl);
                    caps.setCapability("bundleId", params.getBundleId());
                    caps.setCapability("wdaLocalPort", params.getWdaLocalPort());
                    caps.setCapability("webkitDebugProxyPort", params.getWebkitDebugProxyPort());
                    caps.setCapability("clearSystemFiles","true");
                    break;
            }
            return caps;
        } catch(Exception e){
            e.printStackTrace();
            utils.log().fatal("Failed to load capabilities. ABORT!!" + e.toString());
            throw e;
        }
    }



    public Capabilities getCaps2(String deviceName, String systemPort, String chromeDriverPort, String wdaLocalPort, String webkitDebugProxyPort) {
        GlobalParams params=new GlobalParams();

        try{
            utils.log().info("getting capabilities");
            DesiredCapabilities caps = new DesiredCapabilities();
            caps.setCapability(MobileCapabilityType.PLATFORM_NAME, params.getPlatformName());
            caps.setCapability(MobileCapabilityType.DEVICE_NAME, deviceName);

            switch(params.getPlatformName()){
                case "Android":
                    caps.setCapability(MobileCapabilityType.AUTOMATION_NAME, "UiAutomator2");
                    caps.setCapability("appPackage", params.getAppPackage());
                    caps.setCapability("appWaitActivity", params.getAppWaitActivity());
                    caps.setCapability("appActivity", params.getAppActivity());
                    caps.setCapability("appWaitDuration",99999);
                    caps.setCapability("newCommandTimeout",10000);
                    if (params.getUDID()!=null){
                        caps.setCapability("udid", params.getUDID());
                    }else{

                        caps.setCapability("avd", deviceName);
                        caps.setCapability("avdLaunchTimeout",99999);
                        caps.setCapability("avdReadyTimeout",15000);
                    }
                    caps.setCapability("systemPort", systemPort);
                    caps.setCapability("chromeDriverPort", chromeDriverPort);
                    caps.setCapability("autoGrantPermissions","true");

                    //ios app path src/test/resources/
                    String androidAppUrl = System.getProperty("user.dir") + File.separator + "src"+File.separator+"test"
                            +File.separator+"resources" +File.separator +"apps" +
                            File.separator + params.getApp();
                    utils.log().info("appUrl is" + androidAppUrl);
                    caps.setCapability("app", androidAppUrl);
                    break;
                case "iOS":
                    caps.setCapability(MobileCapabilityType.AUTOMATION_NAME,"XCUITest");
                    //String iOSAppUrl = getClass().getResource(props.getProperty("iOSAppLocation")).getFile();
                    String iOSAppUrl = System.getProperty("user.dir") + File.separator + "src"+File.separator+"test"
                            +File.separator+"resources" +File.separator +"apps" +
                            File.separator  + params.getApp();
                    utils.log().info("appUrl is" + iOSAppUrl);
                    caps.setCapability("app", iOSAppUrl);
                    caps.setCapability("bundleId", params.getBundleId());
                    caps.setCapability("wdaLocalPort", wdaLocalPort);
                    caps.setCapability("webkitDebugProxyPort", webkitDebugProxyPort);
                    caps.setCapability("clearSystemFiles","true");
                    break;
            }
            return caps;
        } catch(Exception e){
            e.printStackTrace();
            utils.log().fatal("Failed to load capabilities. ABORT!!" + e.toString());
            throw e;
        }

    }
}
