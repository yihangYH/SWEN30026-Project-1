package automail;

import java.util.ArrayList;
import java.util.List;



import automail.Robot.RobotState;
import exceptions.ExcessiveDeliveryException;
import exceptions.ItemTooHeavyException;
import simulation.Clock;
import simulation.IMailDelivery;
import util.Configuration;

public class BulkRobot extends Robot {
	
	/** This class is a sub class of robot called bulkrobot
	 Bulk Robot has a tube that could store at most 5 mails and no hands
	 Bulk robot moves 1 unit per tick*/
	private static final int INDIVIDUAL_MAX_WEIGHT = 2000;

	private int weight;
	private int count = 0;
    private int deliveryCounter;
    
	public BulkRobot(IMailDelivery delivery, MailPool mailPool, int number) {
		super(delivery, mailPool, number);
		super.type_based_rate = 0.01;
		
		this.id = "B" + number;
		for(int i = 0; i < 5; i++) {
			this.tubeList.add(null);
		}
		this.weight = 0;
	}
	@Override
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
            /** If the StorageTube is ready and the Robot is waiting in the mail room then start the delivery */
			if(!isEmpty() && receivedDispatch){
            	receivedDispatch = false;
            	deliveryCounter = 0; // reset delivery counter
            	setDestination();
            	if(!tubeList.isEmpty()) {
            		changeState(RobotState.DELIVERING);
            	}
            }
            break;
		case DELIVERING:
			TotalOperatingTime++;
			// If already here drop off either way
			if(current_floor == destination_floor){ 
                /** 
                 * Delivery complete for the current mails in the tube list
                 * report this to the simulator! 
                 */
				for(int i = 0 ; i < tubeList.size(); i++) {
                	if(tubeList.get(i) == deliveryItem) {
                		tubeList.set(i, null);
                		break;
                	}
                }
				if(apply_fee_Charging) {
					delivery.deliver(this, deliveryItem, serviceFee, avgWaitingTime);
				}else {
					delivery.deliver(this, deliveryItem,"");
				}
                
                deliveryItem =null;
                deliveryCounter++;
                
                // Implies a simulation bug
                if(deliveryCounter > 5){  
                	throw new ExcessiveDeliveryException();
                }
                /**check how many item left in list */
                int leftItem = 0;
                for(int i = 0; i< tubeList.size(); i++) {
        			if(tubeList.get(i) != null) {
        				leftItem++;        				
        			}
        		}
                /** Check if want to return, i.e. if there is no item in the tube*/
                if(leftItem == 0){
                	changeState(RobotState.RETURNING);

                
                /** If there is another item, set the robot's route to the location to deliver the item */
			    }else{
                    setDestination();
                    changeState(RobotState.DELIVERING);

                }
            /** The robot is not at the destination yet, move towards it! */
			}else {
                moveTowards(destination_floor);
			}
            break;
	}
	}
	protected void setDestination() {
		for(int i = 0; i< tubeList.size(); i++) {
			if(tubeList.get(i) != null) {
				deliveryItem = tubeList.get(i);
				break;
			}
		}
	    /** Set the destination floor */
	    destination_floor = deliveryItem.getDestFloor();
	}
	
    // change the state of robots
	protected void changeState(RobotState nextState){
		assert(!(deliveryItem == null));
		if (current_state != nextState) {
	        System.out.printf("T: %3d > %7s changed from %s to %s%n", Clock.Time(), getIdTube(), current_state, nextState);
		}
		current_state = nextState;
		if(nextState == RobotState.DELIVERING){
			// based on the tube list, find the correct mail and print it
			for(int i = 0 ; i<tubeList.size();i++) {
				if(tubeList.get(i) != null) {
					System.out.printf("T: %3d > %7s-> [%s]%n", Clock.Time(), getIdTube(), tubeList.get(i).toString());
					break;
				}
			}
		}
	}

	@Override
	protected void addToTube(MailItem mailItem) throws ItemTooHeavyException {

		for(int i =  tubeList.size()-1; i>= 0 ; i--) {
			if(tubeList.get(i) == null) {
				tubeList.set(i, mailItem);
				break;
			}
		}
		for(int i = 0; i< tubeList.size(); i++) {
			if(tubeList.get(i)!= null) {
				deliveryItem = tubeList.get(i);
				break;
			}
		}

		
	}
	/* get the number of items from tube*/
	public String getIdTube() {
		int numberOfItem =0;
		for(int i = 0; i< tubeList.size(); i++) {
			if(tubeList.get(i) != null) {
				numberOfItem++;
			}
		}
    	return String.format("%s(%1d)", this.id, numberOfItem);
    }
	

}
