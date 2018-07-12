package com.mattstine.dddworkshop.pizzashop.infrastructure;

/**
 * @author Matt Stine
 */
public abstract class VerifiableEventHandler implements EventHandler {
	protected boolean invoked = false;

	public static VerifiableEventHandler of(EventHandler eventHandler) {
		return new VerifiableEventHandler() {
			@Override
			public void handleEvent(Event e) {
				this.invoked = true;
				eventHandler.handleEvent(e);
			}
		};
	}

	public boolean isInvoked() {
		return invoked;
	}
}
