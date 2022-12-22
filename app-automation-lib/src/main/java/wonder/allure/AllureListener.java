package wonder.allure;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.model.FixtureResult;
import io.qameta.allure.model.Label;
import io.qameta.allure.model.Link;
import io.qameta.allure.model.Stage;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StatusDetails;
import io.qameta.allure.model.TestResult;
import io.qameta.allure.model.TestResultContainer;
import io.qameta.allure.util.AnnotationUtils;
import org.junit.Ignore;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import wonder.exception.BaseException;
import wonder.model.TestClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static io.qameta.allure.util.AnnotationUtils.getLabels;
import static io.qameta.allure.util.AnnotationUtils.getLinks;
import static io.qameta.allure.util.ResultsUtils.createFrameworkLabel;
import static io.qameta.allure.util.ResultsUtils.createHostLabel;
import static io.qameta.allure.util.ResultsUtils.createLanguageLabel;
import static io.qameta.allure.util.ResultsUtils.createPackageLabel;
import static io.qameta.allure.util.ResultsUtils.createSuiteLabel;
import static io.qameta.allure.util.ResultsUtils.createTestClassLabel;
import static io.qameta.allure.util.ResultsUtils.createTestMethodLabel;
import static io.qameta.allure.util.ResultsUtils.createThreadLabel;
import static io.qameta.allure.util.ResultsUtils.getProvidedLabels;
import static io.qameta.allure.util.ResultsUtils.getStatusDetails;
import static io.qameta.allure.util.ResultsUtils.md5;

/**
 * @author michelle
 */
@RunListener.ThreadSafe
@SuppressWarnings({"PMD.ExcessiveImports", "PMD.CouplingBetweenObjects", "checkstyle:ClassFanOutComplexity"})
public class AllureListener extends RunListener {
    private final Map<Class<?>, String> classContainerUuidStorage = new ConcurrentHashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public static Optional<Status> getStatus(final Throwable throwable) {
        return Optional.ofNullable(throwable)
                .map(t -> t instanceof AssertionError || t instanceof BaseException ? Status.FAILED : Status.BROKEN);
    }

    private static ThreadLocal<String> testCases = new InheritableThreadLocal<String>() {
        @Override
        protected String initialValue() {
            return UUID.randomUUID().toString();
        }
    };

    private final ThreadLocal<String> currentExecutable = ThreadLocal
            .withInitial(() -> UUID.randomUUID().toString());

    private final AllureLifecycle lifecycle;

    public AllureListener() {
        this(Allure.getLifecycle());
    }

    public AllureListener(final AllureLifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    public AllureLifecycle getLifecycle() {
        return lifecycle;
    }

    @Override
    public void testRunStarted(final Description description) {
        //do nothing
    }

    @Override
    public void testRunFinished(final Result result) {
        //do nothing
    }

    public void onBeforeClass(final TestClass testClass) {
        final String uuid = UUID.randomUUID().toString();
        final TestResultContainer container = new TestResultContainer()
                .setUuid(uuid)
                .setName(testClass.clazz.getSimpleName());
        getLifecycle().startTestContainer(container);
        setClassContainer(testClass.clazz, uuid);
    }

    public void onAfterClass(final TestClass testClass) {
        getClassContainer(testClass.clazz).ifPresent(uuid -> {
            getLifecycle().stopTestContainer(uuid);
            getLifecycle().writeTestContainer(uuid);
        });
    }

    public void beforeGroupStarted(Description description) {
        getClassContainer(getTestClass(description))
                .ifPresent(parentUuid -> {
                    final String uuid = currentExecutable.get();
                    getLifecycle().startPrepareFixture(parentUuid, uuid, getFixtureResult(description.getMethodName()));
                });
    }

    public void fixtureFinished() {
        final String executableUuid = currentExecutable.get();
        currentExecutable.remove();
        getLifecycle().updateFixture(executableUuid, result -> {
            if (Objects.isNull(result.getStatus())) {
                result.setStatus(Status.PASSED);
            }
        });
        getLifecycle().stopFixture(executableUuid);
    }

    public void fixtureFailed(final Failure failure) {
        final String executableUuid = currentExecutable.get();
        getLifecycle().updateFixture(executableUuid, result -> result
                .setStatus(getStatus(failure.getException()).orElse(null))
                .setStatusDetails(getStatusDetails(failure.getException()).orElse(null)));
    }

    public void afterGroupStarted(final Description description) {
        getClassContainer(getTestClass(description))
                .ifPresent(parentUuid -> {
                    final String uuid = currentExecutable.get();
                    getLifecycle().startTearDownFixture(parentUuid, uuid, getFixtureResult(description.getMethodName()));
                });
    }

    @Override
    public void testStarted(final Description description) {
        final String uuid = testCases.get();
        final TestResult result = createTestResult(uuid, description);
        getLifecycle().scheduleTestCase(result);
        getLifecycle().startTestCase(uuid);

        addChildToContainer(getClassContainer(getTestClass(description)), uuid);
    }

    @Override
    public void testFinished(final Description description) {
        final String uuid = testCases.get();
        testCases.remove();
        getLifecycle().updateTestCase(uuid, testResult -> {
            if (Objects.isNull(testResult.getStatus())) {
                testResult.setStatus(Status.PASSED);
            }
        });

        getLifecycle().stopTestCase(uuid);
        getLifecycle().writeTestCase(uuid);
    }

    @Override
    public void testFailure(final Failure failure) {
        final String uuid = testCases.get();
        getLifecycle().updateTestCase(uuid, testResult -> testResult
                .setStatus(getStatus(failure.getException()).orElse(null))
                .setStatusDetails(getStatusDetails(failure.getException()).orElse(null))
        );
    }

    @Override
    public void testAssumptionFailure(final Failure failure) {
        final String uuid = testCases.get();
        getLifecycle().updateTestCase(uuid, testResult ->
                testResult.setStatus(Status.SKIPPED)
                        .setStatusDetails(getStatusDetails(failure.getException()).orElse(null))
        );
    }

    @Override
    public void testIgnored(final Description description) {
        final String uuid = testCases.get();
        testCases.remove();

        final TestResult result = createTestResult(uuid, description);
        result.setStatus(Status.SKIPPED);
        result.setStatusDetails(getIgnoredMessage(description));
        result.setStart(System.currentTimeMillis());

        getLifecycle().scheduleTestCase(result);
        getLifecycle().stopTestCase(uuid);
        getLifecycle().writeTestCase(uuid);
    }

    private Class<?> getTestClass(Description description) {
        Class<?> testClass = description.getTestClass();
        if (!Objects.nonNull(testClass)) {
            throw new RuntimeException("Description doesn't contain testClass");
        }
        return testClass;
    }

    private void setClassContainer(Class<?> clazz, final String uuid) {
        lock.writeLock().lock();
        try {
            classContainerUuidStorage.put(clazz, uuid);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private Optional<String> getClassContainer(Class<?> clazz) {
        lock.readLock().lock();
        try {
            return Optional.ofNullable(classContainerUuidStorage.get(clazz));
        } finally {
            lock.readLock().unlock();
        }
    }

    private FixtureResult getFixtureResult(String methodName) {
        final FixtureResult fixtureResult = new FixtureResult()
                .setName(methodName)
                .setStart(System.currentTimeMillis())
                //.setDescription(method())
                .setStage(Stage.RUNNING);
        return fixtureResult;
    }

    private void addChildToContainer(final Optional<String> containerUuidOptional, final String childUuid) {
        lock.writeLock().lock();
        try {
            containerUuidOptional.ifPresent(uuid ->
                    getLifecycle().updateTestContainer(uuid, container -> container.getChildren().add(childUuid))
            );
        } finally {
            lock.writeLock().unlock();
        }
    }

    private Optional<String> getDisplayName(final Description result) {
        return Optional.ofNullable(result.getAnnotation(DisplayName.class))
                .map(DisplayName::value);
    }

    private Optional<String> getDescription(final Description result) {
        return Optional.ofNullable(result.getAnnotation(io.qameta.allure.Description.class))
                .map(io.qameta.allure.Description::value);
    }

    private List<Link> extractLinks(final Description description) {
        final List<Link> result = new ArrayList<>(getLinks(description.getAnnotations()));
        Optional.of(description)
                .map(Description::getTestClass)
                .map(AnnotationUtils::getLinks)
                .ifPresent(result::addAll);
        return result;
    }

    private List<Label> extractLabels(final Description description) {
        final List<Label> result = new ArrayList<>(getLabels(description.getAnnotations()));
        Optional.of(description)
                .map(Description::getTestClass)
                .map(AnnotationUtils::getLabels)
                .ifPresent(result::addAll);
        return result;
    }

    private String getHistoryId(final Description description) {
        return md5(description.getClassName() + description.getMethodName());
    }

    private String getPackage(final Class<?> testClass) {
        return Optional.ofNullable(testClass)
                .map(Class::getPackage)
                .map(Package::getName)
                .orElse("");
    }

    private StatusDetails getIgnoredMessage(final Description description) {
        final Ignore ignore = description.getAnnotation(Ignore.class);
        final String message = Objects.nonNull(ignore) && !ignore.value().isEmpty()
                ? ignore.value() : "Test ignored (Before Group Failed)!";
        return new StatusDetails().setMessage(message);
    }

    private TestResult createTestResult(final String uuid, final Description description) {
        final String className = description.getClassName();
        final String methodName = description.getMethodName();
        final String name = Objects.nonNull(methodName) ? methodName : className;
        final String fullName = Objects.nonNull(methodName) ? String.format("%s.%s", className, methodName) : className;
        final String suite = Optional.ofNullable(description.getTestClass())
                .map(it -> it.getAnnotation(DisplayName.class))
                .map(DisplayName::value).orElse(className);

        final TestResult testResult = new TestResult()
                .setUuid(uuid)
                .setHistoryId(getHistoryId(description))
                .setFullName(fullName)
                .setName(name);

        testResult.getLabels().addAll(getProvidedLabels());
        testResult.getLabels().addAll(Arrays.asList(
                createPackageLabel(getPackage(description.getTestClass())),
                createTestClassLabel(className),
                createTestMethodLabel(name),
                createSuiteLabel(suite),
                createHostLabel(),
                createThreadLabel(),
                createFrameworkLabel("junit4"),
                createLanguageLabel("java")
        ));
        testResult.getLabels().addAll(extractLabels(description));
        testResult.getLinks().addAll(extractLinks(description));

        getDisplayName(description).ifPresent(testResult::setName);
        getDescription(description).ifPresent(testResult::setDescription);
        return testResult;
    }
}
