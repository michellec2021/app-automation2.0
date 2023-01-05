package wonder.model;

import java.lang.reflect.Method;

/**
 * @author michelle
 */
public class TestClass {
    public final Class<?> clazz;
    public final TestClassResult result;

    public TestClass(Class<?> clazz, TestClassResult result) {
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
