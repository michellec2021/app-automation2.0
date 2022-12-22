package wonder.cart;

import wonder.annotation.BeforeGroup;
import wonder.annotation.Case;
import wonder.annotation.Priority;
import wonder.annotation.PriorityEnum;
import wonder.annotation.Type;
import wonder.annotation.TypeEnum;

/**
 * @author michelle
 */
public interface CheckCheckoutPageWebService {
    @BeforeGroup
    void prepareData();

    @Case
    @Priority(PriorityEnum.CRITICAL)
    @Type(TypeEnum.UI)
    void checkoutPageTest();
}
