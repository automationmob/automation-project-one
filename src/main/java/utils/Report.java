package utils;

import io.appium.java_client.AppiumDriver;
import org.jbehave.core.model.*;
import org.jbehave.core.reporters.FilePrintStreamFactory;
import org.jbehave.core.reporters.Format;
import org.jbehave.core.reporters.StoryReporter;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.StepCollector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Report implements StoryReporter {


	static String screenShot;

	String platformName,appPackage,bundleId;

	String deviceName;

	AppiumDriver<?> driver;


	int counting;

	public void setAppiumDriver(AppiumDriver driver1) {
		driver=driver1;
	}

	public void setParams(GlobalParams params) {
		platformName=params.getPlatformName();
		appPackage=params.getAppPackage();
		bundleId=params.getBundleId();
		deviceName=params.getDeviceName();
	}

	public void setReportPathStr(String reportPath) {
	}





	private enum ExpectedResult {
		passed, failed, skipped
	}



	private static Report singleton = new Report();

	public final static Format REPORT = new Format("Report") {
		@Override
		public StoryReporter createStoryReporter(FilePrintStreamFactory factory,
				StoryReporterBuilder storyReporterBuilder) {
			return singleton;
		}
	};

	public Report() {

	}

	public static Report getInstance() {
		if(singleton == null) {
			synchronized (Report.class) {
				singleton = new Report();
			}
		}
		return singleton;

	}

	private void appendReportContent(String title, ExpectedResult expectedResult, String description) {

	}

	/**
	 * Overloaded method for report per story, the code will reset all variables
	 * after each story.
	 * 
	 * @param title
	 * @param expectedResult
	 * @param description
	 */
	private void appendReportContentPerStory(String title, ExpectedResult expectedResult, String description) {

	}

	/**
	 * GenerateReprotPerStory(): this method will be used to generate report per
	 * story as below: A- if the meta for Report Name is not null. B- if the feature
	 * is enabled.
	 */
	private void GenerateReprotPerStory() {

	}

	private void writeReport() {

	}

	/**
	 * overloaded method, this will be used only if generate report per story is
	 * enabled.
	 * 
	 * @param reportName
	 */
	private void writeReport(String reportName) {

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

	}

	/**
	 * BindReportStatistics() this method aims to generate HTML table body. The
	 * table row will be generated dynamically based on the reports data (passed
	 * failed count) If there is a previous data for the same report (same meta tag)
	 * the code will remove it, considering incrementing the removed data.
	 */
	private void bindReportStatistics() {

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
	private String getOldReportData(String passedCount, String failedCount, String skippedCount) {

		return null;
	}

	/**
	 * BindTabletHeader(): This method aims to generate the HTML table header, this
	 * will help to avoid updating the generic template.
	 * 
	 * @return table header
	 */
	private String bindTabletHeader() {

		return null;
	}

	/**
	 * Reset(): this method aims to reset the variables as we are generating report
	 * per story, old data already saved in map tables.
	 */
	public void Reset() {

	}

	/**
	 * setDuationTime(): this method aims check if set execution time for the
	 * report. if the report name is a new report name, then will add, otherwise
	 * will increment the current value.
	 * 
	 * @param reportName
	 */
	private void setDuationTime(String reportName) {

	}

	

	private static String getExecutionTimeFormatted(double seconds) {
	return null;
	}

	public void setReportHeader(String title) {
	}

	/**
	 * This method will be used as flag whether generating report per story feature
	 * is enabled or not.
	 * 
	 * @param generateReprotPerStory
	 */
	public void generateReportsPerStory(boolean generateReprotPerStory) {
	}

	public void getBuildNumberValueFromStories(String buildNumber) {
	}

	public void getReportTitle(String reporTitle) {
	}

	public void getTitle(String headReportTitle) {
	}

	public void getReportSubTitle(String reporSubTitle) {
	}

	public void getPlatFormValueFromStories(String platForm) {
	}

	public void setReportSubHeader(String title) {
	}

	/**
	 * Set the report generation path with a fully qualified path for the desired
	 * report folder. Missing folder will be automatically generated, a file
	 * separator ({@linkFile.separator}) will be added to the end of the string if
	 * the string does not end with one.
	 * 
	 * @param path
	 *            path where reports will be generated
	 */
	public void setReportPath(String path) {

	}

	/**
	 * Set a custom report file name for the generated reports.
	 * 
	 * By default, the file name will be in the following format
	 * "report-MMddhhmmss.html"
	 * 
	 * @param customReportFileName
	 *            custom name for generated reports.
	 */
	public void setReportFileName(String customReportFileName) {
	}

	public String getCurrentScenario() {
		return null;
	}

	private String recogniseKeyWords(String step) {
return null;
	}

	private String removeSpecialCharacters(String text) {
		return text.replace("｟", "<").replace("｠", ">").replace("［", "").replace("］", "");
	}

	public void append(String text) {
	}

	// / StoryReporter implementation

	public void storyNotAllowed(Story story, String filter) {

	}

	public void storyCancelled(Story story, StoryDuration storyDuration) {

	}

	public void beforeStory(Story story, boolean givenStory) {

	}

	public void afterStory(boolean givenStory) {
	}

	public void narrative(Narrative narrative) {

	}

	public void lifecyle(Lifecycle lifecycle) {

	}

	@Override
	public void beforeStorySteps(StepCollector.Stage stage) {

	}

	@Override
	public void afterStorySteps(StepCollector.Stage stage) {

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

	public void beforeScenario(String scenarioTitle) {

	}

	public void scenarioMeta(Meta meta) {

	}

	public void afterScenario() {
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
	public void beforeExamples(List<String> steps, ExamplesTable table) {

	}

	@Override
	public void example(Map<String, String> tableRow) {

	}


	public void example(Map tableRow, int exampleIndex) {

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

	@Override
	public void afterExamples() {

	}

	public void beforeStep(String step) {

	}

	public void successful(String step) {

	}

	public void ignorable(String step) {

	}

	public void pending(String step) {

	}

	public void notPerformed(String step) {

	}

	// getter and setter methods
	public String getScreenShot() {
		return screenShot;
	}

	public void setScreenShot(String screenShot) {
		this.screenShot = screenShot;
	}


	public void failed(String step, Throwable cause)  {


	}


	public void failedOutcomes(String step, OutcomesTable table) {

	}

	public void restarted(String step, Throwable cause) {
	}

	public void dryRun() {

	}

	public void pendingMethods(List<String> methods) {

	}

	public void setStartTimeBeforeScenario() {


	}

	public void setThreadingEnabled() {
	}

	public void restartedStory(Story story, Throwable cause) {

	}

	public void skipScenariosList(ArrayList<Scenario> skipScenarios) {

	}

	public void writeSkipScenarios() {

	}

	public void comment(String arg0) {
		// TODO Auto-generated method stub

	}

	public static void openReport() {
		

		}

	public void afterStep(String message) {

	}
	
	
	private String changeTitle(String titleHint) {
		
	return null;
		
	}
		
	
}