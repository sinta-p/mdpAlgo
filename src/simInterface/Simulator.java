package simInterface;

import entities.Robot;
import entities.GridBox;
import entities.GridMap;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import static constant.EntitiesConstants.*;


public class Simulator extends JFrame {

	private JPanel mapPanel;
	private JButton connectionButton;
	private JButton printHexButton;
	private JCheckBox testCheckBox; 
	private JButton loadMapButton;
	
	private GridMap simMap;
	private Robot simRobot;
	private GridBox[][] simBox;
	
    public Simulator(GridMap grid, Robot robot) {
        simMap = grid;
        simRobot = robot;
        simBox = grid.getCells();
        initializeUi();
    }
    
    private void initializeUi(){
    	mapPanel = new MapPanel(simMap, simRobot);
    	connectionButton = new JButton("connect");
    	testCheckBox = new JCheckBox("test");
        testCheckBox.setHorizontalTextPosition(SwingConstants.LEFT);
    	printHexButton = new JButton("print Hex");
    	loadMapButton = new JButton("Load Map");
    	
    	
    	simRobot.addPropertyChangeListener((PropertyChangeListener) mapPanel);
    	for(int a = 0; a< MAP_ROWS; a++)
			for(int b=0; b< MAP_COLS ; b++)
				simBox[b][a].addPropertyChangeListener((PropertyChangeListener) mapPanel);
			
    	JPanel wrapper = new JPanel(new FlowLayout());
        wrapper.add(mapPanel);
        this.add(wrapper, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        bottomPanel.add(connectionButton);
        bottomPanel.add(testCheckBox);
        bottomPanel.add(printHexButton);
        bottomPanel.add(loadMapButton);
        this.add(bottomPanel, BorderLayout.PAGE_END);
        
        pack();
        setTitle("SIN Simulator");
        setResizable(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
    }
    
    public void addconnectButtonListener(ActionListener actionListener) {
        connectionButton.addActionListener(actionListener);
    }

    public void addprintHexButtonListener(ActionListener actionListener) {
        printHexButton.addActionListener(actionListener);
    }
    
    public void addloadMapButtonListener(ActionListener actionListener) {
        loadMapButton.addActionListener(actionListener);
    }
    
    
  }
   
  
