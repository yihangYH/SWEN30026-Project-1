package util;

import simulation.Clock;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Configuration extends Properties
{
    public static final String SEED_KEY = "Seed";

    public static final String REGULAR_ROBOTS_KEY = "Regular_Robots";
    public static final String FAST_ROBOTS_KEY = "Fast_Robots";
    public static final String BULK_ROBOTS_KEY = "Bulk_Robots";

    public static final String MAIL_TO_CREATE_KEY = "Mail_to_Create";
    public static final String MAIL_MAX_WEIGHT_KEY = "Mail_Max_Weight";
    public static final String MAIL_RECEIVING_LENGTH_KEY = "Mail_Receiving_Length";
    public static final String FEE_CHARGING_KEY = "Fee_Charging";

    public static final String N_FLOORS_KEY = "Floors";
    public static final String LOWEST_FLOOR_KEY = "Lowest_Floor";
    public static final String MAILROOM_LOCATION_FLOOR_KEY = "Mailroom_Location_Floor";

    private static Configuration configuration = null;

    public static Configuration getInstance()
    {
        if(configuration == null)
        {
            configuration = new Configuration();

            try {
                configuration.setUp();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return configuration;
    }

    private void setUp() throws IOException 
    {
        // Default properties
        setProperty(N_FLOORS_KEY, "10");
        setProperty(LOWEST_FLOOR_KEY, "1");
        setProperty(MAILROOM_LOCATION_FLOOR_KEY, "1");
        setProperty(MAIL_TO_CREATE_KEY, "80");
        setProperty(FEE_CHARGING_KEY, "false");

        // Read properties
        FileReader inStream = null;
        try {
            inStream = new FileReader("automail.properties");
            load(inStream);
        } finally {
            if (inStream != null) {
                inStream.close();
            }
        }

        // Floors
        int nFloors = Integer.parseInt(getProperty(N_FLOORS_KEY));
        System.out.println("#Floors: " + nFloors);

        // Mail_to_Create
        int mail_to_create = Integer.parseInt(getProperty(MAIL_TO_CREATE_KEY));
        System.out.println("#Created mails: " + mail_to_create);

        // Max_mail_weight
        int mail_max_weight = Integer.parseInt(getProperty(MAIL_MAX_WEIGHT_KEY));
        System.out.println("#Maximum weight: " + mail_max_weight);

        // Last_Delivery_Time
        int mail_receiving_length = Integer.parseInt(getProperty(MAIL_RECEIVING_LENGTH_KEY));
        System.out.println("#Mail receiving length: " + mail_receiving_length);

        // Robots
        int num_regular_robots = Integer.parseInt(getProperty(REGULAR_ROBOTS_KEY));
        int num_fast_robots = Integer.parseInt(getProperty(FAST_ROBOTS_KEY));
        int num_bulk_robots = Integer.parseInt(getProperty(BULK_ROBOTS_KEY));
        int total_robots = num_regular_robots + num_fast_robots + num_bulk_robots;
        System.out.println("#RegularRobots: " + num_regular_robots);
        System.out.println("#FastRobots: " + num_fast_robots);
        System.out.println("#BulkRobots: " + num_bulk_robots);
        System.out.println("#TotalRobots: " + total_robots);
        assert(total_robots > 0);

        // Calculate charge for a service fee and a maintenance cost.
        boolean fee_Charging = Boolean.parseBoolean(getProperty(FEE_CHARGING_KEY));
        System.out.println("Is fee charging enabled: " + fee_Charging);
    }
}
