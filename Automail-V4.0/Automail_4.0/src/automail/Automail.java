package automail;

import simulation.IMailDelivery;

public class Automail {
//	Initialize a list of Robot
    private Robot[] robots;
//	the mailPool class
    private MailPool mailPool;
//  IMailDelivery is on interface.
    public Automail(MailPool mailPool, IMailDelivery delivery, int numRegRobots, int numFastRobots, int numBulkRobots) {  	
    	/** Initialize the MailPool */
    	
    	this.mailPool = mailPool;
    	int totalRobots = numBulkRobots + numFastRobots + numRegRobots;
    	/** Initialize robots, currently only regular robots */
    	robots = new Robot[totalRobots];
    	for(int i =0 ; i < totalRobots; i++) {
    		robots[i] = null;
    	}
//    	for each robot Initialize robot with ImailDelivery and robotID
    	int i = 0;
    	while( i< totalRobots) {
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
