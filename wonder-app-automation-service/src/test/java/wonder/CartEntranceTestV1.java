package wonder;

import org.junit.Assert;
import org.junit.Test;
import wonder.cart.CheckCheckoutPageWebServiceImpl;
import wonder.cart.CheckMergeShoppingCartWebServiceImpl;
import wonder.runner.TestExecutor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author michelle
 */
public class CartEntranceTestV1 {
    @Test
    public void testCart() {
        Map<Class<?>, List<String>> caseMap = new HashMap<>();
        caseMap.put(CheckMergeShoppingCartWebServiceImpl.class, List.of("checkMergeShoppingCartInCheckoutPag", "checkMergeShoppingCartInShopCartPag"));
        caseMap.put(CheckCheckoutPageWebServiceImpl.class, List.of("checkoutPageTest"));
        TestExecutor testExecutor = new TestExecutor(caseMap);
        testExecutor.runCases();
        Assert.assertEquals(1, 1);
    }
}
