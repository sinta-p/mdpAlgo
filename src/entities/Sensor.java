package entities;

import constant.EntitiesConstants;
import static constant.EntitiesConstants.*;


public class Sensor {


    private int mRange;
    private int mPosX;
    private int mPosY;
    private int mDirection;
    private int mReliability;
    private Robot mRobot;

    public Sensor(int range, int posX, int posY, int direction, int reliability) {
        mRange = range;
        mPosX = posX;
        mPosY = posY;
        mDirection = direction;
        mReliability = reliability;
    }

    /**
     * Return the distance of obstacle (if any) from the current sensor's
     * absolute position in the grid. If no obstacle is found, it returns
     * a large value.
     * @param grid
     * @return
     */
    int sense(GridMap grid) {
        int absPosX = getActualPosX();
        int absPosY = getActualPosY();

        int actualDirection = getActualOrientation();

        for (int i = 1; i <= mRange; i++) {
            if (actualDirection == NORTH) {
                if (grid.getIsObstacle(absPosX, absPosY - i))
                    return i;
            } else if (actualDirection == EAST) {
                if (grid.getIsObstacle(absPosX + i, absPosY))
                    return i;
            } else if (actualDirection == SOUTH) {
                if (grid.getIsObstacle(absPosX, absPosY + i))
                    return i;
            } else if (actualDirection == WEST) {
                if (grid.getIsObstacle(absPosX - i, absPosY))
                    return i;
            }
        }
        return 100;
    }

    int getActualOrientation() {
        /*
        NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3
         */
        int actualDirection = -1;
        if (mDirection == LEFT) {
            actualDirection = (mRobot.getOrientation() + 3) % 4;
        } else if (mDirection == MIDDLE) {
            actualDirection = mRobot.getOrientation();
        } else if (mDirection == RIGHT) {
            actualDirection = (mRobot.getOrientation() + 1) % 4;
        }
        return actualDirection;
    }

    int getActualPosX() {
        if (mRobot.getOrientation() == NORTH) {
            return getCorrectedRobotPosX() + mPosX;
        } else if (mRobot.getOrientation() == EAST) {
            return getCorrectedRobotPosX() - mPosY;
        } else if (mRobot.getOrientation() == SOUTH) {
            return getCorrectedRobotPosX() - mPosX;
        } else if (mRobot.getOrientation() == WEST) {
            return getCorrectedRobotPosX() + mPosY;
        }
        return 0;
    }

    int getActualPosY() {
        if (mRobot.getOrientation() == NORTH) {
            return getCorrectedRobotPosY() + mPosY;
        } else if (mRobot.getOrientation() == EAST) {
            return getCorrectedRobotPosY() + mPosX;
        } else if (mRobot.getOrientation() == SOUTH) {
            return getCorrectedRobotPosY() - mPosY;
        } else if (mRobot.getOrientation() == WEST) {
            return getCorrectedRobotPosY() - mPosX;
        }
        return 0;
    }

    /**
     * As the sensor location is relative to the robot, the corrected robot location
     * (i.e. considering heading) needs to be calculated to be the base location for
     * calculating sensor location
     * @return X coordinate for sensor calculation
     */
    private int getCorrectedRobotPosX() {
        /*
         * ROBOT'S MOST WEST POINT IN THE ARENA
         * THEREFORE, MUST ADD 2 WHEN FACING EAST (FOR SENSORS IN MIDDLE)
         * THEREFORE, MUST ADD 2 WHEN FACING SOUTH (FOR SENSORS IN RIGHT)
         */
        if (mRobot.getOrientation() == EAST || mRobot.getOrientation() == SOUTH)
            return mRobot.getPosX() + 2;
        return mRobot.getPosX();
    }

    /**
     * As the sensor location is relative to the robot, the corrected robot location
     * (i.e. considering heading) needs to be calculated to be the base location for
     * calculating sensor location
     * @return Y coordinate for sensor calculation
     */
    private int getCorrectedRobotPosY() {
        /*
         * ROBOT'S MOST NORTH POINT IN THE ARENA
         * THEREFORE, MUST ADD 2 WHEN FACING SOUTH (FOR SENSORS IN MIDDLE)
         * THEREFORE, MUST ADD 2 WHEN FACING WEST (FOR SENSORS IN RIGHT)
         */
        if (mRobot.getOrientation() == SOUTH || mRobot.getOrientation() == WEST)
            return mRobot.getPosY() + 2;
        return mRobot.getPosY();
    }

    public int getReliability() {
        return mReliability;
    }

    int getRange() {
        return mRange;
    }

    public void setRobot(Robot robot) {
        mRobot = robot;
    }
}
