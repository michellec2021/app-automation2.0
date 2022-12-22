package wonder.cart;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author michelle
 */
public class CheckCheckoutPageWebServiceImpl implements CheckCheckoutPageWebService {
    private final Logger logger = LoggerFactory.getLogger(CheckCheckoutPageWebServiceImpl.class);

    @Override
    public void prepareData() {
        Allure.step("prepare group data",()-> Assert.assertEquals(1,1));
    }

    @Override
    public void checkoutPageTest() {
        logger.info("test checkout page");
        Assert.assertEquals(1, 1);
    }
}
