package simulation;

import automail.Automail;
import automail.Building;
import automail.MailPool;
import com.unimelb.swen30006.wifimodem.WifiModem;
import exceptions.ExcessiveDeliveryException;
import exceptions.ItemTooHeavyException;
import util.Configuration;
import util.ReportDelivery;

import java.util.HashMap;

/**
 * This class simulates the behaviour of AutoMail
 */
public class Simulation {
	
    /** Constant for the mail generator */
    private static IMailDelivery iMailDelivery;

    public static void main(String[] args) throws Exception {
    	
    	/** Load properties for simulation based on either default or a properties file.**/
    	Configuration configuration = Configuration.getInstance();

        iMailDelivery = new ReportDelivery();

        /** This code section below is to save a random seed for generating mails.
         * If a program argument is entered, the first argument will be a random seed.
         * If not a random seed will be from a properties file. 
         * Otherwise, no a random seed. */
        
        /** Used to see whether a seed is initialized or not */
        HashMap<Boolean, Integer> seedMap = new HashMap<>();
        if (args.length == 0 ) { // No arg
        	String seedProp = configuration.getProperty(Configuration.SEED_KEY);
        	if (seedProp == null) { // and no property
        		seedMap.put(false, 0); // so randomise
        	} else { // Use property seed
        		seedMap.put(true, Integer.parseInt(seedProp));
        	}
        } else { // Use arg seed - overrides property
        	seedMap.put(true, Integer.parseInt(args[0]));
        }
        Integer seed = seedMap.get(true);
        System.out.println("#A Random Seed: " + (seed == null ? "null" : seed.toString()));

        // setup the clock
        Clock.MAIL_RECEVING_LENGTH = Integer.parseInt(configuration.getProperty(Configuration.MAIL_RECEIVING_LENGTH_KEY));
        
        /** Install the modem & turn on the modem **/
     	WifiModem wifiModem = WifiModem.getInstance(Building.getInstance().getMailroomLocationFloor());
     	System.out.println("Setting up Wifi Modem");
     	System.out.println(wifiModem.Turnon());

        /**
         * This code section is for running a simulation
         */

        /** Instantiate MailPool and Automail */
     	MailPool mailPool = new MailPool();
        int num_regular_robots = Integer.parseInt(configuration.getProperty(Configuration.REGULAR_ROBOTS_KEY));
        int num_fast_robots = Integer.parseInt(configuration.getProperty(Configuration.FAST_ROBOTS_KEY));
        int num_bulk_robots = Integer.parseInt(configuration.getProperty(Configuration.BULK_ROBOTS_KEY));
        int total_robots = num_regular_robots + num_fast_robots + num_bulk_robots;
        Automail automail = new Automail(mailPool, iMailDelivery, num_regular_robots, num_fast_robots, num_bulk_robots);

        int mail_to_create = Integer.parseInt(configuration.getProperty(Configuration.MAIL_TO_CREATE_KEY));
        int mail_max_weight = Integer.parseInt(configuration.getProperty(Configuration.MAIL_MAX_WEIGHT_KEY));
        MailGenerator mailGenerator = new MailGenerator(mail_to_create, mail_max_weight, mailPool, seedMap);
        
        /** Generate all the mails */
        mailGenerator.generateAllMail();

        while(iMailDelivery.getDeliveredItems().size() != mailGenerator.MAIL_TO_CREATE)
        {
        	// System.out.printf("Delivered: %4d; Created: %4d%n", MAIL_DELIVERED.size(), mailGenerator.MAIL_TO_CREATE);
            mailGenerator.addToMailPool();
            try {
                automail.getMailPool().loadItemsToRobot();
				for (int i=0; i < total_robots; i++)
				{
					automail.getRobots()[i].operate();
				}
			} catch (ExcessiveDeliveryException|ItemTooHeavyException e) {
				e.printStackTrace();
				System.out.println("Simulation unable to complete.");
				System.exit(0);
			}

            Clock.Tick();
        }

        printResults();
        System.out.println(wifiModem.Turnoff());
    }

    public static void printResults(){
        System.out.println("T: "+Clock.Time()+" | Simulation complete!");
        System.out.println("Final Delivery time: "+Clock.Time());
        System.out.printf("Delay: %.2f%n", iMailDelivery.getTotal_delay());
    }
}
