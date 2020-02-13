package simInterface;

import java.awt.event.*;
import static constant.EntitiesConstants.*;

import javax.swing.*;

import connection.SocketMgr;
import entities.GridMap;

public class printHexButtonListener implements ActionListener {
	

	private Simulator cSim;
	private GridMap cGrid;
	private String obstacleString="";
	private String exploredString="";

	
	public printHexButtonListener(Simulator curSim,GridMap grid) {
		cSim = curSim;
		cGrid =grid;
		cSim.addprintHexButtonListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("Hex: ");
			
		for(int y = 0; y< MAP_ROWS; y++) {
			for(int x = 0; x< MAP_COLS ;x++) {
				exploredString+=(cGrid.getIsExplored(x, y)?1:1);
			}
		}
		System.out.println(hexConverter(exploredString));

		for(int y = 0; y< MAP_ROWS; y++) {
			for(int x = 0; x< MAP_COLS ;x++) {
				obstacleString+=(cGrid.getIsObstacle(x, y)?1:0);
			}
		}
		System.out.println(hexConverter(obstacleString));

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
