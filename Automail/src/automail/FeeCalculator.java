package automail;

import java.util.ArrayList;
import java.util.List;

import com.unimelb.swen30006.wifimodem.WifiModem;

import simulation.IMailDelivery;
import util.Configuration;


/** This class is used to calculate fee */


public class FeeCalculator {
	
	/**calculate service fee*/
    private List<Double> ServiceFee = new ArrayList<Double>();
    public void initialize(int nFloor) {  
    	for(int k =1 ; k <= nFloor+1; k++) {
    		ServiceFee.add(0.0);;
    	}
		    
	}
	public boolean checkDelivered(Robot robot){
		if(robot.current_floor == robot.destination_floor) {
			return true;
		}
		return false;
	}
	// this method use to find the service fee
    public double updateServiceFee(WifiModem wifiModem, Robot robot) {
    	if(checkDelivered(robot) && !robot.isReturning() && !robot.isWaiting()) {
    		double prevFee = ServiceFee.get(robot.destination_floor);
    		double newFee =  wifiModem.forwardCallToAPI_LookupPrice(robot.destination_floor);
    		// lookup success
    		if(newFee >= prevFee) {
    			ServiceFee.set(robot.destination_floor,newFee);
    	    	/**
    	        *
    	        * @return the service fee
    	        */
    			return newFee;
    		}
    		// lookup request fail
    		if(newFee < 0.0) {
    			// if other robot lookup fee previous
    			if(prevFee != 0.0) {
    				return prevFee;
    			}// if system has no records 
    			return 0.0;
    		
    		}
    	}
    	return 0.0;
 
    }
 // calculate maintenance fee of each type of robots.
    public double updateMaintenance(Robot robot,Robot[] robots) {

    	int numRobot = 0;
    	double time= 1;
    	double type = robot.getTypeBasedRate();
    	
    	//first find type of current robot
    	for(int i=0; i<robots.length; i++) {
    		if(robots[i].getTypeBasedRate() == type) {
    			numRobot++;
    			time += robots[i].TotalOperatingTime;
    		}
    	}
    	
    	/**
        *
        * @return the avg time of each robots
        */
    	return time/numRobot;
    }

}
