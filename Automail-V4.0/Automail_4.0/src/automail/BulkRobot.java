package automail;

import automail.Robot.RobotState;
import simulation.IMailDelivery;

public class BulkRobot extends Robot {
	private static final int INDIVIDUAL_MAX_WEIGHT = 2000;

    private IMailDelivery delivery;
    private final String id;
    /** Possible states the robot can be in */
    public enum RobotState { DELIVERING, WAITING, RETURNING }
    private RobotState current_state;
    private int current_floor;
    private int destination_floor;
    private MailPool mailPool;
    private boolean receivedDispatch;

    private MailItem deliveryItem = null;
    private MailItem tube = null;

    private int deliveryCounter;
	public BulkRobot(IMailDelivery delivery, MailPool mailPool, int number) {
		super(delivery, mailPool, number);
		this.id = "B" + number;
	}

}
