package simInterface;


import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import java.io.IOException;
import entities.GridMap;
import entities.Robot;


public class LoadMapButtonListener implements ActionListener{


	private Simulator curSim;
	private GridMap curGrid;
	private Robot curRobot;
	
	public LoadMapButtonListener(Simulator sim, Robot robot, GridMap grid) {
		curSim = sim;
		curRobot = robot;
		curGrid = grid;
		sim.addLoadMapButtonListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("LOADMAP PRESSED ");
		new ConnectWorker().execute();
	}
	
	class ConnectWorker extends SwingWorker<Integer,Integer>{
		
		@Override
	    protected Integer doInBackground() throws Exception {
			String file_path = JOptionPane.showInputDialog(null,"Please input a map.","Map Loader", JOptionPane.QUESTION_MESSAGE);
			if(null!=file_path) {
				try {
					curGrid.loadFromDisk("maps/" + file_path);
					System.out.println("Loaded map" + file_path);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
	                JOptionPane.showMessageDialog(null, "The file doesn't exist!", "Map loader", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
				
			}
	        return 1;
	    }
	
	    @Override
	    protected void done() {
	        super.done();
	    }
	}
	
	
}
