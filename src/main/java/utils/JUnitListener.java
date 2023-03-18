package utils;


import io.appium.java_client.AppiumDriver;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class JUnitListener extends RunListener {
    private static AppiumDriver<?> driver;

    private Boolean isCatchFailed=false;



    @Override
    public void testRunStarted(Description result) throws Exception {
        System.out.println( "######### testRunStarted ########## ");

    }

    @Override
    public void testFinished(Description description) throws Exception {
        System.out.println( "######### Test Case Success ########## ");

    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        System.out.println( "######### Test Case Success ########## ");
        if (result.wasSuccessful()!=true&& isCatchFailed!=true){
            result.createListener().testFailure(result.getFailures().get(0));
        }

    }


    @Override
    public void testFailure(Failure failure) throws Exception {
        System.out.println( "######### Test Case Failed Started ########## ");

    }




    @Override
    public void testIgnored(Description description) throws Exception {
        System.out.println( "######### Test Case Ignored Started ########## ");
    }
}
