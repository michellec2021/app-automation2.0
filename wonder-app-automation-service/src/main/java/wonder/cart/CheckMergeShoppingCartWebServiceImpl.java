package wonder.cart;

import io.qameta.allure.Step;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author michelle
 */
public class CheckMergeShoppingCartWebServiceImpl implements CheckMergeShoppingCartWebService {
    private final Logger logger = LoggerFactory.getLogger(CheckMergeShoppingCartWebServiceImpl.class);

    @Override
    public void setUp() {
        beforeStep();
        logger.info("run before group...");
    }

    @Step("before test group")
    private void beforeStep() {
        Assert.assertEquals(1, 1);
    }

    @Override
    public void tearDown() {
        tearStep();
        logger.info("run after group...");
    }

    @Step("after test group")
    private void tearStep() {
        Assert.assertEquals(6, 6);
    }

    @Override
    public void beforeTest() {
        beforeTestStep();
        logger.info("run before test...");
    }

    @Step("before test case")
    private void beforeTestStep() {
        Assert.assertEquals(3, 7);
    }

    @Override
    public void afterTest() {
        afterTestStep();
        logger.info("run after test...");
    }

    @Step("after test case")
    private void afterTestStep() {
        Assert.assertEquals(4, 4);
    }

    @Override
    public void checkMergeShoppingCartInCheckoutPag() {
        logger.info("check merge shopping cart in checkout page");
        step1();
    }

    @Step("first step")
    public void step1() {
        Assert.assertEquals(3, 5);
        logger.info("run step 1");
    }

    @Override
    public void checkMergeShoppingCartInShopCartPag() {
        logger.info("check merge shopping cart in shop cart page");
    }
}
