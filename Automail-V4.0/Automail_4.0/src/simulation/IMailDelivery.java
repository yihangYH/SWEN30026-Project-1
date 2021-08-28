package simulation;

import automail.MailItem;
import automail.Robot;

import java.util.Set;

/**
 * a MailDelivery is used by the Robot to deliver mail once it has arrived at the correct location
 */
public interface IMailDelivery {

	/**
     * Delivers an item at its floor
     * @param mailItem the mail item being delivered.
     */
	void deliver(Robot robot, MailItem mailItem, String additionalLog);

	double getTotal_delay();

	Set<MailItem> getDeliveredItems();
}