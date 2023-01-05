package wonder.runner.notification;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import wonder.model.TestClass;

/**
 * @author michelle
 */
public class Listener extends RunListener {
    public void onBeforeClass(TestClass testClass) {
    }

    public void onAfterClass(TestClass testClass) {
    }

    public void beforeGroupStarted(Description description) {
    }

    public void afterGroupStarted(Description description) {
    }

    public void fixtureFinished() {
    }

    public void fixtureFailed(Failure failure) {
    }
}
