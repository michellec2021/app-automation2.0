package wonder.runner;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wonder.model.TestClass;
import wonder.model.TestResult;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

/**
 * @author michelle
 */
public class BeforeGroupRunner extends Runner {
    private final Logger logger = LoggerFactory.getLogger(BeforeGroupRunner.class);
    private final Statement statement;
    private final TestClass testClass;
    private final Method testMethod;
    private Description description;

    public BeforeGroupRunner(TestClass testClass, Method testMethod, Statement statement) {
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
        notifier.fireBeforeGroupStarted(description);
        try {
            logger.info("Run Before Group Starts");
            statement.evaluate();
        } catch (Throwable e) {
            testClass.result.setBeforeGroupResult(TestResult.FAILED);
            notifier.fireFixtureFailed(new Failure(description, e));
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error("Run Before Group Exception: " + stringWriter);
        } finally {
            notifier.fireFixtureFinished();
        }
    }
}
