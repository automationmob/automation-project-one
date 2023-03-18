package utils;

import com.github.valfirst.jbehave.junit.monitoring.JUnitDescriptionGenerator;
import com.github.valfirst.jbehave.junit.monitoring.JUnitScenarioReporter;
import com.github.valfirst.jbehave.junit.monitoring.StoryPathsExtractor;
import org.jbehave.core.ConfigurableEmbedder;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.embedder.EmbedderControls;
import org.jbehave.core.embedder.PerformableTree;
import org.jbehave.core.failures.BatchFailures;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.CandidateSteps;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.NullStepMonitor;
import org.jbehave.core.steps.StepMonitor;
import org.junit.AssumptionViolatedException;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import java.util.ArrayList;
import java.util.List;

public class MyTestRunner extends BlockJUnit4ClassRunner {
    private Embedder configuredEmbedder;
    private Configuration configuration;
    private int numberOfTestCases;
    private Description rootDescription;
    private ConfigurableEmbedder configurableEmbedder;

    public MyTestRunner(Class<ConfigurableEmbedder> clazz) throws InitializationError, ReflectiveOperationException {
        super(clazz);

        configurableEmbedder = clazz.newInstance();
        configuredEmbedder = configurableEmbedder.configuredEmbedder();
        configuration = configuredEmbedder.configuration();

        List<String> storyPaths = new StoryPathsExtractor(configurableEmbedder).getStoryPaths();

        StepMonitor originalStepMonitor = configuration.stepMonitor();
        configuration.useStepMonitor(new NullStepMonitor());
        List<Description> storyDescriptions = buildDescriptionFromStories(storyPaths);
        configuration.useStepMonitor(originalStepMonitor);

        rootDescription = Description.createSuiteDescription(clazz);
        for (Description storyDescription : storyDescriptions) {
            rootDescription.addChild(storyDescription);
        }
    }


    @Override
    public void run(RunNotifier notifier) {
        System.out.println("Executing run()");
        //Add Listener. This will register our JUnit Listener.
        notifier.addListener(new JUnitListener());
        //Get each test notifier
        EachTestNotifier testNotifier = new EachTestNotifier(notifier,
                getDescription());
        try {
            //In order capture testRunStarted method
            //at the very beginning of the test run, we will add below code.
            //Invoke here the run testRunStarted() method
            notifier.fireTestRunStarted(getDescription());
            Statement statement = classBlock(notifier);
            statement.evaluate();
        }
        catch (AssumptionViolatedException e) {
            testNotifier.fireTestIgnored();
        }
        catch (StoppedByUserException e) {
            throw e;
        }
        catch (Throwable e) {
            testNotifier.addFailure(e);
        }
    }


    @Override
    public Description getDescription() {
        return rootDescription;
    }

    @Override
    public int testCount() {
        return numberOfTestCases;
    }

    @Override
    protected Statement childrenInvoker(RunNotifier notifier) {
        return new Statement() {
            @Override
            public void evaluate() {
                JUnitScenarioReporter junitReporter = new JUnitScenarioReporter(
                        notifier, numberOfTestCases, rootDescription, configuration.keywords());
                // tell the reporter how to handle pending steps
                junitReporter.usePendingStepStrategy(configuration
                        .pendingStepStrategy());

                addToStoryReporterFormats(junitReporter);

                configurableEmbedder.run();
            }
        };
    }

    private void addToStoryReporterFormats(JUnitScenarioReporter junitReporter) {
        StoryReporterBuilder storyReporterBuilder = configuration
                .storyReporterBuilder();
        StoryReporterBuilder.ProvidedFormat junitReportFormat = new StoryReporterBuilder.ProvidedFormat(
                junitReporter);
        storyReporterBuilder.withFormats(junitReportFormat);
    }

    private List<Description> buildDescriptionFromStories(List<String> storyPaths) {
        List<CandidateSteps> candidateSteps = getCandidateSteps();
        JUnitDescriptionGenerator descriptionGenerator = new JUnitDescriptionGenerator(candidateSteps, configuration);
        List<Description> storyDescriptions = new ArrayList<>();

        addSuite(storyDescriptions, "BeforeStories");
        PerformableTree performableTree = createPerformableTree(candidateSteps, storyPaths);
        storyDescriptions.addAll(descriptionGenerator.createDescriptionFrom(performableTree));
        addSuite(storyDescriptions, "AfterStories");

        numberOfTestCases += descriptionGenerator.getTestCases();

        return storyDescriptions;
    }

    private List<CandidateSteps> getCandidateSteps() {
        List<CandidateSteps> candidateSteps;
        InjectableStepsFactory stepsFactory = configurableEmbedder.stepsFactory();
        if (stepsFactory != null) {
            candidateSteps = stepsFactory.createCandidateSteps();
        } else {
            candidateSteps = configuredEmbedder.candidateSteps();
            if (candidateSteps == null || candidateSteps.isEmpty()) {
                candidateSteps = configuredEmbedder.stepsFactory().createCandidateSteps();
            }
        }
        return candidateSteps;
    }

    private void addSuite(List<Description> storyDescriptions, String name) {
        storyDescriptions.add(Description.createTestDescription(Object.class,
                name));
        numberOfTestCases++;
    }

    private PerformableTree createPerformableTree(List<CandidateSteps> candidateSteps, List<String> storyPaths) {
        BatchFailures failures = new BatchFailures(configuredEmbedder.embedderControls().verboseFailures());
        PerformableTree performableTree = configuredEmbedder.performableTree();
        PerformableTree.RunContext context = performableTree.newRunContext(configuration, candidateSteps,
                configuredEmbedder.embedderMonitor(), configuredEmbedder.metaFilter(), failures);
        performableTree.addStories(context, configuredEmbedder.storyManager().storiesOfPaths(storyPaths));
        return performableTree;
    }

    public static EmbedderControls recommendedControls(Embedder embedder) {
        return embedder.embedderControls()
                // don't throw an exception on generating reports for failing stories
                .doIgnoreFailureInView(true)
                // don't throw an exception when a story failed
                .doIgnoreFailureInStories(true)
                // .doVerboseFailures(true)
                .useThreads(1);
    }
}


