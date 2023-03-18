package utils;

import io.appium.java_client.AppiumDriver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LocatorsAndTestDataManager {
    private static AppiumDriver<?> driver;

    private static ThreadLocal<Properties> locatorProps = new ThreadLocal<>();

    TestUtils utils = new TestUtils();

    public LocatorsAndTestDataManager(String platformName){
        try {
            thisLocatorsFile(platformName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setLocatorProps(Properties locatorProps1){
        locatorProps.set(locatorProps1);
    }

    public Properties getLocatorProps(){
        return locatorProps.get();
    }

    public  void thisLocatorsFile(String platformName) throws IOException {
        Properties locatorProps=new Properties();
        InputStream inputStream = null;
        String propFileName = null;
        GlobalParams params=new GlobalParams();

        switch (platformName) {
            case "Android":
                propFileName = "AndroidLocators.properties";
                break;
            case "iOS":
                propFileName = "IOSLocators.properties";
                break;
            default:
                utils.log().fatal("the platform does not mach");
        }

        if(locatorProps.isEmpty()){
            try{
                utils.log().fatal("loading Locators properties");
                //src////AndroidLocators.properties
                inputStream= new FileInputStream( System.getProperty("user.dir") + File.separator + "src"+File.separator+"test"
                        +File.separator+"resources" +File.separator +"locators-and-data" +
                        File.separator +propFileName);
                locatorProps.load(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
                utils.log().fatal("Failed to load config properties. ABORT!!" + e.toString());
            } finally {
                if(inputStream != null){
                    inputStream.close();
                }
            }
        }
        setLocatorProps(locatorProps);
    }

    public Properties getTestDataProps() throws IOException {
        Properties dataProps = new Properties();
        InputStream inputStream = null;
        String testDataFileName = "TestData.properties";

        if(dataProps.isEmpty()){
            try{
                utils.log().fatal("loading Test Data properties");
                //src////AndroidLocators.properties
                inputStream= new FileInputStream( System.getProperty("user.dir") + File.separator + "src"+File.separator+"test"
                        +File.separator+"resources" +File.separator +"locators-and-data" +
                        File.separator +testDataFileName);
                dataProps.load(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
                utils.log().fatal("Failed to load config properties. ABORT!!" + e.toString());
            } finally {
                if(inputStream != null){
                    inputStream.close();
                }
            }
        }
        return dataProps;

    }
}
