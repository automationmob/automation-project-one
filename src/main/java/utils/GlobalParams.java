package utils;

import org.json.JSONObject;

public class GlobalParams {
    private static ThreadLocal<String> platformName = new ThreadLocal<String>();
    private static ThreadLocal<String> udid = new ThreadLocal<String>();
    private static ThreadLocal<String> avd = new ThreadLocal<String>();

    private static ThreadLocal<String> deviceName = new ThreadLocal<String>();
    private static ThreadLocal<String> systemPort = new ThreadLocal<String>();
    private static ThreadLocal<String> chromeDriverPort = new ThreadLocal<String>();
    private static ThreadLocal<String> wdaLocalPort = new ThreadLocal<String>();
    private static ThreadLocal<String> webkitDebugProxyPort = new ThreadLocal<String>();
    private static ThreadLocal<String> app = new ThreadLocal<String>();

    private static ThreadLocal<String> screenshotPath=new ThreadLocal<String>();

    private static ThreadLocal<String> runtimeStart=new ThreadLocal<>();

    private static ThreadLocal<String> appPackage=new ThreadLocal<>();
    private static ThreadLocal<String> appWaitActivity=new ThreadLocal<>();
    private static ThreadLocal<String> appActivity=new ThreadLocal<>();

    private static ThreadLocal<String> bundleId=new ThreadLocal<>();

    private static ThreadLocal<String> thisStepFailed=new ThreadLocal<>();



    public void setThisStepFailed(String thisStepFailed1){
        thisStepFailed.set(thisStepFailed1);
    }

    public String getThisStepFailed(){
        return thisStepFailed.get();
    }
    public void setRuntimeStart(String runtimeStart1){
        runtimeStart.set(runtimeStart1);
    }

    public String getRuntimeStart(){
        return runtimeStart.get();
    }
    public void setApp(String app1){
        app.set(app1);
    }

    public String getApp(){
        return app.get();
    }

    public void setAppPackage(String appPackage1){
        appPackage.set(appPackage1);
    }

    public String getAppPackage(){
        return appPackage.get();
    }

    public void setAppWaitActivity(String appWaitActivity1){
        appWaitActivity.set(appWaitActivity1);
    }

    public String getAppWaitActivity(){
        return appWaitActivity.get();
    }

    public void setAppActivity(String appActivity1){
        appActivity.set(appActivity1);
    }

    public String getAppActivity(){
        return appActivity.get();
    }
    public void setPlatformName(String platformName1){
        platformName.set(platformName1);
    }

    public String getPlatformName(){
        return platformName.get();
    }

    public String getUDID() {
        return udid.get();
    }

    public void setUDID(String udid2) {
        udid.set(udid2);
    }

    public String getAVD() {
        return avd.get();
    }



    public void setAVD(String avd2) {
        avd.set(avd2);
    }

    public String getDeviceName() {
        return deviceName.get();
    }

    public void setDeviceName(String deviceName2) {
        deviceName.set(deviceName2);
    }

    public String getSystemPort() {
        return systemPort.get();
    }

    public void setSystemPort(String systemPort2) {
        systemPort.set(systemPort2);
    }

    public String getChromeDriverPort() {
        return chromeDriverPort.get();
    }

    public void setChromeDriverPort(String chromeDriverPort2) {
        chromeDriverPort.set(chromeDriverPort2);
    }

    public String getWdaLocalPort() {
        return wdaLocalPort.get();
    }

    public void setWdaLocalPort(String wdaLocalPort2) {
        wdaLocalPort.set(wdaLocalPort2);
    }

    public String getWebkitDebugProxyPort() {
        return webkitDebugProxyPort.get();
    }

    public void setWebkitDebugProxyPort(String webkitDebugProxyPort2) {
        webkitDebugProxyPort.set(webkitDebugProxyPort2);
    }

    public void setScreenshotPath(String screenshotPath1){
        screenshotPath.set(screenshotPath1);
    }

    public String getScreenshotPath(){
        return screenshotPath.get();
    }

    public void setBundleId(String bundleId1){
        bundleId.set(bundleId1);
    }

    public String getBundleId(){
        return bundleId.get();
    }

    public void initializeGlobalParams(JSONObject retiredOn){
        GlobalParams params = new GlobalParams();


        JSONObject capabilities=retiredOn.getJSONObject("capabilities");

        params.setPlatformName(System.getProperty("platformName", retiredOn.getString("platform")));


        params.setApp(System.getProperty("app", capabilities.getString("app")));

        switch(params.getPlatformName()){
            case "Android":
                params.setDeviceName(System.getProperty("deviceName", capabilities.getString("deviceName")));

                if(!capabilities.get("udid").toString().equalsIgnoreCase("null")){
                    params.setUDID(System.getProperty("udid", capabilities.get("udid").toString()));
                }else {
                        params.setAVD(System.getProperty("avd", capabilities.getJSONArray("avd").get(0).toString()));
                }
                params.setAppPackage(System.getProperty("appPackage", capabilities.getString("appPackage")));
                params.setAppWaitActivity(System.getProperty("appWaitActivity", capabilities.getString("appWaitActivity")));
                params.setAppActivity(System.getProperty("appActivity", capabilities.getString("appActivity")));

                params.setSystemPort(System.getProperty("systemPort", capabilities.getString("systemPort")));
                params.setChromeDriverPort(System.getProperty("chromeDriverPort", capabilities.getString("chromeDriverPort")));
                break;
            case "iOS":
                params.setDeviceName(System.getProperty("deviceName", capabilities.getJSONArray("deviceName").get(0).toString()));
                params.setBundleId(System.getProperty("bundleId", capabilities.getString("bundleId")));
                if (!capabilities.get("udid").toString().equalsIgnoreCase("null")) {
                    params.setAVD(System.getProperty("udid", capabilities.getJSONArray("udid").get(0).toString()));
                }
                params.setWdaLocalPort(System.getProperty("wdaLocalPort", capabilities.getString("wdaLocalPort")));
                params.setWebkitDebugProxyPort(System.getProperty("webkitDebugProxyPort", capabilities.getString("webkitDebugProxyPort")));
                break;
            default:
                throw new IllegalStateException("Invalid Platform Name!");
        }

    }
}
