package algo;
import static constant.EntitiesConstants.*;

import java.util.ArrayList;
import java.util.List;

import entities.GridMap;
import entities.Robot;
import entities.Sensor;
import simInterface.Simulator;
import simInterface.ConnectButtonListener;
import simInterface.LoadMapButtonListener;
import simInterface.PrintHexButtonListener;
import simInterface.ExplorationButtonListener;
import simInterface.FastestPathButtonListener;

public class MdpApp {
	public static void main(String args[]) {
		
		GridMap test = new GridMap();
		
		test.presetObstacles();
		test.printMapObstacle();
		
		GridMap grid = new GridMap();
		//setObstaclesMap(grid);
		Sensor sensor4 = new Sensor(2, 0, 0, LEFT, 3);
		Sensor sensor5 = new Sensor(2, 2, 0, RIGHT, 3);
		Sensor sensor3 = new Sensor(2, 0, 0, MIDDLE, 5);
		Sensor sensor1 = new Sensor(2, 2, 0, MIDDLE, 5);
		Sensor sensor2 = new Sensor(2, 1, 0, MIDDLE, 3);
		Sensor sensor6 = new Sensor(4, 4, 1, RIGHT, 1);
		List<Sensor> sensors = new ArrayList<>();
		sensors.add(sensor1);
		sensors.add(sensor2);
		sensors.add(sensor3);
		sensors.add(sensor4);
		sensors.add(sensor5);
		sensors.add(sensor6);
		Robot robot = new Robot(grid, sensors);
		Simulator simulator = new Simulator(grid, robot);
		
		new ConnectButtonListener(simulator);
		new PrintHexButtonListener(simulator,grid,robot);
		new LoadMapButtonListener(simulator,robot,grid);
		new ExplorationButtonListener(simulator,robot,grid);
		new FastestPathButtonListener(simulator,robot,grid);
		

		
		simulator.setVisible(true);
        System.out.println("Simulator started.");
	}

}
