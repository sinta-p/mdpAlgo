package simInterface;


import algo.*;
import entities.GridMap;
import entities.Robot;
import connection.CommMgr;
import connection.SocketMgr;
import simInterface.Simulator;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Created by koallen on 11/10/17.
 */
public class RealRunButtonListener implements ActionListener {

    private Simulator mView;
    private GridMap mGrid;
    private Robot mRobot;

    public RealRunButtonListener(Simulator view, Robot robot, GridMap grid) {
        mView = view;
        mGrid = grid;
        mRobot = robot;
        mView.addRealRunButtonListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Physical run button pressed, real run: " + mView.getIsRealRun());
        if (mView.getIsRealRun()) {
            if (mView.getRobotSpeed() == 0) {
                JOptionPane.showMessageDialog(null, "Please set robot speed (X Steps per second)!", "Fastest path", JOptionPane.ERROR_MESSAGE);
            }
            mView.disableButtons();
            new PhysicalRunWorker().execute();
        }
    }

    class PhysicalRunWorker extends SwingWorker<Integer, Integer> {

        @Override
        protected Integer doInBackground() throws Exception {
            // receive way point
            String msg = SocketMgr.getInstance().receiveMessage(false);
            List<Integer> waypoints;
            while ((waypoints = CommMgr.parseMessage(msg)) == null) {
                msg = SocketMgr.getInstance().receiveMessage(false);
            }

            // do exploration
            AlgorithmRunner explorationRunner = new ExplorationAlgorithmRunner(mView.getRobotSpeed());
            explorationRunner.run(mGrid, mRobot, mView.getIsRealRun());

            // do fastest path
            AlgorithmRunner fastestPathRunner = new FastestPathAlgorithmRunner(mView.getRobotSpeed(),
                    waypoints.get(0) - 1, waypoints.get(1) - 1);
//            0, 17);
            fastestPathRunner.run(mGrid, mRobot, mView.getIsRealRun());

            return 1;
        }

        @Override
        protected void done() {
            super.done();
            mView.enableButtons();
        }
    }
}

