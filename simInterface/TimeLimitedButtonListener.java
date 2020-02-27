package simInterface;

import entities.GridMap;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;
import entities.Robot;
import algo.*;

public class TimeLimitedButtonListener implements ActionListener {

    private Simulator mView;
    private GridMap mGrid;
    private Robot mRobot;

    public TimeLimitedButtonListener(Simulator view, Robot robot, GridMap grid) {
        mView = view;
        mGrid = grid;
        mRobot = robot;
        mView.addTimeLimitedButtonListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Time limited button pressed");
        if (mView.getRobotSpeed() == 0) {
            JOptionPane.showMessageDialog(null, "Please set robot speed! (X Steps per second)", "Fastest path", JOptionPane.ERROR_MESSAGE);
        }
        mView.disableButtons();
        new TimeLimitedWorker().execute();
    }

    class TimeLimitedWorker extends SwingWorker<Integer, Integer> {

        @Override
        protected Integer doInBackground() throws Exception {
            AlgorithmRunner algorithmRunner = new TimeExplorationAlgorithmRunner(mView.getRobotSpeed());
            algorithmRunner.run(mGrid, mRobot, mView.getIsRealRun());
            return 1;
        }

        @Override
        protected void done() {
            super.done();
            mView.enableButtons();
        }
    }
}
