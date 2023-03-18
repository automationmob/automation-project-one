package qa.runners;

import org.jbehave.core.io.StoryFinder;
import org.json.JSONArray;
import utils.*;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;

public class Threading extends Thread {
    private String deviceName,chromeDriverPort,systemPort,wdaLocalPort,webkitDebugProxyPort,storiesList;
    private URL serverURL;

     DriverManager driverManagement;
    static String  platformRetied;

    static JSONManager jsonManager=new JSONManager();

    static boolean isOtherThread=false;
    static int reserveOtherCounting=0;
    static int reservePublicThread = 0;
    static int reserveOtherThread = 0;
    static int reservePublicCounting=0;

    static int  availableThreads = (int) jsonManager.getJBehaveObjects().getNumber("threads");
   static List originalList= new StoryFinder().findPaths(codeLocationFromClass(PrivateStories.class).getFile(),
           getAllStoriesPath(jsonManager.getJBehaveObjects().getJSONArray("storiesPath")), null);


    static ServerManager serverManager;
    TestUtils utils=new TestUtils();






/*    iPhone 13 Pro
    iPhone 13 Pro Max
    iPhone 13*/


/*    emulator-5554 Pixel_3a_API_30_x86
    emulator-5556 Pixel_4_API_31
    emulator-5558 Pixel */

    public static void main(String[] args) {
//        Stories stories=new Stories();
//        stories.run();
        int rand = new Random().nextInt(90);

        JSONManager jsonManager=new JSONManager();
        serverManager= new ServerManager();
        serverManager.startServer();

         if(jsonManager.getAndroidObjects().getBoolean("execute")){
             platformRetied="Android";
         }else {
             platformRetied="iOS";
         }

           List<String> devicesList = jsonManager.getDevicesList(platformRetied);

           List<Integer> chromeDriverPortList = Arrays.asList(10000+rand, 10032+rand+1, 10033+rand+2);
           List<Integer> systemPortList = Arrays.asList(11000+rand, 11032+rand+1, 11033+rand+2);
           List<Integer> wdaLocalPortList = Arrays.asList(10000+rand, 10032+rand+1, 10033+rand+2);
           List<Integer> webkitDebugProxyPortList = Arrays.asList(11000+rand, 11032+rand+1, 11033+rand+2);

           for (int i = 0; i < jsonManager.getJBehaveObjects().getInt("threads"); i++) {
               new Threading(platformRetied,jsonManager.getJBehaveJsonArrayAtList("storiesPath").get(i)
                       , devicesList.get(i), serverManager.getServer().getUrl(),
                       chromeDriverPortList.get(i).toString()
                       , systemPortList.get(i).toString()
                       , wdaLocalPortList.get(i).toString()
                       , webkitDebugProxyPortList.get(i).toString()).start();
           }

    }


    public Threading(String threadName,String storiesPath, String deviceName, URL serverURL, String chromeDriverPort, String systemPort
            , String wdaLocalPort, String webkitDebugProxyPort){
        super(threadName);
        this.deviceName=deviceName;
        this.serverURL=serverURL;
        this.chromeDriverPort=chromeDriverPort;
        this.systemPort=systemPort;
        this.wdaLocalPort=wdaLocalPort;
        this.webkitDebugProxyPort=webkitDebugProxyPort;
        driverManagement=new DriverManager();
        this.storiesList=storiesPath;

    }

    private static List getAllStoriesPath(JSONArray jsonArray) {
        ArrayList<Object> listData = new ArrayList<Object>();

        //Checking whether the JSON array has some value or not
        if (jsonArray != null) {

            //Iterating JSON array
            for (int i = 0; i < jsonArray.length(); i++) {

                //Adding each element of JSON array into ArrayList
                listData.add(jsonArray.get(i));
            }
        }
        return listData;
    }

   @Override
    public void run() {
       GlobalParams params=new GlobalParams();
       JSONManager jsonManager=new JSONManager();


       switch (Thread.currentThread().getName()) {
        case "Android":
            params.initializeGlobalParams(jsonManager.getAndroidObjects());
            initializeDrivers();
            new PrivateStories(storiesList, driverManagement, params).run();
            break;
        case "iOS":
            params.initializeGlobalParams(jsonManager.getIOSObjects());
            initializeDrivers();
            new PrivateStories(storiesList, driverManagement, params).run();
            break;
    }
       if(serverManager.getServer() != null){
           serverManager.getServer().stop();
           utils.log().info("stopped appium server");
       }
   }

    protected  List<String> getExistPathForThisThread(List<String> storiesList) {
        int x = 1 - Collections.binarySearch(storiesList, "stories/02-Home Page/") - 2;
        int y = 1 - Collections.binarySearch(storiesList, "stories/06-Hamburger Public/") - 2;


        switch (jsonManager.getJBehaveObjects().getString("lifeCycle")){
            case "Mix":
//                if(availableThreads==0) availableThreads=
                if(availableThreads>2) {
                    if(reservePublicThread==0) reservePublicThread= availableThreads/ 3;
                    if (reservePublicCounting < reservePublicThread) {
                        storiesList.removeAll(storiesList.subList(x, y));
                        int start= (int) (storiesList.size()* ((reservePublicCounting / ((double)reservePublicThread))));
                        int end = (int) (storiesList.size()* ((double)(reservePublicCounting + 1) / ((double)reservePublicThread)));
                        if (end>storiesList.size()) end=storiesList.size();
                        storiesList=storiesList.subList(start, end);
                        availableThreads = availableThreads - 1;
                        reservePublicCounting++;
                    }
                }else {
                    isOtherThread=true;
                }
                if(isOtherThread) {
                    if (reserveOtherThread == 0) reserveOtherThread = availableThreads;
                    if (reserveOtherCounting <= availableThreads) {
                        storiesList=originalList.subList(x, y);
                        int start = (int) (storiesList.size() * ((reserveOtherCounting / ((double) reserveOtherThread))));
                        int end = (int) (storiesList.size() * ((double) (reserveOtherCounting + 1) / ((double) reserveOtherThread)));
                        if (end>=storiesList.size()) end=storiesList.size()-1;
                        storiesList = storiesList.subList(start, end);
                        System.out.println("this number of cases at public"+storiesList.size());
                        availableThreads = availableThreads - 1;
                        reserveOtherCounting++;
                    }
                }
                break;
           /* case "PrivateOnly":
                if(reserveOtherThread==0) reserveOtherThread= availableThreads;
                if (availableThreads >0) {
                    storiesList=originalList.subList(x,y);
                    int start= (int) (storiesList.size()* ((reserveOtherCounting / ((double)reserveOtherThread))));
                    int end = (int) (storiesList.size()* ((double)(reserveOtherCounting + 1) / ((double)reserveOtherThread)));
                    storiesList=storiesList.subList(start, end);
                    System.out.println("this number of cases at private"+storiesList.size());
                    availableThreads = availableThreads - 1;
                    reserveOtherCounting++;
                }
                break;*/
           /* case "PublicOnly":
                if(reserveOtherThread==0) reserveOtherThread= availableThreads;
                if (availableThreads >0) {
                    int start= (int) (storiesList.size()* ((reserveOtherCounting / ((double)reserveOtherThread))));
                    int end = (int) (storiesList.size()* ((double)(reserveOtherCounting + 1) / ((double)reserveOtherThread)));
                    storiesList=storiesList.subList(start, end);
                    availableThreads = availableThreads - 1;
                    reserveOtherCounting++;
                }
                break;*/
        }
        return storiesList;
    }

    private void initializeDrivers(){
        try {
            driverManagement.initializeDriver(deviceName,serverURL,chromeDriverPort,systemPort,wdaLocalPort,webkitDebugProxyPort);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
