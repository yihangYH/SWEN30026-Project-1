package automail;

import simulation.IMailDelivery;

public class RegularRobot extends Robot  {
	
	
public RegularRobot(IMailDelivery delivery, MailPool mailPool, int number) {
	super(delivery, mailPool, number);
}
}
