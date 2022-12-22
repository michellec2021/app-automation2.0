package wonder.runner;

import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;
import wonder.exception.AfterTestException;
import wonder.model.TestClass;
import wonder.utils.MethodUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class RunAfters extends Statement {
    private final Statement next;
    private final Method after;
    private final TestClass testClass;

    public RunAfters(TestClass testClass, Statement next, Method after) {
        this.testClass = testClass;
        this.next = next;
        this.after = after;
    }

    @Override
    public void evaluate() throws Exception {
        List<Throwable> errors = new ArrayList<>();
        try {
            next.evaluate();
        } catch (Throwable e) {
            errors.add(e);
        } finally {
            runAfter(after, testClass.getClassInstance(), errors);
        }
        MultipleFailureException.assertEmpty(errors);
    }

    private void runAfter(Method after, Object target, List<Throwable> errors) {
        try {
            MethodUtils.invokeExplosively(after, target);
        } catch (Throwable e) {
            errors.add(new AfterTestException(e));
        }
    }
}