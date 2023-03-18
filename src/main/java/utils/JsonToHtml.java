package utils;

import org.apache.commons.text.StringEscapeUtils;

import java.io.*;
import java.text.DecimalFormat;
import java.util.HashMap;

public class JsonToHtml{
     StringBuilder  stepsBuilder;
     String currentScenario;
    private HashMap<String, Long> scenriosTime;

    private static StringBuffer lineChartData= new StringBuffer();
    private static StringBuffer  logReportText = new StringBuffer();

    private static Integer passedTestCount;
    private static Integer failedTestCount;
    private static Integer skippedTestCount;
    private double totalExecutionTime;
    public long startTime;
    static public long executionStartTime;


    String getCurrentStep;
     ExpectedResult successful;

     TestUtils utils=new TestUtils();
     GlobalParams params=new GlobalParams();


    public JsonToHtml() {
        stepsBuilder = new StringBuilder();
        passedTestCount=0;
        failedTestCount=0;
        skippedTestCount=0;
    }

    public void parsingStory(String storyName) {
        JBehaveStoryParsing storyParsing= new JBehaveStoryParsing();
        storyParsing.storyParser(storyName);
    }


    private enum ExpectedResult {
        passed, failed, skipped
    }


    public String generateHtml() throws IOException {
        Integer totalTestCount = passedTestCount + failedTestCount + skippedTestCount;
        DecimalFormat twoDecimals = new DecimalFormat("#0.00");

        double passedPercent = (double) passedTestCount / (double) totalTestCount;
        double failedPercent = (double) failedTestCount / (double) totalTestCount;
        double skippedPercent = (double) skippedTestCount / (double) totalTestCount;

        StringBuilder pieChartData = new StringBuilder();
        pieChartData.append("{title:\"Passed\",value:" + twoDecimals.format(passedPercent) + "},");
        pieChartData.append("{title:\"Failed\",value:" + twoDecimals.format(failedPercent) + "},");
        pieChartData.append("{title:\"Skipped\",value:" + twoDecimals.format(skippedPercent) + "}");



        String templateFileName = System.getProperty("user.dir") + File.separator+"Android" + "Template.html";
        BufferedReader br = new BufferedReader(new FileReader(templateFileName));


        String totalExecutionTimeHeader = String.format("total %s", getExecutionTimeFormatted(totalExecutionTime));


        StringBuilder html = new StringBuilder();
        String line = br.readLine();
        while (line != null) {
            html.append(line);
            html.append(System.lineSeparator());
            line = br.readLine();
        }
        String content = html.toString();

        br.close();
        content = content.replace("@@@totalExecutionTimeHeader@@@", totalExecutionTimeHeader);// need to update by duration
        content = content.replace("@@@DeviceName@@@", "device");// need to update by params
        content = content.replace("@@@BuildName@@@", "Latest Version");// need to update by params
        content = content.replace("@@@TestCases@@@",  logReportText.toString());// by test cases method
        content = content.replace("@@@pieChart@@@", pieChartData.toString());
        content = content.replace("@@@lineChart@@@", lineChartData.toString());
        content = content.replace("@@@ReportTitle@@@", "STC Mobile ("+params.getPlatformName()+")");
//        content = content.replace("@@@build_number@@@", m_BuildNumber);
//        content = content.replace("@@@platform@@@", m_PlatForm);
//        content = content.replace("@@@reportSubTitle@@@", reportSubTitle);
//        content = content.replace("@@@Title@@@", title);
        content = content.replace("@@@PaasedCount@@@", passedTestCount.toString());
        content = content.replace("@@@FailedCount@@@", failedTestCount.toString());
        content = content.replace("@@@SkippedCount@@@", skippedTestCount.toString());
        content = content.replace("@@@date@@@", utils.getDateTime());
        html.append("<html>");
        html.append("<head>");
        html.append("<title>JSON to HTML Example</title>");
        html.append("</head>");
        html.append("<body>");
        html.append("</body>");
        html.append("</html>");
        return content;
    }



    public void writeHtmlFile(String fileName) {
        try {
            FileWriter fileWriter = new FileWriter(fileName);
            fileWriter.write(generateHtml());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void testCaseContent(String title, ExpectedResult expectedResult, String description){
        //Get story details
        String code = "P";

        if (expectedResult == ExpectedResult.passed) passedTestCount++;
        if (expectedResult == ExpectedResult.failed)code="F";failedTestCount++;
        if (expectedResult == ExpectedResult.skipped)code="K"; skippedTestCount++;

        long endTime = System.nanoTime();

        long duration = endTime - startTime;
        double seconds = (double) duration / 1000000000.0;
        double roundOff = Math.round(seconds * 100.0) / 100.0;
        totalExecutionTime = Math.round(((double) (endTime - executionStartTime) / 1000000000.0) * 100.0) / 100.0;


        logReportText.append("<div class=\"test-case " + expectedResult.toString() + "\">");
        logReportText.append("<div class=\"icon\">" + code + "</div>");
        logReportText.append("<div class=\"body\">");
//        logReportText.append(changeTitle(title));
        logReportText.append(title);
        logReportText.append(" <span>(" + roundOff + " seconds)</span>");
        logReportText.append("<p>");
        logReportText.append(description);
        logReportText.append("</p>");
        logReportText.append("<span class=\"dog\"></span>");
        logReportText.append("</div>");
        logReportText.append("<div class=\"clear\"></div>");
        logReportText.append("</div>");

        if (expectedResult != ExpectedResult.skipped) {
            String lineChart = String.format(
                    "{label:'%s',value:%s, passed:" + (expectedResult == ExpectedResult.passed) + "},",
                    title.replace("'", "&quot;"), String.valueOf(Math.round(roundOff)));
            lineChartData.append(lineChart);
        }
    }


    private String getExecutionTimeFormatted(double seconds) {
        int numberOfDays;
        int numberOfHours;
        int numberOfMinutes;
        int numberOfSeconds;

        if (seconds > (60 * 60 * 24)) // days
        {
            numberOfDays = (int) seconds / 86400;
            numberOfHours = ((int) seconds % 86400) / 3600;
            numberOfMinutes = (((int) seconds % 86400) % 3600) / 60;
            numberOfSeconds = (((int) seconds % 86400) % 3600) % 60;
            return numberOfDays + " Days, " + numberOfHours + " Hours, " + numberOfMinutes + " Minutes";
        } else if (seconds > (60 * 60)) // hours
        {
            numberOfHours = ((int) seconds % 86400) / 3600;
            numberOfMinutes = (((int) seconds % 86400) % 3600) / 60;
            numberOfSeconds = (((int) seconds % 86400) % 3600) % 60;
            return numberOfHours + " Hours, " + numberOfMinutes + " Minutes";
        } else if (seconds > 60) // minutes
        {
            numberOfMinutes = (((int) seconds % 86400) % 3600) / 60;
            numberOfSeconds = (((int) seconds % 86400) % 3600) % 60;
            return numberOfMinutes + " Minutes, " + numberOfSeconds + " Seconds";
        } else {
            String formattedSeconds = new DecimalFormat("#").format(seconds);
            return formattedSeconds + " Seconds";
        }
    }



    public void beforeScenario(String scenarioTitle) {
        currentScenario = StringEscapeUtils.escapeHtml4(scenarioTitle);
        stepsBuilder.setLength(0);
    }


    public void beforeStep(String step) {

        step = StringEscapeUtils.escapeHtml4(removeSpecialCharacters(step)) + "<br />";
        step = recogniseKeyWords(step);
        stepsBuilder.append(step);
        getCurrentStep=step;

    }
    private String removeSpecialCharacters(String text) {
        return text.replace("｟", "<").replace("｠", ">").replace("［", "").replace("］", "");
    }
    private String recogniseKeyWords(String step) {
        step = step.replaceFirst("Given", "<b class=\"keyword\">Given</b>");
        step = step.replaceFirst("When", "<b class=\"keyword\">When</b>");
        step = step.replaceFirst("Then", "<b class=\"keyword\">Then</b>");
        step = step.replaceFirst("And", "<b class=\"keyword\">And</b>");

        return step;
    }

    // step status
    public void successful(String step) {

        successful = ExpectedResult.passed;
    }

    public void pending(String step) {
        step = StringEscapeUtils.escapeHtml4(removeSpecialCharacters(step));
        step = recogniseKeyWords(step);
        stepsBuilder.append(step + " ... <b class=\"red\">(PENDING)</b><br />");
        successful = ExpectedResult.failed;
    }

    public void notPerformed(String step) {
        step = StringEscapeUtils.escapeHtml4(removeSpecialCharacters(step));
        step = recogniseKeyWords(step);
        stepsBuilder.append(step + " <i class=\"pink\">(NOT PERFORMED)</i><br />");
    }

    public void failed(String step, String failedCause, String screenShot)  {
        stepsBuilder.append("<b class=\"red\">" + failedCause + "</b><br />");

        stepsBuilder.append( "'<img src='data:image/png;base64, "+ utils.getImageBase64(screenShot) + "' alt='Red dot' height='400' width='400'/> <br />");
        successful = ExpectedResult.failed;

    }

    public void afterStep(String message) {
        String step=getCurrentStep;
        step=step.replace("<b class=\"keyword\">When</b> ", "(").trim();
        step=step.replace("<b class=\"keyword\">Given</b> ", "(").trim();
        step=step.replace("<b class=\"keyword\">Then</b> ", "(").trim();
        step=step.replace("<b class=\"keyword\">And</b> ", "(").trim();
        step=step.replace("<br />", ")");
        step=recogniseKeyWords(step);
//        stepsBuilder.append("<b class=\"red\">" + message + "</b><br />");
    }

    public void afterScenario() {
        testCaseContent(currentScenario, successful, stepsBuilder.toString());
    }

}