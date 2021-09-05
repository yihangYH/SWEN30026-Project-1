package automail;

import automail.Robot.RobotState;
import exceptions.ExcessiveDeliveryException;
import exceptions.ItemTooHeavyException;
import simulation.Clock;
import simulation.IMailDelivery;

public class FastRobot extends Robot {
//	private static final int INDIVIDUAL_MAX_WEIGHT = 2000;
//
//    private IMailDelivery delivery;
////    private final String id;
//    /** Possible states the robot can be in */
////    public enum RobotState { DELIVERING, WAITING, RETURNING }
//    //private RobotState current_state;
//    private int current_floor;
//    private int destination_floor;
//    private MailPool mailPool;
//    private boolean receivedDispatch;
//
//    private MailItem deliveryItem = null;
//
//    private int deliveryCounter;
    
public FastRobot(IMailDelivery delivery, MailPool mailPool, int number) {
	super(delivery, mailPool, number);
	super.id = "F" + number; 
//    current_floor = Building.getInstance().getMailroomLocationFloor();
//    this.delivery = delivery;
//    this.mailPool = mailPool;
//    this.receivedDispatch = false;
//    this.deliveryCounter = 0;
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
        	if(current_floor - 1 == Building.getInstance().getMailroomLocationFloor()) {
        		moveTowards(Building.getInstance().getMailroomLocationFloor());
        		mailPool.registerWaiting(this);
        		changeState(RobotState.WAITING);
        	}else if(current_floor - 2 == Building.getInstance().getMailroomLocationFloor()) {
        		moveTowards(Building.getInstance().getMailroomLocationFloor());
        		moveTowards(Building.getInstance().getMailroomLocationFloor());
        		mailPool.registerWaiting(this);
        		changeState(RobotState.WAITING);
//        		break;
        	}else if(current_floor - 3 == Building.getInstance().getMailroomLocationFloor()) {
        		moveTowards(Building.getInstance().getMailroomLocationFloor());
        		moveTowards(Building.getInstance().getMailroomLocationFloor());
        		moveTowards(Building.getInstance().getMailroomLocationFloor());
        		mailPool.registerWaiting(this);
        		changeState(RobotState.WAITING);
//        		break;
        	}else{
        		moveTowards(Building.getInstance().getMailroomLocationFloor());
        		moveTowards(Building.getInstance().getMailroomLocationFloor());
        		moveTowards(Building.getInstance().getMailroomLocationFloor());
        		break;
        	}
        }
	case WAITING:
        /** If the StorageTube is ready and the Robot is waiting in the mailroom then start the delivery */
        if(!isEmpty() && receivedDispatch){
        	receivedDispatch = false;
        	deliveryCounter = 0; // reset delivery counter
        	setDestination();
        	changeState(RobotState.DELIVERING);
        }
        break;
	case DELIVERING:
		if(current_floor == destination_floor){ // If already here drop off either way
            /** Delivery complete, report this to the simulator! */
            delivery.deliver(this, deliveryItem, "");
            deliveryItem = null;
            deliveryCounter++;
            if(deliveryCounter > 1){  // Implies a simulation bug
            	throw new ExcessiveDeliveryException();
            }
            /** Check if want to return, i.e. if there is no item in the hands*/
            if(deliveryItem == null){
            	changeState(RobotState.RETURNING);
            }
		} else {
    		/** The robot is not at the destination yet, move towards it! */
            if(current_floor + 1 == destination_floor) {
            	moveTowards(destination_floor);
            }else if (current_floor + 2 == destination_floor) {
				moveTowards(destination_floor);
				moveTowards(destination_floor);
			}else if (current_floor + 3 == destination_floor) {
				moveTowards(destination_floor);
				moveTowards(destination_floor);
				moveTowards(destination_floor);
			}else {
				moveTowards(destination_floor);
				moveTowards(destination_floor);
				moveTowards(destination_floor);
			}
		}
        break;
}
}

@Override
public void dispatch() {
	// TODO Auto-generated method stub
	receivedDispatch = true;
}



/**
 * Sets the route for the robot
 */
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


@Override
public String getIdTube() {
	// TODO Auto-generated method stub
	return String.format("%s(%1d)", this.id, 0);
}



@Override
public void addToHand(MailItem mailItem) throws ItemTooHeavyException {
	// TODO Auto-generated method stub
//	super.addToHand(mailItem);
}



@Override
public void addToTube(MailItem mailItem) throws ItemTooHeavyException {
	// TODO Auto-generated method stub
	assert(deliveryItem == null);
	deliveryItem = mailItem;
	if (deliveryItem.weight > INDIVIDUAL_MAX_WEIGHT) throw new ItemTooHeavyException();
}



private void moveTowards(int destination) {
    if(current_floor < destination){
        current_floor ++;
    } else {
        current_floor --;
    }
}



@Override
public boolean isEmpty() {
	// TODO Auto-generated method stub
	return (deliveryItem == null);
}




}
