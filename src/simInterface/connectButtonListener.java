package simInterface;

import java.awt.event.*;
import java.net.ServerSocket;

import javax.swing.*;

import connection.SocketMgr;


public class connectButtonListener implements ActionListener{
	
	private Simulator sim;
	
	public connectButtonListener(Simulator curSim) {
		sim = curSim;
		sim.addconnectButtonListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("Let's Connect");
		new ConnectWorker().execute();
	}
	
	class ConnectWorker extends SwingWorker<Integer,Integer>{
		
		@Override
	    protected Integer doInBackground() throws Exception {
	        SocketMgr connection = new SocketMgr();
	        connection.openConnection();
	        connection.sendMessage("P", "HiRpi");
	        connection.receiveMessage(true);
	        return 1;
	    }
	
	    @Override
	    protected void done() {
	        super.done();
	    }
	}

}
