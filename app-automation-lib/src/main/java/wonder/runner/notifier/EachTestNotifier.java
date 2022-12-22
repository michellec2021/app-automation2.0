package wonder.runner.notifier;

import org.junit.internal.AssumptionViolatedException;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.MultipleFailureException;
import wonder.exception.AfterTestException;

public class EachTestNotifier {
    private final RunNotifier notifier;

    private final Description description;

    public EachTestNotifier(RunNotifier notifier, Description description) {
        this.notifier = notifier;
        this.description = description;
    }

    public void addFailure(Throwable targetException) {
        if (targetException instanceof AfterTestException) {
            return;
        }
        if (targetException instanceof MultipleFailureException) {
            addMultipleFailureException((MultipleFailureException) targetException);
        } else {
            notifier.fireTestFailure(new Failure(description, targetException));
        }
    }

    private void addMultipleFailureException(MultipleFailureException mfe) {
        for (Throwable each : mfe.getFailures()) {
            addFailure(each);
        }
    }

    public void addFailedAssumption(AssumptionViolatedException e) {
        notifier.fireTestAssumptionFailed(new Failure(description, e));
    }

    public void fireTestFinished() {
        notifier.fireTestFinished(description);
    }

    public void fireTestStarted() {
        notifier.fireTestStarted(description);
    }

    public void fireTestIgnored() {
        notifier.fireTestIgnored(description);
    }
}