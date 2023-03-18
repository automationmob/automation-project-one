package utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

public class JBehaveStoryParsing extends JsonToHtml{

    private enum ExpectedResult {
        successful, failed, notPerformed
    }


    public  void storyParser(String testCaseName) {
        JSONParser parser = new JSONParser();

        try {
            // Read the JSON file
            Object obj = parser.parse(new FileReader(testCaseName));

            // Convert the object to a JSONObject
            JSONObject jsonObject = (JSONObject) obj;

            // Get the path and title
            String path = (String) jsonObject.get("path");
            String title = (String) jsonObject.get("title");

            // Get the meta array
            JSONArray metaArray = (JSONArray) jsonObject.get("meta");

            // Loop through the meta array
            for (Object metaObj : metaArray) {
                JSONObject meta = (JSONObject) metaObj;
                String keyword = (String) meta.get("keyword");
                String name = (String) meta.get("name");
                String value = (String) meta.get("value");

                System.out.println("Keyword: " + keyword);
                System.out.println("Name: " + name);
                System.out.println("Value: " + value);
            }

            // Get the scenarios array
            JSONArray scenariosArray = (JSONArray) jsonObject.get("scenarios");

            // Loop through the scenarios array
            for (Object scenarioObj : scenariosArray) {
                JSONObject scenario = (JSONObject) scenarioObj;
                String scenarioKeyword = (String) scenario.get("keyword");
                String scenarioTitle = (String) scenario.get("title");
                beforeScenario(scenarioTitle);

                System.out.println("Scenario Keyword: " + scenarioKeyword);
                System.out.println("Scenario Title: " + scenarioTitle);


                //switch if we do not have examples
                if (scenario.get("examples")!=null){
            /*        // Get the examples object
                    JSONObject examplesObj = (JSONObject) scenario.get("examples");


                    // Get the parameters array

                    JSONArray parametersArray = (JSONArray) examplesObj.get("parameters");

                    // Get the parameter names and values
                    JSONArray parameterNames = (JSONArray) parametersArray.get(0);
                    JSONArray parameterValues = (JSONArray) parametersArray.get(1);*/

                    // Loop through the examples array
                    JSONArray examplesArray = (JSONArray) scenario.get("examples");
                    for (Object exampleObj : examplesArray) {
                        JSONObject example = (JSONObject) exampleObj;
                        String exampleKeyword = (String) example.get("keyword");
                        String exampleValue = (String) example.get("value");

                        System.out.println("Example Keyword: " + exampleKeyword);
                        System.out.println("Example Value: " + exampleValue);

                        // Get the steps array
                        JSONArray stepsArray = (JSONArray) example.get("steps");

                        // Loop through the steps array
                        for (Object stepObj : stepsArray) {
                            JSONObject step = (JSONObject) stepObj;
                            String outcome = (String) step.get("outcome");
                            ExpectedResult comingResult= ExpectedResult.valueOf(outcome);
                            String stepText = (String) step.get("step");
                            beforeStep(stepText);

                            if (comingResult==ExpectedResult.successful) successful(stepText);
                            if (comingResult==ExpectedResult.notPerformed) notPerformed(stepText);
                            if (comingResult==ExpectedResult.failed){
                                String getFailure=step.get("failure").toString();
                                String getFailureText=getFailure.substring(getFailure.indexOf("EXCEPTION Text Start"), getFailure.length()-1);
                                String getFailureTextNoRupashData=getFailureText.replace(getFailureText.substring(getFailureText.indexOf("$$$$"), getFailureText.length()-1),"");
                                String getFailureScreenshot=getFailure.substring(getFailure.indexOf("SCREEN EXCEPTION Start:")
                                        , getFailure.indexOf("EXCEPTION Text Start")).replace("SCREEN EXCEPTION Start:","").trim();
                                failed(stepText,getFailureTextNoRupashData,getFailureScreenshot);
                            }

                            afterStep(stepText);

            /*                // Replace the placeholders with parameter values
                            for (int i = 0; i < parameterNames.size(); i++) {
                                String paramName = (String) parameterNames.get(i);
                                String paramValue = (String) parameterValues.get(i);
                                stepText = stepText.replace("<" + paramName + ">", paramValue);
                            }*/
                        }
                    }
                }else{

                    // Get the steps array
                    JSONArray stepsArray = (JSONArray) scenario.get("steps");
                    for (Object stepObj : stepsArray) {
                        JSONObject step = (JSONObject) stepObj;
                        String outcome = (String) step.get("outcome");
                        ExpectedResult comingResult= ExpectedResult.valueOf(outcome);
                        String stepText = (String) step.get("step");

                        beforeStep(stepText);
                        if (comingResult==ExpectedResult.successful) successful(stepText);
                        if (comingResult==ExpectedResult.notPerformed) notPerformed(stepText);
                        if (comingResult==ExpectedResult.failed){
                            String getFailure=step.get("failure").toString();
                            String getFailureScreenshot=getFailure.substring(getFailure.indexOf("SCREEN EXCEPTION Start:")
                                    , getFailure.indexOf("EXCEPTION Text Start")).replace("SCREEN EXCEPTION Start:","").trim();
                            String getFailureText=getFailure.substring(getFailure.indexOf("EXCEPTION Text Start"), getFailure.length()-1);
                            String getFailureTextNoRupashData=getFailureText.replace(getFailureText.substring(getFailureText.indexOf("$$$$"), getFailureText.length()-1),"");

                            failed(stepText,getFailureTextNoRupashData,getFailureScreenshot);
                        }
                        afterStep(stepText);
                    }

                }
                afterScenario();

            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }


}
