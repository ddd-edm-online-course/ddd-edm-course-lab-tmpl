package com.mattstine.dddworkshop.pizzashop.ordering;

import com.mattstine.dddworkshop.pizzashop.payments.PaymentRef; /**
 * @author Matt Stine
 */
interface OrderRepository {
	void add(Order order);

	OrderRef nextIdentity();

	Order findById(OrderRef orderRef);

	Order findByPaymentRef(PaymentRef paymentRef);
}
