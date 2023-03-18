package utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class JSONManager {

    private static ThreadLocal<JSONObject> iosObjects = new ThreadLocal<JSONObject>();

    private static ThreadLocal<JSONObject> androidObjects = new ThreadLocal<JSONObject>();

    private static ThreadLocal<JSONObject> jsonJBhaveObjects = new ThreadLocal<JSONObject>();


     JSONObject jsonMainObjects;
     JSONArray jsonMobileDriverArray;


    public JSONManager()  {
        try {
            JSONTokenizer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setIOSObjects(JSONObject iosObjects1){
        iosObjects.set(iosObjects1);
    }

    public JSONObject getIOSObjects(){
        return iosObjects.get();
    }

    public void setAndroidObjects(JSONObject androidObjects1){
        androidObjects.set(androidObjects1);
    }

    public JSONObject getAndroidObjects(){
        return androidObjects.get();
    }


    public void setJBehaveObjects(JSONObject jBehaveObjects1){
        jsonJBhaveObjects.set(jBehaveObjects1);
    }

    public JSONObject getJBehaveObjects(){
        return jsonJBhaveObjects.get();
    }




    //Json

    protected void JSONTokenizer() throws IOException {
        InputStream dataIs = null;
        try{
            String fileName="soar.json";
            dataIs= JSONManager.class.getClassLoader().getResourceAsStream(fileName);
            JSONTokener tokenizer=new JSONTokener(dataIs);
            jsonMainObjects=new JSONObject(tokenizer);
            jsonMobileDriverArray=jsonMainObjects.getJSONArray("mobileDriver");
            setIOSObjects(jsonMobileDriverArray.getJSONObject(1));
            setAndroidObjects(jsonMobileDriverArray.getJSONObject(0));
            setJBehaveObjects(jsonMainObjects.getJSONObject("jbehave"));

        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }finally {
            if (dataIs!=null) dataIs.close();
        }
    }


    public List<String> getJBehaveJsonArrayAtList(String JBhaveArrayObject) {
        ArrayList<String> setStoriesListArray = new ArrayList<String>();
        JSONArray getStoriesJsonArray = getJBehaveObjects().getJSONArray(JBhaveArrayObject);
        for (int i = 0; i < getStoriesJsonArray.length(); i++) {
            setStoriesListArray.add(getStoriesJsonArray.get(i).toString());
            }
        return setStoriesListArray;
    }

    public List<String> getDevicesList(String platformName) {
        ArrayList<String> getDevicesArray = new ArrayList<String>();
        JSONArray getDevicesJsonArray = null;
        switch (platformName){
            case "Android":
                 getDevicesJsonArray =androidObjects.get().getJSONObject("capabilities").getJSONArray("avd");
                break;
            case "iOS":
                getDevicesJsonArray = iosObjects.get().getJSONObject("capabilities").getJSONArray("deviceName");
                break;
        }
        for (int i = 0; i < getDevicesJsonArray.length(); i++) {
            getDevicesArray.add(getDevicesJsonArray.get(i).toString());
        }
        return getDevicesArray;
    }

    //properties

}
