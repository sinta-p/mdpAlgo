package simInterface;

import java.awt.event.*;
import static constant.EntitiesConstants.*;

import javax.swing.*;

import connection.SocketMgr;
import entities.GridMap;
import entities.Robot;

public class printHexButtonListener implements ActionListener {
	

	private Simulator cSim;
	private GridMap cGrid;
	private Robot cRobot;
	private String obstacleString="";
	private String exploredString="";
	private StringBuilder finalOutput;
	
	
	public printHexButtonListener(Simulator curSim,GridMap grid, Robot robot) {
		cSim = curSim;
		cGrid =grid;
		cRobot = robot;
		cSim.addprintHexButtonListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		for(int y = 0; y< MAP_ROWS; y++) {
			for(int x = 0; x< MAP_COLS ;x++) {
				exploredString+=(cGrid.getIsExplored(x, y)?1:0);
			}
		}

		for(int y = 0; y< MAP_ROWS; y++) {
			for(int x = 0; x< MAP_COLS ;x++) {
				obstacleString+=(cGrid.getIsObstacle(x, y)?1:0);
			}
		}
		
		finalOutput = new StringBuilder("A|");
		finalOutput.append(cRobot.getPosX());
		finalOutput.append("|");
		finalOutput.append(cRobot.getPosY());
		finalOutput.append("|");
		finalOutput.append(cRobot.getOrientation());
		finalOutput.append("|");
		finalOutput.append(hexConverter(exploredString));
		finalOutput.append("|");
		finalOutput.append(hexConverter(obstacleString));
		System.out.println(finalOutput);


	}
	
	public String hexConverter(String bString) {
		
		String binaryString = bString;
		String finalString = "";
		int digitNumber = 1;
		int sum = 0;
		for(int i = 0; i < binaryString.length(); i++){
	        if(digitNumber == 1)
	            sum+=Integer.parseInt(binaryString.charAt(i) + "")*8;
	        else if(digitNumber == 2)
	            sum+=Integer.parseInt(binaryString.charAt(i) + "")*4;
	        else if(digitNumber == 3)
	            sum+=Integer.parseInt(binaryString.charAt(i) + "")*2;
	        else if(digitNumber == 4 || i < binaryString.length()+1){
	            sum+=Integer.parseInt(binaryString.charAt(i) + "")*1;
	            digitNumber = 0;
	            if(sum < 10)
	                finalString+=String.valueOf(sum);
	            else if(sum == 10)
	                finalString+="A";
	            else if(sum == 11)
	                finalString+="B";
	            else if(sum == 12)
	                finalString+="C";
	            else if(sum == 13)
	                finalString+="D";
	            else if(sum == 14)
	                finalString+="E";
	            else if(sum == 15)
	                finalString+="F";
	            sum=0;
	        }
	        digitNumber++;  
		}
		return finalString;
	
		
	}
}
