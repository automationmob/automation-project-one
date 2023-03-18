package qa.runners;


import com.github.valfirst.jbehave.junit.monitoring.JUnitReportingRunner;
import com.qa.stepsdef.HelperSteps;
import com.qa.stepsdef.LifeCyclePrivate;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.embedder.StoryControls;
import org.jbehave.core.failures.FailingUponPendingStep;
import org.jbehave.core.failures.PendingStepStrategy;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.junit.JUnitStories;
import org.jbehave.core.reporters.CrossReference;
import org.jbehave.core.reporters.Format;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.InstanceStepsFactory;
import org.jbehave.web.selenium.*;
import org.junit.runner.RunWith;
import utils.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;
import static org.jbehave.core.reporters.Format.*;

/**
 * Class which provides the link between the JBehave's executor framework
 * (called Embedder) and the textual stories.
 */
@RunWith(JUnitReportingRunner.class)
public class Stories extends JUnitStories {
	public static String reportName;
	JSONManager jbhaveObjects=new JSONManager();


	Embedder embedder = null;
	PendingStepStrategy pendingStepStrategy = new FailingUponPendingStep();
	CrossReference crossReference = new CrossReference().withJsonOnly();
	ContextView contextView = new LocalFrameContextView().sized(640, 120).located(250, 30);
	SeleniumContext seleniumContext = new SeleniumContext();
	Format[] formats = new Format[] { new SeleniumContextOutput(seleniumContext), CONSOLE,JSON_TEMPLATE, Report.REPORT};
	StoryReporterBuilder reporterBuilder = null;
	public static String reportFilePath;
	public static String reportsFolderName;


	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss");
	LocalDateTime now = LocalDateTime.now();
	private static ThreadLocal<String> existPath = new ThreadLocal<String>();



	public void setExistPath(String platformName1){
		existPath.set(platformName1);
	}

	public String getExistPath(){
		return existPath.get();
	}



	@Override
	public Configuration configuration() {

		return new SeleniumConfiguration().useSeleniumContext(seleniumContext)
				.usePendingStepStrategy(pendingStepStrategy).useFailureStrategy(new FailingUponPendingStep())
				.useStoryControls(new StoryControls().doResetStateBeforeScenario(true))
				.useStoryReporterBuilder(reporterBuilder);
	}

	@Override
	public InjectableStepsFactory stepsFactory() {
		return new InstanceStepsFactory(configuration(),new LifeCyclePrivate(), new HelperSteps());
	}


	@Override
	protected List<String> storyPaths() {
		return new StoryFinder().findPaths(codeLocationFromClass(this.getClass()).getFile(),
				jbhaveObjects.getJBehaveJsonArrayAtList("storiesPath"), null);
	}

	protected void existPath(String s){
		setExistPath(s);
	}

	@Override
	public void run() {
		try {
			getEmbedder().runStoriesAsPaths(storyPaths());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			getEmbedder().generateCrossReference();
		}
	}

	
	
	
	private Embedder getEmbedder() {

		if (embedder == null) {
			embedder = configuredEmbedder();
			embedder.embedderControls().doFailOnStoryTimeout(true);
//			embedder.embedderControls().useStoryTimeouts("5000");
			embedder.embedderControls().useThreads(1);
			embedder.useMetaFilters(jbhaveObjects.getJBehaveJsonArrayAtList("meta"));
//			embedder.useExecutorService(Executors.newFixedThreadPool(3));
		}
		return embedder;

	}

	public Stories() {
		GlobalParams params=new GlobalParams();
		JSONManager jsonManager=new JSONManager();
		TestUtils utils=new TestUtils();
		params.initializeGlobalParams(jsonManager.getAndroidObjects());
		params.setRuntimeStart(utils.getDateTime());

		ServerManager serverManager=  new ServerManager();
		serverManager.startServer();

		DriverManager driverManager=new DriverManager();

		try {
			driverManager.initializeDriver();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}




		reportName = "STC-Automation-Report" + params.getRuntimeStart()+ ".html";
		Report.getInstance().setReportPathStr(reportName);
		Report.getInstance().setAppiumDriver(driverManager.getDriver());
		Report.getInstance().setReportHeader("STC Mobile (" + params.getPlatformName() + ")");
		Report.getInstance().setReportFileName(reportName);
		Report.getInstance().setParams(params);

//		ReportManager.getInstance().setReportHeader("STC Mobile (" + params.getPlatformName() + ")");
//		ReportManager.getInstance().setReportFolderName(reportsFolderName);
//		ReportManager.getInstance().setReportFileName(reportFilePath);
//		ReportManager.getInstance().setAppiumDriver(driverManager.getDriver());
//		ReportManager.getInstance().setParams(params);

		Report.getInstance().setReportPath(
				System.getProperty("user.dir") + File.separator + "Reports" + File.separator + params.getPlatformName()
						+ File.separator + "GenratedReport-" + params.getRuntimeStart());

			reporterBuilder = new StoryReporterBuilder().withFailureTrace(true).withFailureTraceCompression(true)
					.withDefaultFormats().withFormats(formats).withCrossReference(crossReference);

		JUnitReportingRunner.recommendedControls(getEmbedder());


	}

}