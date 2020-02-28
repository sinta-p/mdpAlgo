package simInterface;

import java.awt.event.*;
import static constant.EntitiesConstants.*;

import javax.swing.*;

import connection.SocketMgr;
import entities.GridMap;
import entities.Robot;

public class PrintHexButtonListener implements ActionListener {
	

	private Simulator cSim;
	private GridMap cGrid;
	private Robot cRobot;
	private String obstacleString = "";
	private String exploredString = "";


	private StringBuilder finalOutput;
	
	
	public PrintHexButtonListener(Simulator curSim, GridMap grid, Robot robot) {
		cSim = curSim;
		cGrid =grid;
		cRobot = robot;
		cSim.addPrintHexButtonListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {

		exploredString = "11";
		obstacleString = "";
		for(int y = MAP_ROWS-1; y >=0; y--) {
			for(int x = 0; x< MAP_COLS ;x++) {
				exploredString+=(cGrid.getIsExplored(x, y)?1:0);
			}
		}

		for(int y = MAP_ROWS-1; y >= 0; y--) {
			for(int x = 0; x< MAP_COLS ;x++) {
				if(cGrid.getIsExplored(x,y))
					obstacleString+=(cGrid.getIsObstacle(x, y)?1:0);
			}
		}

		while(0!= obstacleString.length()%8){
			obstacleString+="0";
		}

		exploredString+=(11);
		
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
