package automail;

import java.util.ArrayList;
import java.util.List;
import exceptions.ExcessiveDeliveryException;
import exceptions.ItemTooHeavyException;
import simulation.Clock;
import simulation.IMailDelivery;



/**
 * The robot delivers mail!
 */

public abstract class Robot {

    protected static final int INDIVIDUAL_MAX_WEIGHT = 2000;

    protected IMailDelivery delivery;
    protected  String id;
    /** Possible states the robot can be in */
    public enum RobotState { DELIVERING, WAITING, RETURNING }
    protected RobotState current_state;
    protected int current_floor;
    protected int destination_floor;
    protected MailPool mailPool;
    protected boolean receivedDispatch;

    protected MailItem deliveryItem = null;
    protected MailItem tube = null;

    protected int deliveryCounter;
    protected List<MailItem> tubeList = new ArrayList<MailItem>();
    protected int TotalOperatingTime;
    protected double serviceFee;
    protected double type_based_rate;
    protected double avgWaitingTime;

    

    /**
     * Initiates the robot's location at the start to be at the mailroom
     * also set it to be waiting for mail.
     * @param delivery governs the final delivery
     * @param mailPool is the source of mail items
     */
    public Robot(IMailDelivery delivery, MailPool mailPool, int number){
    	this.id = "R" + number;
        // current_state = RobotState.WAITING;
    	current_state = RobotState.RETURNING;
        current_floor = Building.getInstance().getMailroomLocationFloor();
        this.delivery = delivery;
        this.mailPool = mailPool;
        this.receivedDispatch = false;
        this.deliveryCounter = 0;
        this.type_based_rate = 0.025;
    }
    
    

	/**
     * This is called when a robot is assigned the mail items and ready to dispatch for the delivery 
     */
    protected void dispatch() {
    	receivedDispatch = true;
    }

    /**
     * This is called on every time step
     * @throws ExcessiveDeliveryException if robot delivers more than the capacity of the tube without refilling
     */
    public abstract void operate() throws ExcessiveDeliveryException;

    /**
     * Sets the route for the robot
     */
    protected void setDestination() {
        /** Set the destination floor */
        destination_floor = deliveryItem.getDestFloor();
    }

    /**
     * Generic function that moves the robot towards the destination
     * @param destination the floor towards which the robot is moving
     */
    protected void moveTowards(int destination) {
        if(current_floor < destination){
            current_floor++;
        } else {
            current_floor--;
        }
    }
    
    public String getIdTube() {
    	return String.format("%s(%1d)", this.id, (tube == null ? 0 : 1));
    }
    
    /**
     * Prints out the change in state
     * @param nextState the state to which the robot is transitioning
     */
    protected void changeState(RobotState nextState){
    	assert(!(deliveryItem == null && tube != null));
    	if (current_state != nextState) {
            System.out.printf("T: %3d > %7s changed from %s to %s%n", Clock.Time(), getIdTube(), current_state, nextState);
    	}
    	current_state = nextState;
    	if(nextState == RobotState.DELIVERING){
            System.out.printf("T: %3d > %7s-> [%s]%n", Clock.Time(), getIdTube(), deliveryItem.toString());
    	}
    }

	protected MailItem getTube() {
		return tube;
	}
	
	protected RobotState getState() {
		return this.current_state;
	};

	protected boolean isEmpty() {
		return (deliveryItem == null && tube == null);
	}

	protected void addToHand(MailItem mailItem) throws ItemTooHeavyException {
		assert(deliveryItem == null);
		deliveryItem = mailItem;
		if (deliveryItem.weight > INDIVIDUAL_MAX_WEIGHT) throw new ItemTooHeavyException();
	}

	protected void addToTube(MailItem mailItem) throws ItemTooHeavyException {
		assert(tube == null);
		tube = mailItem;
		if (tube.weight > INDIVIDUAL_MAX_WEIGHT) throw new ItemTooHeavyException();
	}
	
	protected int getsize() {
		int size = 0;
		return size;
		
	}
	public int getCurrentFloor() {
		return current_floor;
	}
	public void fee(double serviceFee, double avgWaitingTime ) {
		this.serviceFee = serviceFee;
		this.avgWaitingTime = avgWaitingTime; 
	}
	public double getTypeBasedRate() {
		return this.type_based_rate;
	}
    
	public boolean isWaiting() {
		if(this.current_state == RobotState.WAITING) {
			return true;
		}
		return false;
	}
	public boolean isReturning() {
		if(this.current_state == RobotState.RETURNING) {
			return true;
		}
		return false;
	}


	

}
