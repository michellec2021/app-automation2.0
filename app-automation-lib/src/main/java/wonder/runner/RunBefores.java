package wonder.runner;

import org.junit.runners.model.Statement;
import wonder.model.ClassTestResult;
import wonder.model.TestClass;
import wonder.model.TestResult;
import wonder.utils.MethodUtils;

import java.lang.reflect.Method;

public class RunBefores extends Statement {
    private final Statement next;
    private final Method before;
    private final TestClass testClass;

    public RunBefores(TestClass testClass, Statement next, Method before) {
        this.next = next;
        this.before = before;
        this.testClass = testClass;
    }

    @Override
    public void evaluate() throws Throwable {
        try {
            MethodUtils.invokeExplosively(before, testClass.getClassInstance());
        } catch (Throwable e) {
            testClass.result.setBeforeTestResult(TestResult.FAILED);
            throw e;
        }
        next.evaluate();
    }
}