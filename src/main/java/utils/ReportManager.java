package utils;


import io.appium.java_client.AppiumDriver;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jbehave.core.model.*;
import org.jbehave.core.reporters.FilePrintStreamFactory;
import org.jbehave.core.reporters.Format;
import org.jbehave.core.reporters.StoryReporter;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.StepCollector;
import org.openqa.selenium.OutputType;

import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;


public class ReportManager implements StoryReporter {


	private boolean m_GenerateReportPerStory;
	private StringBuilder logReportText;
	private StringBuilder logReportTextPerStory;
	private Integer passedTestCount;
	private Integer failedTestCount;
	private Integer skippedTestCount;
	private Integer reportPassedTestCount;
	private Integer reportFailedTestCount;
	private Integer reportSkippedTestCount;
	private StringBuilder lineChartData;
	private StringBuilder lineChartData2;
	private double totalExecutionTime;
	private double reporttotalExecutionTime;
	private Map<String, String> tableRowExample;
	private ExpectedResult successful;
	private StringBuilder stepsBuilder;
	private String currentScenario;
	private String passedTestCountKey;
	private String failedTestCountKey;
	private String skippedTestCountKey;
	private String ReportNameKey;
	private long startTime;
	private String reportTitle = "";
	private String title = "";
	private String reportSubTitle = "";
	private String m_ReprotName;
	private String reportPerStoryTableData;
	private String oldContent;
	private Hashtable<String, String> statisticsTable;
	private Hashtable<String, Double> timedurationTable;
	private Hashtable<String, String> chartContentTable;


	private String m_BuildNumber;
	private String m_PlatForm;
	boolean readFromProject;
	private long executionStartTime;
	private boolean isThreading;
	private HashMap<String, Long> scenriosTime;

	private static String customReportFileName;
	private static String customReportFolderName;
	private static String platformName;

	private static String appPackage;
	private static String bundleId;

	String getCurrentStep;
	String titleHintOfAssersion="defaultColor";

	private AppiumDriver<?> driver;

	public void setAppiumDriver(AppiumDriver driver1) {
		driver=driver1;
	}

	public void setParams(GlobalParams params) {
		platformName=params.getPlatformName();
		appPackage=params.getAppPackage();
		bundleId=params.getBundleId();
	}


	private enum ExpectedResult {
		passed, failed, skipped
	}

	private static ReportManager singleton;

	public final static Format REPORT = new Format("Report") {
		@Override
		public StoryReporter createStoryReporter(FilePrintStreamFactory factory,
				StoryReporterBuilder storyReporterBuilder) {
			return singleton;
		}
	};

	public ReportManager() {
		logReportText = new StringBuilder();
		logReportTextPerStory = new StringBuilder();
		stepsBuilder = new StringBuilder();
		lineChartData = new StringBuilder();
		lineChartData2 = new StringBuilder();
		passedTestCount = 0;
		failedTestCount = 0;
		skippedTestCount = 0;
		totalExecutionTime = 0;
		reportPassedTestCount = 0;
		reportFailedTestCount = 0;
		reportSkippedTestCount = 0;
		statisticsTable = new Hashtable<String, String>();
		timedurationTable = new Hashtable<String, Double>();
		chartContentTable = new Hashtable<String, String>();
		reportPerStoryTableData = "";
		oldContent = "";
		m_BuildNumber = "";
		m_PlatForm = "";
		readFromProject = false;
		scenriosTime = new HashMap<String, Long>();
		executionStartTime = System.nanoTime();
	}

	public static ReportManager getInstance() {
		if(singleton == null) {
			synchronized (ReportManager.class) {
				singleton = new ReportManager();
			}
		}

		return singleton;
	}

	private synchronized void appendReportContent(String title, ExpectedResult expectedResult, String description) {
		String code = "P";
		if (m_GenerateReportPerStory) {
			appendReportContentPerStory(title, expectedResult, description);
		}
		if (expectedResult == ExpectedResult.passed)
			passedTestCount++;
		else if (expectedResult == ExpectedResult.skipped) {
			skippedTestCount++;
			code = "K";
		} else {
			failedTestCount++;
			code = "F";
		}

		long endTime = System.nanoTime();
		long scenarioStartTime = System.nanoTime();
		if (!isThreading) {
			scenarioStartTime = startTime;
		} else {
			scenarioStartTime = scenriosTime.get(Thread.currentThread().getName());
		}
		long duration = endTime - scenarioStartTime;
		double seconds = (double) duration / 1000000000.0;
		double roundOff = Math.round(seconds * 100.0) / 100.0;
		totalExecutionTime = Math.round(((double) (endTime - executionStartTime) / 1000000000.0) * 100.0) / 100.0;
		logReportText.append("<div class=\"test-case " + expectedResult.toString() + "\">");
		logReportText.append("<div class=\"icon\">" + code + "</div>");
		logReportText.append("<div class=\"body\">");
		logReportText.append(changeTitle(title));
		logReportText.append("<span>" + String.valueOf(Math.round(roundOff)) + " seconds)</span>");
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

	/**
	 * Overloaded method for report per story, the code will reset all variables
	 * after each story.
	 * 
	 * @param title
	 * @param expectedResult
	 * @param description
	 */
	private synchronized void appendReportContentPerStory(String title, ExpectedResult expectedResult, String description) {
		String code = "P";
		if (expectedResult == ExpectedResult.passed) {
			reportPassedTestCount++;
		} else if (expectedResult == ExpectedResult.skipped) {
			reportSkippedTestCount++;
			code = "K";
		} else {
			reportFailedTestCount++;
			code = "F";
		}

		if (chartContentTable.get(m_ReprotName) != null) {
			lineChartData2 = new StringBuilder().append(chartContentTable.get(m_ReprotName));
		}

		long endTime = System.nanoTime();
		long duration = endTime - startTime;
		double seconds = (double) duration / 1000000000.0;
		double roundOff = Math.round(seconds * 100.0) / 100.0;

		reporttotalExecutionTime += roundOff;

		logReportTextPerStory.append("<div class=\"test-case " + expectedResult.toString() + "\">");
		logReportTextPerStory.append("<div class=\"icon\">" + code + "</div>");
		logReportTextPerStory.append("<div class=\"body\">");
		logReportTextPerStory.append("<h6>" + title + "</h6>");
		logReportTextPerStory.append("<span>" + String.valueOf(Math.round(roundOff)) + " seconds)</span>");
		logReportTextPerStory.append("<p>");
		logReportTextPerStory.append(description);
		logReportTextPerStory.append("</p>");
		logReportTextPerStory.append("<span class=\"dog\"></span>");
		logReportTextPerStory.append("</div>");
		logReportTextPerStory.append("<div class=\"clear\"></div>");
		logReportTextPerStory.append("</div>");

		if (expectedResult != ExpectedResult.skipped) {
			String lineChart = String.format(
					"{label:'%s',value:%s, passed:" + (expectedResult == ExpectedResult.passed) + "},",
					title.replace("'", "&quot;"), String.valueOf(Math.round(roundOff)));
			lineChartData2.append(lineChart);
		}
		chartContentTable.put(m_ReprotName, lineChartData2.toString());
	}

	/**
	 * GenerateReprotPerStory(): this method will be used to generate report per
	 * story as below: A- if the meta for Report Name is not null. B- if the feature
	 * is enabled.
	 */


	private synchronized void writeReport() {
		Integer totalTestCount = passedTestCount + failedTestCount + skippedTestCount;
		DecimalFormat twoDecimals = new DecimalFormat("#0.00");

		double passedPercent = (double) passedTestCount / (double) totalTestCount;
		double failedPercent = (double) failedTestCount / (double) totalTestCount;
		double skippedPercent = (double) skippedTestCount / (double) totalTestCount;

		StringBuilder pieChartData = new StringBuilder();
		pieChartData.append("{title:\"Passed\",value:" + twoDecimals.format(passedPercent) + "},");
		pieChartData.append("{title:\"Failed\",value:" + twoDecimals.format(failedPercent) + "},");
		pieChartData.append("{title:\"Skipped\",value:" + twoDecimals.format(skippedPercent) + "}");

		Date dateNow = new Date();
		SimpleDateFormat format = new SimpleDateFormat("MMMMM d, yyyy hh:mm aaa");


		File reportsFolder = new File(customReportFolderName);
		if (!reportsFolder.exists()) {
			reportsFolder.mkdirs();
		}

		//This a initialization
		String templateFileName = platformName + "Template.html";

		String totalExecutionTimeHeader = String.format("total %s", getExecutionTimeFormatted(totalExecutionTime));
		try {

			// URL url = Resources.getResource(templateFileName);
			// String content = Resources.toString(url, Charsets.UTF_8);
			String path = System.getProperty("user.dir") + File.separator;
			BufferedReader br = new BufferedReader(new FileReader(path + templateFileName));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			String content = sb.toString();

			br.close();
			content = content.replace("@@@totalExecutionTimeHeader@@@", totalExecutionTimeHeader);
			content = content.replace("@@@DeviceName@@@", "Android");
			content = content.replace("@@@BuildName@@@", "latest-Version");
			content = content.replace("@@@TestCases@@@", logReportText.toString());
			content = content.replace("@@@pieChart@@@", pieChartData.toString());
			content = content.replace("@@@lineChart@@@", lineChartData.toString());
			content = content.replace("@@@ReportTitle@@@", reportTitle);
			content = content.replace("@@@build_number@@@", m_BuildNumber);
			content = content.replace("@@@platform@@@", m_PlatForm);
			content = content.replace("@@@reportSubTitle@@@", reportSubTitle);
			content = content.replace("@@@Title@@@", title);
			content = content.replace("@@@PaasedCount@@@", passedTestCount.toString());
			content = content.replace("@@@FailedCount@@@", failedTestCount.toString());
			content = content.replace("@@@SkippedCount@@@", skippedTestCount.toString());
			content = content.replace("@@@date@@@", format.format(dateNow).toUpperCase());
			if (reportPerStoryTableData != null && !reportPerStoryTableData.isEmpty()) {
				content = content.replaceAll("@@@data@@@", bindTabletHeader() + reportPerStoryTableData + "</table>");
			} else {
				content = content.replaceAll("@@@data@@@", "");
			}

			Writer writer;
			writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(customReportFolderName + customReportFileName), "UTF8"));

			writer.append(content);
			writer.flush();
			writer.close();

		} catch (Exception e) {

			e.printStackTrace();
		}
	}


	/**
	 * modifyReportPerStoryData(): this method will help to: 1- add and increment
	 * the passed, skipped and failed count for each single report. 2- Remove the
	 * old row in HTML table in case of modification. 3- set the counters before
	 * write them.
	 * 
	 * @param fullReportName
	 */
	private void modifyReportPerStoryData(String fullReportName) {
		Integer passedCount = 0;
		Integer failedCount = 0;
		Integer skippedCount = 0;

		boolean appendReportContent = statisticsTable.get(ReportNameKey) == null ? true : false;

		if (appendReportContent) {
			statisticsTable.put(ReportNameKey, fullReportName);

			statisticsTable.put(passedTestCountKey, Integer.toString(reportPassedTestCount));

			statisticsTable.put(failedTestCountKey, Integer.toString(reportFailedTestCount));

			statisticsTable.put(skippedTestCountKey, Integer.toString(reportSkippedTestCount));

			statisticsTable.put(m_ReprotName, logReportTextPerStory.toString());
			bindReportStatistics();
		} else if (statisticsTable.get(ReportNameKey).equalsIgnoreCase(fullReportName)) {
			passedCount = Integer.parseInt(statisticsTable.get(passedTestCountKey));

			failedCount = Integer.parseInt(statisticsTable.get(failedTestCountKey));

			skippedCount = Integer.parseInt(statisticsTable.get(skippedTestCountKey));

			String reportContent = statisticsTable.get(m_ReprotName);
			reportContent += logReportTextPerStory.toString();
			oldContent = "";
			oldContent = getOldReportData(statisticsTable.get(passedTestCountKey),
					statisticsTable.get(failedTestCountKey), statisticsTable.get(skippedTestCountKey));

			passedCount += reportPassedTestCount;
			failedCount += reportFailedTestCount;
			skippedCount += reportSkippedTestCount;

			statisticsTable.put(passedTestCountKey, Integer.toString(passedCount));

			statisticsTable.put(failedTestCountKey, Integer.toString(failedCount));

			statisticsTable.put(skippedTestCountKey, Integer.toString(skippedCount));

			statisticsTable.put(m_ReprotName, reportContent);
			bindReportStatistics();
		} else {
			statisticsTable.put(ReportNameKey, fullReportName);
			statisticsTable.put(passedTestCountKey, Integer.toString(reportPassedTestCount));
			statisticsTable.put(failedTestCountKey, Integer.toString(reportFailedTestCount));
			statisticsTable.put(skippedTestCountKey, Integer.toString(reportSkippedTestCount));

			statisticsTable.put(m_ReprotName, logReportTextPerStory.toString());
			bindReportStatistics();
		}
		reportPassedTestCount = passedCount == 0 ? reportPassedTestCount : passedCount;
		reportFailedTestCount = failedCount == 0 ? reportFailedTestCount : failedCount;
		reportSkippedTestCount = failedCount == 0 ? reportSkippedTestCount : skippedCount;
	}

	/**
	 * BindReportStatistics() this method aims to generate HTML table body. The
	 * table row will be generated dynamically based on the reports data (passed
	 * failed count) If there is a previous data for the same report (same meta tag)
	 * the code will remove it, considering incrementing the removed data.
	 */
	private synchronized void bindReportStatistics() {
		String reportNameLinkHref = "<a href={0}>{1}</a>";
		reportNameLinkHref = reportNameLinkHref.replace("{0}", statisticsTable.get(ReportNameKey)).replace("{1}",
				statisticsTable.get(ReportNameKey));

		StringBuilder reportsData = new StringBuilder();
		reportsData.append("<tr>");
		reportsData.append("<td>");
		reportsData.append(reportNameLinkHref);
		reportsData.append("</td>");
		reportsData.append("<td>");
		reportsData.append(statisticsTable.get(passedTestCountKey));
		reportsData.append("</td>");
		reportsData.append("<td>");
		reportsData.append(statisticsTable.get(failedTestCountKey));
		reportsData.append("</td>");
		reportsData.append("<td>");
		reportsData.append(statisticsTable.get(skippedTestCountKey));
		reportsData.append("</td>");
		reportsData.append("</tr>");
		reportPerStoryTableData += reportsData.toString();
		reportPerStoryTableData = reportPerStoryTableData.replace(oldContent, "");
	}

	/**
	 * getOldReportData: This method will be called only if we need to remove row
	 * from the HTML table this step will help to avoid duplicate content.
	 * 
	 * @paramPassedCount
	 * @paramfailedCount
	 * @paramskippedCount
	 * @return
	 */
	private synchronized String getOldReportData(String passedCount, String failedCount, String skippedCount) {
		String reportNameLinkHref = "<a href={0}>{1}</a>";
		reportNameLinkHref = reportNameLinkHref.replace("{0}", statisticsTable.get(ReportNameKey)).replace("{1}",
				statisticsTable.get(ReportNameKey));
		StringBuilder reportPreviousRow = new StringBuilder();
		reportPreviousRow.append("<tr>");
		reportPreviousRow.append("<td>");
		reportPreviousRow.append(reportNameLinkHref);
		reportPreviousRow.append("</td>");
		reportPreviousRow.append("<td>");
		reportPreviousRow.append(passedCount);
		reportPreviousRow.append("</td>");
		reportPreviousRow.append("<td>");
		reportPreviousRow.append(failedCount);
		reportPreviousRow.append("</td>");
		reportPreviousRow.append("<td>");
		reportPreviousRow.append(skippedCount);
		reportPreviousRow.append("</td>");
		reportPreviousRow.append("</tr>");
		return reportPreviousRow.toString();
	}

	/**
	 * BindTabletHeader(): This method aims to generate the HTML table header, this
	 * will help to avoid updating the generic template.
	 * 
	 * @return table header
	 */
	private synchronized String bindTabletHeader() {
		StringBuilder tableHeaderData = new StringBuilder();
		tableHeaderData.append(" <table class=\"responstable\">");
		tableHeaderData.append("<tr>");
		tableHeaderData.append("<th>");
		tableHeaderData.append("Report Name:");
		tableHeaderData.append("</th>");
		tableHeaderData.append("<th data-th=\"Driver details\"><span>");
		tableHeaderData.append("Test cases passed:");
		tableHeaderData.append("</span></th>");
		tableHeaderData.append("<th>");
		tableHeaderData.append("Test cases failed:");
		tableHeaderData.append("</th>");
		tableHeaderData.append("<th>");
		tableHeaderData.append("Test cases skipped:");
		tableHeaderData.append("</th>");
		tableHeaderData.append("</tr>");
		return tableHeaderData.toString();
	}

	/**
	 * Reset(): this method aims to reset the variables as we are generating report
	 * per story, old data already saved in map tables.
	 */
	public synchronized void Reset() {
		reportPassedTestCount = 0;
		reportFailedTestCount = 0;
		reportSkippedTestCount = 0;
		reporttotalExecutionTime = 0;
		logReportTextPerStory.delete(0, logReportTextPerStory.length());
		lineChartData2.setLength(0);
	}

	/**
	 * setDuationTime(): this method aims check if set execution time for the
	 * report. if the report name is a new report name, then will add, otherwise
	 * will increment the current value.
	 * 
	 * @param reportName
	 */
	private void setDuationTime(String reportName) {
		if (timedurationTable.get(reportName) != null) {
			double acummDuration = timedurationTable.get(reportName);
			acummDuration += reporttotalExecutionTime;
			timedurationTable.put(reportName, acummDuration);
		} else {
			timedurationTable.put(reportName, reporttotalExecutionTime);
		}
	}

	

	private static String getExecutionTimeFormatted(double seconds) {
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

	public void setReportHeader(String title) {
		reportTitle = title;
	}

	/**
	 * This method will be used as flag whether generating report per story feature
	 * is enabled or not.
	 * 
	 * @param generateReprotPerStory
	 */
	public void generateReportsPerStory(boolean generateReprotPerStory) {
		m_GenerateReportPerStory = generateReprotPerStory;
	}

	public void getBuildNumberValueFromStories(String buildNumber) {
		m_BuildNumber = buildNumber;
	}

	public void getReportTitle(String reporTitle) {
		reportTitle = reporTitle;
	}

	public void getTitle(String headReportTitle) {
		title = headReportTitle;
	}

	public void getReportSubTitle(String reporSubTitle) {
		reportSubTitle = reporSubTitle;
	}

	public void getPlatFormValueFromStories(String platForm) {
		m_PlatForm = platForm;
	}

	public void setReportSubHeader(String title) {
		reportSubTitle = title;
	}

	/**
	 * Set a custom report file name for the generated reports.
	 * 
	 * By default, the file name will be in the following format
	 * "report-MMddhhmmss.html"
	 * 
	 * @paramcustomReportFileName
	 *            custom name for generated reports.
	 */
	public synchronized void setReportFileName(String customReportFileName1) {
		customReportFileName=customReportFileName1;
	}

	public synchronized void setReportFolderName(String customReportFileName1){
		customReportFolderName=customReportFileName1;
	}



	public String getCurrentScenario() {
		return currentScenario;
	}

	private String recogniseKeyWords(String step) {
		step = step.replaceFirst("Given", "<b class=\"keyword\">Given</b>");
		step = step.replaceFirst("When", "<b class=\"keyword\">When</b>");
		step = step.replaceFirst("Then", "<b class=\"keyword\">Then</b>");
		step = step.replaceFirst("And", "<b class=\"keyword\">And</b>");

		return step;
	}

	private String removeSpecialCharacters(String text) {
		return text.replace("｟", "<").replace("｠", ">").replace("［", "").replace("］", "");
	}

	public void append(String text) {
		stepsBuilder.append("<span class=\"gray\">" + text + "</span><br />");
	}

	// / StoryReporter implementation

	public void storyNotAllowed(Story story, String filter) {

	}

	public void storyCancelled(Story story, StoryDuration storyDuration) {

	}

	public synchronized void beforeStory(Story story, boolean givenStory) {
		// check if the featured is enabled, if so, read meta for ReportName.

		if (m_GenerateReportPerStory) {
			m_ReprotName = story.getMeta().getProperty("ReportName").replace(" ", "-");
		}
	}

	public void afterStory(boolean givenStory) {
		writeReport();
	}

	public void narrative(Narrative narrative) {

	}

	public void lifecyle(Lifecycle lifecycle) {
	}

	@Override
	public void beforeStorySteps(StepCollector.Stage stage) {

	}

	@Override
	public synchronized void afterStorySteps(StepCollector.Stage stage) {
		System.out.println("afterStorySteps");

	}

	@Override
	public void beforeScenarioSteps(StepCollector.Stage stage) {

	}

	@Override
	public void afterScenarioSteps(StepCollector.Stage stage) {

	}

	public void scenarioNotAllowed(Scenario scenario, String filter) {

	}

	@Override
	public void beforeScenario(Scenario scenario) {

	}

	public synchronized void beforeScenario(String scenarioTitle) {
		currentScenario = StringEscapeUtils.escapeHtml4(scenarioTitle);
		stepsBuilder.setLength(0);
		if (!isThreading) {
			this.startTime = System.nanoTime();
		}
	}

	public void scenarioMeta(Meta meta) {

	}

	public synchronized void afterScenario() {
		appendReportContent(currentScenario, successful, stepsBuilder.toString());
	}

	@Override
	public void beforeGivenStories() {

	}

	public void givenStories(GivenStories givenStories) {

	}

	public void givenStories(List<String> storyPaths) {

	}

	@Override
	public void afterGivenStories() {

	}

	public synchronized void beforeExamples(List<String> steps, ExamplesTable table) {

	}

	public synchronized void example(Map<String, String> tableRow) {
		tableRowExample = tableRow;
		stepsBuilder.append("<br />");
	}

	@Override
	public synchronized void example(Map<String, String> tableRow, int exampleIndex) {
		int fixIndex=0;
		if (exampleIndex>fixIndex) {
			if(platformName.equalsIgnoreCase("iOS")){
				driver.terminateApp(bundleId);
				driver.activateApp(bundleId);
			}else{
			driver.closeApp();
			driver.activateApp(appPackage);
			}
		}
	}

	public void afterExamples() {

	}



	public synchronized void beforeStep(String step) {

		if (tableRowExample != null) {
			for (String example : tableRowExample.keySet()) {
				step = step.replaceAll("<" + example + ">",
						"<" + Matcher.quoteReplacement(tableRowExample.get(example)) + ">");
			}
		}
		step = StringEscapeUtils.escapeHtml4(removeSpecialCharacters(step)) + "<br />";
		step = recogniseKeyWords(step);
		stepsBuilder.append(step);
		getCurrentStep=step;
	}

	public synchronized void successful(String step) {

		successful = ExpectedResult.passed;
	}

	public void ignorable(String step) {

	}

	@Override
	public void comment(String step) {

	}

	public synchronized void pending(String step) {
		step = StringEscapeUtils.escapeHtml4(removeSpecialCharacters(step));
		step = recogniseKeyWords(step);
		stepsBuilder.append(step + " ... <b class=\"red\">(PENDING)</b><br />");
		successful = ExpectedResult.skipped;
	}

	public synchronized void notPerformed(String step) {
		step = StringEscapeUtils.escapeHtml4(removeSpecialCharacters(step));
		step = recogniseKeyWords(step);
		stepsBuilder.append(step + " <i class=\"pink\">(NOT PERFORMED)</i><br />");
	}

	@Override
	public synchronized void failed(String step, Throwable cause)  {
		String causeMessage = StringEscapeUtils.escapeHtml4(cause.getCause().getMessage());
		stepsBuilder.append("<b class=\"red\">" + causeMessage + "</b><br />");
		stepsBuilder.append( "'<img src='data:image/png;base64, "+ driver.getScreenshotAs(OutputType.BASE64)+ "' alt='Red dot' height='400' width='400'/> <br />");
		successful = ExpectedResult.failed;

	}
    @Override
	public void failedOutcomes(String step, OutcomesTable table) {

	}
	@Override
	public void restarted(String step, Throwable cause) {

	}

	@Override
	public void dryRun() {

	}

	@Override
	public void pendingMethods(List<String> methods) {

	}

	public synchronized void setStartTimeBeforeScenario() {
		scenriosTime.put(Thread.currentThread().getName(), System.nanoTime());
	}

	public synchronized void setThreadingEnabled() {
		isThreading = true;
	}

	@Override
	public void restartedStory(Story story, Throwable cause) {

	}


	public void skipScenariosList(ArrayList<Scenario> skipScenarios) {

	}

	public void writeSkipScenarios() {

	}

	
	
	private String changeTitle(String titleHint) {
		
		switch (titleHintOfAssersion) {
		case "red":
			titleHintOfAssersion="defaultColor";
			return "<h6> <b class=\"orange\">" + titleHint + " </b></h6>";

		default:
			return "<h6>" + titleHint + "</h6>";
		}
		
	}
		
	
}