package simInterface;


import entities.GridMap;
import entities.Robot;
import algo.AlgorithmRunner;
import algo.FastestPathAlgorithmRunner;

import java.awt.event.*;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

public class FastestPathButtonListener implements ActionListener {

    private Simulator mView;
    private GridMap mGrid;
    private Robot mRobot;

    public FastestPathButtonListener(Simulator view, Robot robot, GridMap grid) {
        mView = view;
        mGrid = grid;
        mRobot = robot;
        mView.addFastestPathButtonListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Fastest path button pressed");
        if (mView.getRobotSpeed() == 0) {
            JOptionPane.showMessageDialog(null, "Please set robot speed! (X Steps per second)", "Fastest path", JOptionPane.ERROR_MESSAGE);
        }
        mView.disableButtons();
        new FastestPathWorker().execute();
    }

    class FastestPathWorker extends SwingWorker<Integer, Integer> {

        @Override
        protected Integer doInBackground() throws Exception {
            System.out.println("Worker started");
            AlgorithmRunner algorithmRunner = new FastestPathAlgorithmRunner(mView.getRobotSpeed());
            algorithmRunner.run(mGrid, mRobot, mView.getIsRealRun());
            return 1;
        }

        @Override
        protected void done() {
            super.done();
            System.out.println("Worker finished");
            mView.enableButtons();
        }
    }
}
