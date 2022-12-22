package wonder.runner;

import org.junit.runner.JUnitCore;
import org.junit.runners.model.Statement;
import wonder.allure.AllureListener;
import wonder.annotation.AfterGroup;
import wonder.annotation.AfterTest;
import wonder.annotation.BeforeGroup;
import wonder.annotation.BeforeTest;
import wonder.model.ClassTestResult;
import wonder.model.TestClass;
import wonder.utils.AnnotationUtils;
import wonder.utils.MethodUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * @author michelle
 */
public class TestExecutor {
    private static Statement getMethodStatement(Method method, Object target) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                MethodUtils.invokeExplosively(method, target);
            }
        };
    }

    private final Map<Class<?>, List<String>> caseMap;

    public TestExecutor(Map<Class<?>, List<String>> caseMap) {
        this.caseMap = caseMap;
    }

    AllureListener allureListener = new AllureListener();

    public void runCases() {
        initReportFolder();
        for (Map.Entry<Class<?>, List<String>> caseEntry : caseMap.entrySet()) {
            List<String> methodNames = caseEntry.getValue();
            ClassTestResult classTestResult = new ClassTestResult();
            TestClass testClass = new TestClass(caseEntry.getKey(), classTestResult);
            Object target = testClass.getClassInstance();
            JUnitCore jUnitCore = new JUnitCore();
            jUnitCore.addListener(allureListener);
            if (classContainsFixture(testClass)) {
                allureListener.onBeforeClass(testClass);
            }
            executeBeforeGroup(jUnitCore, testClass, target);
            for (String methodName : methodNames) {
                Method method = testClass.getMethod(methodName);
                Statement statement = getMethodStatement(method, target);
                statement = withBeforeTest(testClass, statement);
                statement = withAfterTest(testClass, statement);
                jUnitCore.run(new TestCaseRunner(testClass, method, statement));
            }
            executeAfterGroup(jUnitCore, testClass, target);
            allureListener.onAfterClass(testClass);
        }
    }

    private void initReportFolder() {
        File file = new File("allure-results");
        if (file.exists()) {
            file.renameTo(new File("allure-results-" + ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))));
        }
    }

    private void executeBeforeGroup(JUnitCore jUnitCore, TestClass testClass, Object instance) {
        List<Method> befores = AnnotationUtils.getAnnotatedMethods(testClass.clazz, BeforeGroup.class);
        if (befores.isEmpty()) {
            return;
        }
        Method beforeGroupMethod = befores.get(0);
        Statement beforeGroupStatement = getMethodStatement(beforeGroupMethod, instance);
        jUnitCore.run(new BeforeGroupRunner(testClass, beforeGroupMethod, beforeGroupStatement));
    }

    private void executeAfterGroup(JUnitCore jUnitCore, TestClass testClass, Object target) {
        List<Method> afters = AnnotationUtils.getAnnotatedMethods(testClass.clazz, AfterGroup.class);
        if (afters.isEmpty()) {
            return;
        }
        Method afterGroupMethod = afters.get(0);
        Statement afterGroupStatement = getMethodStatement(afterGroupMethod, target);
        jUnitCore.run(new AfterGroupRunner(testClass, afterGroupMethod, afterGroupStatement));
    }

    private Statement withBeforeTest(TestClass testClass, Statement statement) {
        List<Method> befores = AnnotationUtils.getAnnotatedMethods(testClass.clazz, BeforeTest.class);
        return befores.isEmpty() ? statement : new RunBefores(testClass, statement, befores.get(0));
    }

    private Statement withAfterTest(TestClass testClass, Statement statement) {
        List<Method> afters = AnnotationUtils.getAnnotatedMethods(testClass.clazz, AfterTest.class);
        return afters.isEmpty() ? statement : new RunAfters(testClass, statement, afters.get(0));
    }

    private boolean classContainsFixture(TestClass testClass) {
        return !AnnotationUtils.getAnnotatedMethods(testClass.clazz, BeforeGroup.class).isEmpty()
                || !AnnotationUtils.getAnnotatedMethods(testClass.clazz, AfterGroup.class).isEmpty();
    }
}
