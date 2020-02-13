package entities;

import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static constant.EntitiesConstants.*;



public class GridMap {
	private int rows = MAP_ROWS;
	private int cols = MAP_COLS;
	private GridBox[][] map;
	
	
	
	//constructors
	public GridMap() {
		map = new GridBox[MAP_COLS][MAP_ROWS];
        for (int x = 0; x < MAP_COLS; x++) {
            for (int y = 0; y < MAP_ROWS; y++) {
                map[x][y] = new GridBox(x,y);
            }
        }
        reset();
	}
	
	//getters & setters

    public GridBox[][] getCells() {
        return map;
    }
    

    public static boolean isInStartZone(int x, int y) {
        return x < ZONE_SIZE && x >= 0
                && y < MAP_ROWS && y >= MAP_ROWS - ZONE_SIZE;
    }

    public static boolean isInEndZone(int x, int y) {
        return x < MAP_COLS && x >= MAP_COLS - ZONE_SIZE
                && y < ZONE_SIZE && y >= 0;
    }

    public boolean getIsObstacle(int x, int y) {
        return isOutOfArena(x, y) || map[x][y].getIsObstacle();
    }

    public boolean isOutOfArena(int x, int y) {
        return x < 0 || y < 0 || x >= MAP_COLS || y >= MAP_ROWS;
    }

    public void setIsObstacle(int x, int y, boolean isObstacle) {
        if (isOutOfArena(x, y))
            return;
        map[x][y].setObstacle(isObstacle);
        
    }

    public void setExplored(int x, int y, boolean explored) {
        if (isOutOfArena(x, y))
            return;
        map[x][y].setVisited(explored);
    }

    public boolean getIsExplored(int x, int y) {
        return !isOutOfArena(x, y) && map[x][y].getIsVisited();
    }
	
	
	//preset of obstacle
	public void presetObstacles() {
		
		int[][] preset = {
				{ 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0},
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},	
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{ 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
				{ 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1},
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},	
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1},
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1},
				{ 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
				{ 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
				{ 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0},
				{ 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0},
		};
		
		for (int prow = 0; prow <rows; prow++ ) {
			for(int pcol = 0; pcol < cols; pcol++) {
				if (preset[prow][pcol] == 1)
					map[pcol][prow].setObstacle(true);			
			}
		}
		
		
	}
	
	public void printMapObstacle() {
		//border top settings
		for(int bt = 0; bt<cols+2 ; bt++) {
			System.out.print("_");
		}
		
		
		for(int r = 0; r<rows; r++) {
			System.out.println();
			System.out.print("|");
			for(int c=0; c<cols ; c++)
				map[c][r].printGrid();
			System.out.print("|");

		}

		//border bottom settings
		System.out.println();
		for(int bt = 0; bt<cols+2 ; bt++) {
			System.out.print("_");
		}
	}
	
	
	public void setObstacles() {
		int[][] obs = {
				{1,2},
				{1,3},
				{1,4},
				{3,2},
				{2,3},
		};
		
		for(int i = 0; i < obs.length; i++) {
			int r=obs[i][0];
			int c=obs[i][1];
			map[r][c].setObstacle(true);
			
				
		}
	}
	
	 public double checkExploredPercentage() {
        double totalCells = 0.0;
        double cellsExplored = 0.0;
        for (int x = 0; x < MAP_COLS; x++) {
            for (int y = 0; y < MAP_ROWS; y++) {
                if (map[x][y].getIsVisited()) {
                    cellsExplored += 1;
                }
                totalCells += 1;
            }
        }
        return (cellsExplored / totalCells) * 100;
    }
	 
	 public void reset() {
        for (int x = 0; x < MAP_COLS; x++) {
            for (int y = 0; y < MAP_ROWS; y++) {
                if (!isInStartZone(x, y) && !isInEndZone(x, y))
                    setExplored(x, y, false);
                else
                    setExplored(x, y, true);
            }
        }
    }

    public void loadFromDisk(String path) throws IOException {
    	this.reset();

        BufferedReader reader = new BufferedReader(new FileReader(path));

        for (int i = 0; i < MAP_ROWS; i++) {
            String line = reader.readLine();
            String[] numberStrings = line.trim().split("\\s+");
            for (int j = 0; j < MAP_COLS; j++) {
                if (numberStrings[j].equals("1")) {
                    this.setIsObstacle(j, i, true);
                } else {
                    this.setIsObstacle(j, i, false);
                }
            }
        }
    }

    //for real run
    public void setObstacleProbability(int x, int y, int value) {
        if (isOutOfArena(x, y))
            return;
        map[x][y].updateCounter(value);
    }
	//line 107 mdp_a onwards
}
