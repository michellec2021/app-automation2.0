package wonder.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author michelle
 */
public class MethodUtils {
    public static Object invokeExplosively(Method method, Object target, final Object... params) throws Throwable {
        try {
            return method.invoke(target, params);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }
}
