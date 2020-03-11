package connection;

import constant.EntitiesConstants;
import java.util.ArrayList;
import java.util.List;

//Message creation for android
public class CommMgr extends EntitiesConstants{

    public static String generateFinalDescriptor(String part1, String part2) {
        return "{finaldescriptor:\"" + part1 + "," + part2 + "\"}";
    }

    /**
     * Generate map string for Android communication, note that on Android the coordinate of
     * the robot is the upper right corner.
     * @param descriptor Map descriptor in Android format
     * @param x Robot's x coordinates
     * @param y Robot's y coordinates
     * @param heading Robot's heading
     * @return Message string for sending to Android
     */
    public static String generateMapDescriptorMsg(String descriptor1,String descriptor2, int x, int y, int heading) {
        StringBuilder builder = new StringBuilder();
        //y,x,orientation
        builder.append("MDF");
        builder.append("|");
        builder.append(MAP_ROWS - y-1);
        builder.append("|");
        builder.append(x);
        builder.append("|");
        if (heading == NORTH) {
            builder.append(0);
        } else if (heading == EAST) {
            builder.append(1);
        } else if (heading == SOUTH) {
            builder.append(2);
        } else if (heading == WEST) {
            builder.append(3);
        }
        builder.append("|");
        builder.append(descriptor1);
        builder.append("|");
        builder.append(descriptor2);

        return builder.toString();
    }

    /**
     * Parse waypoint message from Android, the Y coordinate received
     * starts from the bottom, so it's reversed.
     * @param msg
     * @return
     */
    public static List<Integer> parseMessage(String msg) {
        String[] splitString = msg.split(",", 2);
        List<Integer> waypoint = new ArrayList<>();

        Integer wayPointX, wayPointY;
        try {
            wayPointX = Integer.parseInt(splitString[0]);
            wayPointY = (MAP_ROWS - Integer.parseInt(splitString[1]));
            waypoint.add(wayPointX);
            waypoint.add(wayPointY);
            return waypoint;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
