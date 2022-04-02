package automail;

import simulation.IMailDelivery;
import exceptions.ExcessiveDeliveryException;
import util.Configuration;

public class RegularRobot extends Robot  {
	
	// This class is a sub class of robot called regular robot
	// Regular Robot has a tube that could store 1 mail and hands to take 1 mail. It will always deliver mail in hand first
	// Regular robot moves 1 unit per tick
	public RegularRobot(IMailDelivery delivery, MailPool mailPool, int number) {
		super(delivery, mailPool, number);
		this.type_based_rate = 0.025;
	}
	public void operate() throws ExcessiveDeliveryException { 
    	Configuration configuration = Configuration.getInstance();
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
                    moveTowards(Building.getInstance().getMailroomLocationFloor());

                	break;
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

}
