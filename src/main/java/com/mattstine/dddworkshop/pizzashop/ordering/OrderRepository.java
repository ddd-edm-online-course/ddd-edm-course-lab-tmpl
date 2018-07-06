package com.mattstine.dddworkshop.pizzashop.ordering;

/**
 * @author Matt Stine
 */
public interface OrderRepository {
	void add(Order order);

	OrderRef nextIdentity();

	Order findById(OrderRef orderRef);
}
