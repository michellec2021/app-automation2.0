package wonder.model;

import org.junit.runner.JUnitCore;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.lang.reflect.Method;

/**
 * @author michelle
 */
public class TestClass {
    public final Class<?> clazz;
    public final ClassTestResult result;

    public TestClass(Class<?> clazz, ClassTestResult result) {
        this.clazz = clazz;
        this.result = result;
    }

    public String getClassName() {
        return this.clazz.getSimpleName();
    }

    public Object getClassInstance() {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Method getMethod(String methodName) {
        try {
            return clazz.getMethod(methodName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
