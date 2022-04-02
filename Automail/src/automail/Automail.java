package automail;

import java.util.ArrayList;
import java.util.List;

import com.unimelb.swen30006.wifimodem.WifiModem;

import automail.Robot.RobotState;
import simulation.IMailDelivery;

public class Automail {
//	Initialize a list of Robot
    private Robot[] robots;
//	the mailPool class
    private MailPool mailPool;

//  IMailDelivery is an interface.
    public Automail(MailPool mailPool, IMailDelivery delivery, int numRegRobots, int numFastRobots, int numBulkRobots,
    		int nFloor) {  	
    	/** Initialize the MailPool */
    	
    	this.mailPool = mailPool;

    	int totalRobots = numBulkRobots + numFastRobots + numRegRobots;
    	/** Initialize an array called robots, and assign the value in the array to null */
    	robots = new Robot[totalRobots];
    	for(int i =0 ; i < totalRobots; i++) {
    		robots[i] = null;
    	}
    	/** iterate the robots array
    	 * for each type robot Initialize robot with ImailDelivery and robotID
    	 * and add the robot to robots array
    	 */
    	int i = 0;
    	while( i< totalRobots) {
    		// Regular robot first and then Fast robot, last is Bulk robot
    		for(int j = 0; j < numRegRobots; j++) {
    			if(robots[i] == null) {
    				robots[i] = new RegularRobot(delivery, mailPool, i);
    				i++;
    			}
    		}
    		for(int j = 0; j < numFastRobots; j++) {
    			if(robots[i] == null) {
    				robots[i] = new FastRobot(delivery, mailPool, i);
    				i++;
    			}
    		}
    		for(int j = 0; j < numBulkRobots; j++) {
    			if(robots[i] == null) {
    				robots[i] = new BulkRobot(delivery, mailPool, i);
    				i++;
    			}
    		}
    	}

    	
    	
    	
    }

    public Robot[] getRobots() {
        return robots;
    }

    public MailPool getMailPool() {
        return mailPool;
    }
    


    
    
    

    
}
