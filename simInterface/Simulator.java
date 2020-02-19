package simInterface;

import entities.Robot;
import entities.GridMap;
import static constant.EntitiesConstants.*;
import entities.GridBox;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;


public class Simulator extends JFrame {

    // Swing components
    private JPanel mMapPanel;
    private JButton mExplorationButton;
    private JButton mFastestPathButton;
    private JButton mLoadMapButton;
    private JButton mTimeLimitedButton;
    private JButton mCoverageLimitedButton;
    private JButton mPrintHexButton;
    private JButton mRealRunButton;
    private JCheckBox mRealRunCheckBox;
    private JFormattedTextField mRobotSpeedField;

    // model
    private GridMap mSimulationGrid;
    private Robot mSimulationRobot;
    private GridBox[][] mSimulationBox;

    public Simulator(GridMap grid, Robot robot) {
        mSimulationGrid = grid;
        mSimulationRobot = robot;
        mSimulationBox = grid.getCells();
        initializeUi();
    }

    private void initializeUi() {
        // create components
        mMapPanel = new MapPanel(mSimulationGrid, mSimulationRobot);
        mPrintHexButton = new JButton("Print Hex");
        mExplorationButton = new JButton("Exploration");
        mFastestPathButton = new JButton("Fastest path");
        mLoadMapButton = new JButton("Load map");
        mTimeLimitedButton = new JButton("Time limited");
        mCoverageLimitedButton = new JButton("Coverage limited");
        mRealRunButton = new JButton("Physical run");
        mRealRunCheckBox = new JCheckBox("Real run");
        mRealRunCheckBox.setHorizontalTextPosition(SwingConstants.LEFT);
        mRobotSpeedField = new JFormattedTextField(NumberFormat.getIntegerInstance());
        mRobotSpeedField.setPreferredSize(new Dimension(50, mRobotSpeedField.getHeight()));

        // set up as observer
        //mSimulationRobot.addObserver((Observer) mMapPanel);
        //mSimulationGrid.addObserver((Observer) mMapPanel);
        mSimulationRobot.addPropertyChangeListener((PropertyChangeListener) mMapPanel);
        for(int a = 0; a< MAP_ROWS; a++)
            for(int b=0; b< MAP_COLS ; b++) {
                mSimulationBox[b][a].addPropertyChangeListener((PropertyChangeListener) mMapPanel);
            }


        // layout components
        JPanel wrapper = new JPanel(new FlowLayout());
        wrapper.add(mMapPanel);
        this.add(wrapper, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        bottomPanel.add(mRealRunCheckBox);
        bottomPanel.add(new JLabel("Speed"));
        bottomPanel.add(mRobotSpeedField);
        bottomPanel.add(mPrintHexButton);
        bottomPanel.add(mRealRunButton);
        bottomPanel.add(mExplorationButton);
        bottomPanel.add(mFastestPathButton);
        bottomPanel.add(mTimeLimitedButton);
        bottomPanel.add(mCoverageLimitedButton);
        bottomPanel.add(mLoadMapButton);
        this.add(bottomPanel, BorderLayout.PAGE_END);

        // set up the frame
        pack();
        setTitle("MDP Simulator");
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public void addExplorationButtonListener(ActionListener actionListener) {
        mExplorationButton.addActionListener(actionListener);
    }

    public void addPrintHexButtonListener(ActionListener actionListener) {
        mPrintHexButton.addActionListener(actionListener);
    }

    public void addFastestPathButtonListener(ActionListener actionListener) {
        mFastestPathButton.addActionListener(actionListener);
    }

    public void addLoadMapButtonListener(ActionListener actionListener) {
        mLoadMapButton.addActionListener(actionListener);
    }

    public void addTimeLimitedButtonListener(ActionListener actionListener) {
        mTimeLimitedButton.addActionListener(actionListener);
    }

    public void addCoverageLimitedButtonListener(ActionListener actionListener) {
        mCoverageLimitedButton.addActionListener(actionListener);
    }

    public void addRealRunCheckBoxListener(ActionListener actionListener) {
        mRealRunCheckBox.addActionListener(actionListener);
    }

    public void addRealRunButtonListener(ActionListener actionListener) {
        mRealRunButton.addActionListener(actionListener);
    }

    public void disableButtons() {
        mExplorationButton.setEnabled(false);
        mFastestPathButton.setEnabled(false);
        mLoadMapButton.setEnabled(false);
        mTimeLimitedButton.setEnabled(false);
        mCoverageLimitedButton.setEnabled(false);
    }

    public void enableButtons() {
        mExplorationButton.setEnabled(true);
        mFastestPathButton.setEnabled(true);
        mLoadMapButton.setEnabled(true);
        mTimeLimitedButton.setEnabled(true);
        mCoverageLimitedButton.setEnabled(true);
    }

    public void disableLoadMapButton() {
        mLoadMapButton.setEnabled(false);
    }

    public void enableLoadMapButton() {
        mLoadMapButton.setEnabled(true);
    }

    public void disableRealRunButton() {
        mRealRunButton.setEnabled(false);
    }

    public void enableRealRunButton() {
        mRealRunButton.setEnabled(true);
    }

    public void disableFastestPathButton() {
        mFastestPathButton.setEnabled(false);
    }

    public void enableFastestPathButton() {
        mFastestPathButton.setEnabled(true);
    }

    public void disableExplorationButton() {
        mExplorationButton.setEnabled(false);
    }

    public void enableExplorationButton() {
        mExplorationButton.setEnabled(true);
    }

    public boolean getIsRealRun() {
        return mRealRunCheckBox.isSelected();
    }

    public int getRobotSpeed() {
        return mRobotSpeedField.getText().equals("") ? 0 : Integer.parseInt(mRobotSpeedField.getText());
    }
}

