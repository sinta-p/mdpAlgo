package entities;

import java.util.List;
import constant.EntitiesConstants;
import connection.SocketMgr;
import static constant.EntitiesConstants.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;



//problem lacks the observed and observable class

public class Robot {
	
	private int posX = START_POS_X;		// top left gridbox
	private int posY = START_POS_Y;
	private int orientation = NORTH;	//North = 1, East = 2, South = 3, West =4
	private GridMap map;
	private List<Sensor> allSensors;
	private PropertyChangeSupport support;
	
	
	
	public Robot(GridMap grid, List<Sensor> sensors) {
        map = grid;
        allSensors = sensors;
        for (Sensor sensor : sensors) {
            sensor.setRobot(this);
        }
        support = new PropertyChangeSupport(this);
    }
	

    public boolean isInRobot(int x, int y) {
        return x < getPosX()+2 && x >= getPosX()
                && y < getPosY()+2 && y >= getPosY();
    }
    

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosX(int x) {
       posX = x;
    }

    public void setPosY(int y) {
        posY = y;
    }

    public int getCenterPosX() {
        return posX + 1;
    }

    public int getCenterPosY() {
        return posY + 1;
    }

    public int getOrientation() {
        return orientation;
    }

    public boolean canCalibrateFront() { return true; }//TODO:

    public boolean canCalibrateLeft() {//TODO:
	    return true;
    }

    public void setOrientation(int direction) {
    	orientation = direction;
    }
    
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }
 
    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }
    

    public boolean isObstacleAhead() {
        for (int i = 0; i < ROBOT_SIZE; i++) {
            if (orientation == NORTH) {
                // DIRECTLY IN FRONT OF ROBOT
                if (map.getIsObstacle(posX + i, posY - 1)) {
                    return true;
                }
            } else if (orientation == SOUTH) {
                // DIRECTLY IN FRONT OF ROBOT
                if (map.getIsObstacle(posX + i, posY + 3)) {
                    return true;
                }
            } else if (orientation == EAST) {
                // DIRECTLY IN FRONT OF ROBOT
                if (map.getIsObstacle(posX + 3, posY + i)) {
                    return true;
                }
            } else if (orientation == WEST) {
                // DIRECTLY IN FRONT OF ROBOT
                if (map.getIsObstacle(posX - 1, posY + i)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isObstacleRight() {
        for (int i = 0; i < ROBOT_SIZE; i++) {
            if (orientation == NORTH) {
                // DIRECTLY BESIDE OF ROBOT
                if (map.getIsObstacle(posX + 3, posY + i)) {
                    return true;
                }
            } else if (orientation == SOUTH) {
                // DIRECTLY BESIDE OF ROBOT
                if (map.getIsObstacle(posX - 1, posY + i)) {
                    return true;
                }
            } else if (orientation == EAST) {
                // DIRECTLY BESIDE OF ROBOT
                if (map.getIsObstacle(posX + i, posY + 3)) {
                    return true;
                }
            } else if (orientation == WEST) {
                // DIRECTLY BESIDE OF ROBOT
                if (map.getIsObstacle(posX + i, posY - 1)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isObstacleLeft() {
        for (int i = 0; i < ROBOT_SIZE; i++) {
            if (orientation == NORTH) {
                // DIRECTLY BESIDE OF ROBOT
                if (map.getIsObstacle(posX - 1, posY + i)) {
                    return true;
                }
            } else if (orientation == SOUTH) {
                // DIRECTLY BESIDE OF ROBOT
                if (map.getIsObstacle(posX + 3, posY + i)) {
                    return true;
                }
            } else if (orientation == EAST) {
                // DIRECTLY BESIDE OF ROBOT
                if (map.getIsObstacle(posX + i, posY - 1)) {
                    return true;
                }
            } else if (orientation == WEST) {
                // DIRECTLY BESIDE OF ROBOT
                if (map.getIsObstacle(posX + i, posY + 3)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    
    
	
	//discuss degree of movement: turn left, turn right etc. 
	public void move() {
		int oldX = posX ;
		int oldY = posY ;
		
		if (orientation == NORTH) { // Limit position to prevent wall crash
            posY--;
            for (int i = 0; i < 3; ++i) {
                map.setIsObstacle(posX + i, posY, false);
                map.setExplored(posX + i, posY, true);
            }
        } else if (orientation == SOUTH) {// Limit position to prevent wall crash
            posY++;
            for (int i = 0; i < 3; ++i) {
                map.setIsObstacle(posX + i, posY + 2, false);
                map.setExplored(posX + i, posY + 2, true);
            }
        } else if (orientation == WEST) { // Limit position to prevent wall crash
            posX--;
            for (int i = 0; i < 3; ++i) {
                map.setIsObstacle(posX, posY + i, false);
                map.setExplored(posX, posY + i, true);
            }
        } else if (orientation == EAST) { // Limit position to prevent wall crash
            posX++;
            for (int i = 0; i < 3; ++i) {
                map.setIsObstacle(posX + 2, posY + i, false);
                map.setExplored(posX + 2, posY + i, true);
            }
        }
		
		support.firePropertyChange("posX",oldX,posX);
		support.firePropertyChange("posY",oldY,posY);

		
		
		
	}
	
	public void turn(int direction) {

		int oldOrientation = orientation ;
		
		if(direction == LEFT) 
			orientation = (orientation +3) % 4;		
		
		else if (direction == RIGHT)
			orientation = (orientation+1) % 4 ;
		
		support.firePropertyChange("orientation",oldOrientation,orientation);

	}
	
	 public void reset() {
		 support.firePropertyChange("posX", this.posX, START_POS_X);
		 support.firePropertyChange("posY", this.posY, START_POS_Y);
		 support.firePropertyChange("orientation", this.orientation, NORTH);
	 }

	
	 /**
     * Updates the simulator's map according to sensor reading. If the sensor reading
     * is smaller or equal to its range, it means there is an obstacle at that distance.
     * If the sensor reading is greater than its range, it means there is no obstacle
     * within its detectable range.
     * @param returnedDistance
     * @param heading
     * @param range
     * @param x
     * @param y
     */
    private void updateMap(int returnedDistance, int heading, int range, int x, int y, boolean realRun, int reliability) {
        int xToUpdate = x, yToUpdate = y;
        int distance = Math.min(returnedDistance, range);
        boolean obstacleAhead = returnedDistance <= range;

        for (int i = 1; i <= distance; i++) {
            if (heading == NORTH) {
                yToUpdate = yToUpdate - 1;
            } else if (heading == SOUTH) {
                yToUpdate = yToUpdate + 1;
            } else if (heading == WEST) {
                xToUpdate = xToUpdate - 1;
            } else if (heading == EAST) {
                xToUpdate = xToUpdate + 1;
            }
            map.setExplored(xToUpdate, yToUpdate, true);
            // if this cell is an obstacle
            if (i == distance && obstacleAhead) {
                if (realRun) {
                    map.setObstacleProbability(xToUpdate, yToUpdate, reliability); // increment by reliability
                } else {
                    map.setIsObstacle(xToUpdate, yToUpdate, true);
                }
            } else { // if this cell is not an obstacle
                if (realRun) {
                    map.setObstacleProbability(xToUpdate, yToUpdate, -reliability); // decrement by reliability
                } else {
                    map.setIsObstacle(xToUpdate, yToUpdate, false);
                }
            }
        }
    }
	

    public void sense(boolean realRun) {
        if (realRun) {
            SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "S");
            String sensorData = SocketMgr.getInstance().receiveMessage(true);
            
            //while sensor never reply, send message again
            while (sensorData == null) {
                SocketMgr.getInstance().sendMessage(TARGET_ARDUINO, "S");
                sensorData = SocketMgr.getInstance().receiveMessage(true);
            }
            
            //
            String[] sensorReadings = sensorData.split(",", allSensors.size());
            for (int i = 0; i < allSensors.size(); i++) {
                int returnedDistance = Integer.parseInt(sensorReadings[i]);
                int heading = allSensors.get(i).getActualOrientation();
                int range = allSensors.get(i).getRange();
                int x = allSensors.get(i).getActualPosX();
                int y = allSensors.get(i).getActualPosY();
                updateMap(returnedDistance, heading, range, x, y, true, allSensors.get(i).getReliability());
            }
        } else {
            for (Sensor sensor : allSensors) {
                int returnedDistance = sensor.sense(map);
                int heading = sensor.getActualOrientation();
                int range = sensor.getRange();
                int x = sensor.getActualPosX();
                int y = sensor.getActualPosY();
                updateMap(returnedDistance, heading, range, x, y, false, sensor.getReliability());
            }
        }
    }

}
