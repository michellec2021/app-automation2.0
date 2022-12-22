package wonder.cart;

import wonder.annotation.Priority;
import wonder.annotation.PriorityEnum;
import wonder.annotation.Type;
import wonder.annotation.TypeEnum;
import wonder.annotation.AfterGroup;
import wonder.annotation.AfterTest;
import wonder.annotation.BeforeGroup;
import wonder.annotation.BeforeTest;
import wonder.annotation.Case;

/**
 * @author michelle
 */
public interface CheckMergeShoppingCartWebService {
    @BeforeGroup
    void setUp();

    @AfterGroup
    void tearDown();

    @BeforeTest
    void beforeTest();

    @AfterTest
    void afterTest();

    @Case
    @Priority(PriorityEnum.CRITICAL)
    @Type(TypeEnum.UI)
    void checkMergeShoppingCartInCheckoutPag();

    @Case
    @Priority(PriorityEnum.CRITICAL)
    @Type(TypeEnum.UI)
    void checkMergeShoppingCartInShopCartPag();
}
