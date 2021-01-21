package com.mattstine.dddworkshop.pizzashop.suites;

import com.mattstine.dddworkshop.pizzashop.infrastructure.domain.valuetypes.AmountTests;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.adapters.InProcessEventLogTests;
import com.mattstine.dddworkshop.pizzashop.ordering.*;
import com.mattstine.dddworkshop.pizzashop.payments.DefaultPaymentServiceIntegrationTests;
import com.mattstine.dddworkshop.pizzashop.payments.DefaultPaymentServiceTests;
import com.mattstine.dddworkshop.pizzashop.payments.InProcessEventSourcedPaymentRepositoryTests;
import com.mattstine.dddworkshop.pizzashop.payments.PaymentTests;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
@SelectClasses({
        AmountTests.class,
        InProcessEventLogTests.class,
        DefaultOrderingServiceIntegrationTests.class,
        DefaultOrderingServiceTests.class,
        InProcessEventSourcedOnlineOrderRepositoryTests.class,
        InProcessEventSourcedOnlineOrderRepositoryIntegrationTests.class,
        OnlineOrderTests.class,
        PizzaTests.class,
        DefaultPaymentServiceIntegrationTests.class,
        DefaultPaymentServiceTests.class,
        InProcessEventSourcedPaymentRepositoryTests.class,
        PaymentTests.class
})
public class SetupSuite {
}
