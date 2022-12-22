package wonder.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author michelle
 */
public class AnnotationUtils {
    public static List<Method> getAnnotatedMethods(Class<?> testClass, Class<? extends Annotation> annotationClass) {
        Class<?> interfaceClass = getImplementedInterfaceClass(testClass);
        return Collections.unmodifiableList(Arrays.stream(interfaceClass.getDeclaredMethods())
                .filter(method -> !method.isSynthetic() && isAnnotated(interfaceClass, method, annotationClass))
                .collect(Collectors.toList()));
    }

    private static boolean isAnnotated(Class<?> clzz, Method method, Class<? extends Annotation> annotationClass) {
        try {
            return clzz.getMethod(method.getName()).isAnnotationPresent(annotationClass);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static Class<?> getImplementedInterfaceClass(Class<?> testClass) {
        Class<?>[] interfacesClass = testClass.getInterfaces();
        if (interfacesClass.length == 0) {
            throw new RuntimeException("unable to get test case interface");
        }
        return interfacesClass[0];
    }
}
