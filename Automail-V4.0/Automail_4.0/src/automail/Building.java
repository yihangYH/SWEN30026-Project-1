package automail;

import util.Configuration;


public class Building
{

    /** The number of floors in the building **/
    private final int nFloors;
    
    /** Represents the ground floor location */
    private final int lowestFloor;
    
    /** Represents the mailroom location */
    private final int mailroomLocationFloor;

    private static Building building;

    private Building(int nFloors, int lowestFloor, int mailroomLocationFloor)
    {
        this.nFloors = nFloors;
        this.lowestFloor = lowestFloor;
        this.mailroomLocationFloor = mailroomLocationFloor;
    }

    public static Building getInstance() {
        if(building == null)
        {
            Configuration configuration = Configuration.getInstance();
            int nFloors = Integer.parseInt(configuration.getProperty(Configuration.N_FLOORS_KEY));
            int lowestFloor = Integer.parseInt(configuration.getProperty(Configuration.LOWEST_FLOOR_KEY));
            int mailroomLocationFloor = Integer.parseInt(configuration.getProperty(Configuration.MAILROOM_LOCATION_FLOOR_KEY));
            building = new Building(nFloors, lowestFloor, mailroomLocationFloor);
        }

        return building;
    }

    public int getnFloors() {
        return nFloors;
    }

    public int getLowestFloor() {
        return lowestFloor;
    }

    public int getMailroomLocationFloor() {
        return mailroomLocationFloor;
    }
}
