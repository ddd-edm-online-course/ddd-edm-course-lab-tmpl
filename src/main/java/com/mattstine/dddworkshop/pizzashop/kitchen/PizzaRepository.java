package com.mattstine.dddworkshop.pizzashop.kitchen;

import com.mattstine.dddworkshop.pizzashop.infrastructure.repository.ports.Repository;

interface PizzaRepository extends Repository<PizzaRef, Pizza, Pizza.PizzaState, PizzaEvent, PizzaAddedEvent> {
}
