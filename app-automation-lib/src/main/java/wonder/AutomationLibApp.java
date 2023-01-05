package wonder;

import core.framework.module.App;

/**
 * @author michelle
 */
public class AutomationLibApp extends App {
    @Override
    protected void initialize() {
        load(new AutomationLibModule());
    }
}
