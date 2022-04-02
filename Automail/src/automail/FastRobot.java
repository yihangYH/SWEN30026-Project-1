package automail;

import automail.Robot.RobotState;
import exceptions.ExcessiveDeliveryException;
import exceptions.ItemTooHeavyException;
import simulation.IMailDelivery;
import util.Configuration;

public class FastRobot extends Robot {
	// This class is a sub class of robot called fast robot
	// Fast Robot has hands to take 1 mail. 
	// Fast robot moves 3 units per tick

    
public FastRobot(IMailDelivery delivery, MailPool mailPool, int number) {
	super(delivery, mailPool, number);
	super.id = "F" + number;
	super.type_based_rate =0.05;
	

}

@Override
public void operate() throws ExcessiveDeliveryException {
	
	Configuration configuration = Configuration.getInstance();
	// initialize apply_fee_Charing, used to checked if fee charging is on 
	boolean apply_fee_Charging = Boolean.parseBoolean(configuration.getProperty(Configuration.FEE_CHARGING_KEY));
	
	switch(current_state) {
	/** This state is triggered when the robot is returning to the mailroom after a delivery */
	case RETURNING:
		TotalOperatingTime++;
		/** If its current position is at the mailroom, then the robot should change state */
        if(current_floor == Building.getInstance().getMailroomLocationFloor()){
			/** Tell the sorter the robot is ready */
			mailPool.registerWaiting(this);
        	changeState(RobotState.WAITING);
        	
        } else {
        	/** If the robot is not at the mailroom floor yet, then move towards it! */
        	if(current_floor - 1 == Building.getInstance().getMailroomLocationFloor()) {
        		current_floor = Building.getInstance().getMailroomLocationFloor();
        		break;
        	}else if(current_floor - 2 == Building.getInstance().getMailroomLocationFloor()) {
        		current_floor = Building.getInstance().getMailroomLocationFloor();
        		break;
        	}else if(current_floor - 3 == Building.getInstance().getMailroomLocationFloor()) {
        		current_floor = Building.getInstance().getMailroomLocationFloor();
        		break;
        	}else{
        		// if moves maxmium 3 floor each time.
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
		TotalOperatingTime++;
		if(current_floor == destination_floor){ // If already here drop off either way
            /** Delivery complete, report this to the simulator! */
			if(apply_fee_Charging) {
				delivery.deliver(this, deliveryItem, serviceFee, avgWaitingTime);
			}else {
				delivery.deliver(this, deliveryItem,"");
			}
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
            	break;
            }else if (current_floor + 2 == destination_floor) {
				moveTowards(destination_floor);
				moveTowards(destination_floor);
				break;
			}else if (current_floor + 3 == destination_floor) {
				moveTowards(destination_floor);
				moveTowards(destination_floor);
				moveTowards(destination_floor);
				break;
			}else {
				moveTowards(destination_floor);
				moveTowards(destination_floor);
				moveTowards(destination_floor);
			}
		}

        break;
	}
}

}
