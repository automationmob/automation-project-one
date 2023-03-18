package qa.runners;



import qa.stepsdef.HelperSteps;
import qa.stepsdef.LifeCyclePrivate;
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
import java.util.List;

import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;
import static org.jbehave.core.reporters.Format.*;

/**
 * Class which provides the link between the JBehave's executor framework
 * (called Embedder) and the textual stories.
 */
@RunWith(MyTestRunner.class)
public class PrivateStories extends JUnitStories {
	JSONManager jbhaveObjects=new JSONManager();
	public static String reportName;


	Embedder embedder = null;
	PendingStepStrategy pendingStepStrategy = new FailingUponPendingStep();
	CrossReference crossReference = new CrossReference().withJsonOnly();
	SeleniumContext seleniumContext = new SeleniumContext();
	Format[] formats = new Format[] { new SeleniumContextOutput(seleniumContext), CONSOLE,STATS,JSON_TEMPLATE,Report.REPORT};
	StoryReporterBuilder reporterBuilder = null;
	public static String reportFilePath;
	public static String reportsFolderName;

	private static ThreadLocal<String> existPath = new ThreadLocal<String>();


	public void setExistPath(String existPath1){
		existPath.set(existPath1);
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
				getExistPath(), null);
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
			embedder.embedderControls().useStoryTimeoutInSecs(5000);
			embedder.embedderControls().useThreads(1);
			embedder.useMetaFilters(jbhaveObjects.getJBehaveJsonArrayAtList("meta"));
		}
		return embedder;

	}




	public PrivateStories(String storiesPath, DriverManager driverManager, GlobalParams params) {
		setExistPath(storiesPath);

		TestUtils utils=new TestUtils();
		params.setRuntimeStart(utils.getDateTime());


		reportName = "STC-Automation-Report" + params.getRuntimeStart()+ ".html";
		Report.getInstance().setReportPathStr(reportName);
		Report.getInstance().setAppiumDriver(driverManager.getDriver());
		Report.getInstance().setReportHeader("STC Mobile (" + params.getPlatformName() + ")");
		Report.getInstance().setReportFileName(reportName);
		Report.getInstance().setParams(params);

		Report.getInstance().setReportPath(
				System.getProperty("user.dir") + File.separator + "Reports" + File.separator + params.getPlatformName()
						+ File.separator + "GenratedReport-" + params.getRuntimeStart());


		reporterBuilder = new StoryReporterBuilder()
					.withFailureTrace(true)
					.withFailureTraceCompression(true)
					.withDefaultFormats()
					.withFormats(formats)
					.withCrossReference(crossReference);

		MyTestRunner.recommendedControls(getEmbedder());
	}


}