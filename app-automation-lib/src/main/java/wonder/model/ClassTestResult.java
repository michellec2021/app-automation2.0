package wonder.model;

/**
 * @author michelle
 */
public class ClassTestResult {
    private TestResult beforeGroupResult;
    private TestResult beforeTestResult;

    public void setBeforeGroupResult(TestResult result) {
        this.beforeGroupResult = result;
    }

    public void setBeforeTestResult(TestResult result) {
        this.beforeTestResult = result;
    }

    public TestResult getBeforeGroupResult() {
        return beforeGroupResult;
    }

    public TestResult getBeforeTestResult() {
        return beforeTestResult;
    }
}
