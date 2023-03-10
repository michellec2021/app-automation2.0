package wonder.runner;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wonder.model.TestClass;
import wonder.runner.notification.Listener;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

/**
 * @author michelle
 */
public class AfterGroupRunner extends Runner {
    private final Logger logger = LoggerFactory.getLogger(AfterGroupRunner.class);
    private final Statement statement;
    private final TestClass testClass;
    private final Method testMethod;
    private final Listener listener;
    private Description description;

    public AfterGroupRunner(TestClass testClass, Method testMethod, Statement statement, Listener listener) {
        this.testClass = testClass;
        this.testMethod = testMethod;
        this.statement = statement;
        this.listener = listener;
    }

    @Override
    public Description getDescription() {
        this.description = Description.createTestDescription(testClass.clazz, testMethod.getName(), testMethod.getAnnotations());
        return description;
    }

    @Override
    public void run(RunNotifier notifier) {
        listener.afterGroupStarted(description);
        try {
            logger.info("Run After Group Starts");
            statement.evaluate();
        } catch (Throwable e) {
            listener.fixtureFailed(new Failure(getDescription(), e));
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            logger.error("Run After Group Exception: " + stringWriter);
        } finally {
            listener.fixtureFinished();
        }
    }
}