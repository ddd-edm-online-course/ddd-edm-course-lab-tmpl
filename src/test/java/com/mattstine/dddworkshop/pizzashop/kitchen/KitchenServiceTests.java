package com.mattstine.dddworkshop.pizzashop.kitchen;

import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.EventHandler;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.Topic;
import com.mattstine.dddworkshop.pizzashop.ordering.OnlineOrderRef;
import com.mattstine.dddworkshop.pizzashop.ordering.OrderingService;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

public class KitchenServiceTests {

    private KitchenService service;
    private KitchenOrderRepository kitchenOrderRepository;
    private EventLog eventLog;

    @Before
    public void setUp() {
        eventLog = mock(EventLog.class);
        kitchenOrderRepository = mock(KitchenOrderRepository.class);
        OrderingService orderingService = mock(OrderingService.class);
        service = new KitchenService(eventLog, kitchenOrderRepository, orderingService);
    }

    @Test
    public void subscribes_to_ordering_topic() {
        verify(eventLog).subscribe(eq(new Topic("ordering")), isA(EventHandler.class));
    }

    @Test
    public void should_return_kitchenOrder_by_onlineOrderRef() {
        OnlineOrderRef onlineOrderRef = new OnlineOrderRef();

        KitchenOrder kitchenOrder = KitchenOrder.builder()
                .eventLog(eventLog)
                .onlineOrderRef(onlineOrderRef)
                .ref(new KitchenOrderRef())
                .build();

        when(kitchenOrderRepository.findByOnlineOrderRef(eq(onlineOrderRef))).thenReturn(kitchenOrder);

        assertThat(service.findKitchenOrderByOnlineOrderRef(onlineOrderRef)).isEqualTo(kitchenOrder);
    }


}
