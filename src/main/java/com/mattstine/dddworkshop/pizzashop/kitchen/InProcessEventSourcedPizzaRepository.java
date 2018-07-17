package com.mattstine.dddworkshop.pizzashop.kitchen;

import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.EventLog;
import com.mattstine.dddworkshop.pizzashop.infrastructure.events.ports.Topic;
import com.mattstine.dddworkshop.pizzashop.infrastructure.repository.adapters.InProcessEventSourcedRepository;

class InProcessEventSourcedPizzaRepository extends InProcessEventSourcedRepository<PizzaRef, Pizza, Pizza.PizzaState, PizzaEvent, PizzaAddedEvent> implements PizzaRepository {
    InProcessEventSourcedPizzaRepository(EventLog eventLog,
                                         Class<PizzaRef> pizzaRefClass,
                                         Class<Pizza> pizzaClass,
                                         Class<Pizza.PizzaState> pizzaStateClass,
                                         Class<PizzaAddedEvent> pizzaAddedEventClass,
                                         Topic pizzas) {
        super(eventLog, pizzaRefClass, pizzaClass, pizzaStateClass, pizzaAddedEventClass, pizzas);
    }
}
