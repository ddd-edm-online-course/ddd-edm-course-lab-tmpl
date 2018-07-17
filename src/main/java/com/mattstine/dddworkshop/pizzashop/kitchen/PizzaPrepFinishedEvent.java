package com.mattstine.dddworkshop.pizzashop.kitchen;

import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.Event;
import lombok.Value;

@Value
final class PizzaPrepFinishedEvent implements Event {
}

