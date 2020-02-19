package constant;


public class EntitiesConstants {
	//MapConstants
	public static final int MAP_ROWS = 20;
    public static final int MAP_COLS = 15;
    public static final int ZONE_SIZE = 3;
    public static final int CELL_SIZE = 30;
    
    //RobotContants
    public static final int START_POS_X = 0;
    public static final int START_POS_Y = MAP_ROWS - 3;
    public static final int PAINT_PIXEL_OFFSET = 10;
    public static final int ROBOT_SIZE = 3;
    public static final int HEADING_PIXEL_SIZE = 8;

    public static final int NORTH = 0;
    public static final int EAST = 1;
    public static final int SOUTH = 2;
    public static final int WEST = 3;

    public static final int LEFT = 4;
    public static final int RIGHT = 5;
    public static final int MIDDLE = 6;

    //CommsConstant
    public static final String TARGET_ARDUINO = "A";
    public static final String TARGET_ANDROID = "T";
    public static final String TARGET_BOTH = "Z";
}
