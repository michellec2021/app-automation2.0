package wonder.runner;

import com.microsoft.appcenter.appium.Factory;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wonder.exception.BeforeGroupException;
import wonder.exception.BeforeTestException;
import wonder.model.TestClass;
import wonder.model.TestResult;
import wonder.runner.notifier.EachTestNotifier;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

/**
 * @author michelle
 */
public class TestCaseRunner extends Runner {
    private final Logger logger = LoggerFactory.getLogger(TestCaseRunner.class);
    private Statement statement;
    private final TestClass testClass;
    private final Method testMethod;
    private Description description;

    public TestCaseRunner(TestClass testClass, Method testMethod, Statement statement) {
        this.testClass = testClass;
        this.testMethod = testMethod;
        this.statement = statement;
    }

    @Override
    public Description getDescription() {
        this.description = Description.createTestDescription(testClass.clazz, testMethod.getName(), testMethod.getAnnotations());
        return description;
    }

    @Override
    public void run(RunNotifier notifier) {
        EachTestNotifier eachNotifier = new EachTestNotifier(notifier, description);
        eachNotifier.fireTestStarted();
        if (testClass.result.getBeforeGroupResult() == TestResult.FAILED) {
            eachNotifier.addFailure(new BeforeGroupException("failed case since before group exception"));
            eachNotifier.fireTestFinished();
            return;
        }
        if (testClass.result.getBeforeTestResult() == TestResult.FAILED) {
            eachNotifier.addFailure(new BeforeTestException("failed case since before test exception"));
            eachNotifier.fireTestFinished();
            return;
        }
        try {
            logger.info("Run Test Starts");
            statement = Factory.createWatcher().apply(statement, description);
            statement.evaluate();
        } catch (Throwable e) {
            eachNotifier.addFailure(e);
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error("Run Test Case Failed: " + stringWriter);
        } finally {
            eachNotifier.fireTestFinished();
        }
    }
}
