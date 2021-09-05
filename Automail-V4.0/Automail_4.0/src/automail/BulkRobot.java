package automail;

import java.util.ArrayList;
import java.util.List;



import automail.Robot.RobotState;
import exceptions.ExcessiveDeliveryException;
import exceptions.ItemTooHeavyException;
import simulation.Clock;
import simulation.IMailDelivery;

public class BulkRobot extends Robot {
	private static final int INDIVIDUAL_MAX_WEIGHT = 2000;
//
//    private IMailDelivery delivery;
//    private final String id;
//    /** Possible states the robot can be in */
//    public enum RobotState { DELIVERING, WAITING, RETURNING }
//    private RobotState current_state;
//    private int current_floor;
//    private int destination_floor;
//    private MailPool mailPool;
//    private boolean receivedDispatch;
//
//    private MailItem deliveryItem = null;
//    private MailItem tube = null;
	private int weight;
	private int count = 0;
//	private List<MailItem> tubeList = new ArrayList<MailItem>();
    private int deliveryCounter;
	public BulkRobot(IMailDelivery delivery, MailPool mailPool, int number) {
		super(delivery, mailPool, number);
		this.id = "B" + number;
		for(int i = 0; i < 5; i++) {
			this.tubeList.add(null);
		}
		this.weight = 0;
	}
	@Override
	public void operate() throws ExcessiveDeliveryException {
		switch(current_state) {
		/** This state is triggered when the robot is returning to the mailroom after a delivery */
		case RETURNING:
			/** If its current position is at the mailroom, then the robot should change state */
            if(current_floor == Building.getInstance().getMailroomLocationFloor()){
    			/** Tell the sorter the robot is ready */
    			mailPool.registerWaiting(this);
            	changeState(RobotState.WAITING);
            } else {
            	/** If the robot is not at the mailroom floor yet, then move towards it! */
                moveTowards(Building.getInstance().getMailroomLocationFloor());
            	break;
            }
		case WAITING:
            /** If the StorageTube is ready and the Robot is waiting in the mailroom then start the delivery */
			if(!isEmpty() && receivedDispatch){
            	receivedDispatch = false;
            	deliveryCounter = 0; // reset delivery counter
            	setDestination();
            	if(!tubeList.contains(null)) {
            		changeState(RobotState.DELIVERING);
            	}
            }
            break;
		case DELIVERING:
			if(current_floor == destination_floor){ // If already here drop off either way
                /** Delivery complete, report this to the simulator! */
                delivery.deliver(this, deliveryItem, "");
                deliveryItem = null;
                deliveryCounter++;
                if(deliveryCounter > 2){  // Implies a simulation bug
                	throw new ExcessiveDeliveryException();
                }
                /** Check if want to return, i.e. if there is no item in the tube*/
                if(tube == null){
                	changeState(RobotState.RETURNING);
                }
                else{
                    /** If there is another item, set the robot's route to the location to deliver the item */
                    deliveryItem = tube;
                    tube = null;
                    setDestination();
                    changeState(RobotState.DELIVERING);
                }
			} else {
        		/** The robot is not at the destination yet, move towards it! */
                moveTowards(destination_floor);
			}
            break;
	}
	}
	private void setDestination() {
	    /** Set the destination floor */
	    destination_floor = deliveryItem.getDestFloor();
	}

	private void changeState(RobotState nextState){
		assert(!(deliveryItem == null));
		if (current_state != nextState) {
	        System.out.printf("T: %3d > %7s changed from %s to %s%n", Clock.Time(), getIdTube(), current_state, nextState);
		}
		current_state = nextState;
		if(nextState == RobotState.DELIVERING){
	        System.out.printf("T: %3d > %7s-> [%s]%n", Clock.Time(), getIdTube(), deliveryItem.toString());
		}
	}
	
	private void moveTowards(int destination) {
        if(current_floor < destination){
            current_floor++;
        } else {
            current_floor--;
        }
    }
	
	
	
	@Override
	public void addToHand(MailItem mailItem) throws ItemTooHeavyException {
		// TODO Auto-generated method stub
		for(int i = 0; i< tubeList.size(); i++) {
			if(tubeList.get(i) == null) {
				tubeList.set(i, mailItem);
				break;
			}
		}
		
	}
	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return (deliveryItem == null && tube == null);
	}
	@Override
	public void dispatch() {
		if(!tubeList.contains(null)) {
			receivedDispatch = true;
		}
	}
	@Override
	public void addToTube(MailItem mailItem) throws ItemTooHeavyException {
		for(int i = 0; i< tubeList.size(); i++) {
			if(tubeList.get(i) == null) {
				tubeList.set(i, mailItem);
				break;
			}
		}
		
//		tube = mailItem;
//		if (tube.weight > INDIVIDUAL_MAX_WEIGHT) throw new ItemTooHeavyException();
		
	}
	

	

}
