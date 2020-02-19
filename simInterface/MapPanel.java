package simInterface;

import entities.Robot;
import entities.GridBox;
import entities.GridMap;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static constant.EntitiesConstants.*;


public class MapPanel extends JPanel implements PropertyChangeListener{
	
	private GridMap mGrid;
    private Robot mRobot;

    MapPanel(GridMap grid, Robot robot) {
        mGrid = grid;
        mRobot = robot;
        initializeMap();
    }

    private void initializeMap() {
        setPreferredSize(new Dimension(CELL_SIZE * MAP_COLS, CELL_SIZE * MAP_ROWS));
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        
        /* paint map */
        GridBox[][] cells = mGrid.getCells();
        for (int x = 0; x < MAP_COLS; x++) {
            for (int y = 0; y < MAP_ROWS; y++) {
                /* draw cells */
                if (GridMap.isInStartZone(x, y))
                    g2d.setColor(Color.YELLOW);
                else if (GridMap.isInEndZone(x, y))
                    g2d.setColor(Color.BLUE);
                else if (cells[x][y].getIsVisited()) {
                    if (cells[x][y].getIsObstacle())
                        g2d.setColor(Color.BLACK);
                    else
                        g2d.setColor(Color.WHITE);
                } else {
                    g2d.setColor(Color.LIGHT_GRAY);
                }
                g2d.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);

                /* draw border */
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }

        /* paint robot */
        g2d.setColor(Color.BLACK);
        g2d.fillOval(mRobot.getPosX() * CELL_SIZE + PAINT_PIXEL_OFFSET,
                mRobot.getPosY() * CELL_SIZE + PAINT_PIXEL_OFFSET,
               CELL_SIZE * ROBOT_SIZE - 2 * PAINT_PIXEL_OFFSET,
                CELL_SIZE * ROBOT_SIZE - 2 * PAINT_PIXEL_OFFSET);

        /* paint robot heading */
        g2d.setColor(Color.WHITE);
        if (mRobot.getOrientation() == NORTH) {
            g2d.fillOval((mRobot.getPosX() + 1) * CELL_SIZE + (CELL_SIZE - HEADING_PIXEL_SIZE) / 2,
                    mRobot.getPosY() * CELL_SIZE + PAINT_PIXEL_OFFSET,
                    HEADING_PIXEL_SIZE, HEADING_PIXEL_SIZE);
        } else if (mRobot.getOrientation() == SOUTH) {
            g2d.fillOval((mRobot.getPosX() + 1) * CELL_SIZE + (CELL_SIZE - HEADING_PIXEL_SIZE) / 2,
                    (mRobot.getPosY() + 2) * CELL_SIZE + CELL_SIZE - HEADING_PIXEL_SIZE - PAINT_PIXEL_OFFSET,
                    HEADING_PIXEL_SIZE, HEADING_PIXEL_SIZE);
        } else if (mRobot.getOrientation() == WEST) {
            g2d.fillOval(mRobot.getPosX() * CELL_SIZE + PAINT_PIXEL_OFFSET,
                    (mRobot.getPosY() + 1) * CELL_SIZE + (CELL_SIZE - HEADING_PIXEL_SIZE) / 2,
                    HEADING_PIXEL_SIZE, HEADING_PIXEL_SIZE);
        } else if (mRobot.getOrientation() == EAST) {
            g2d.fillOval((mRobot.getPosX() + 2) * CELL_SIZE + CELL_SIZE - HEADING_PIXEL_SIZE - PAINT_PIXEL_OFFSET,
                    (mRobot.getPosY() + 1) * CELL_SIZE + (CELL_SIZE - HEADING_PIXEL_SIZE) / 2,
                    HEADING_PIXEL_SIZE, HEADING_PIXEL_SIZE);
        }
    }


	@Override 
	public void propertyChange(PropertyChangeEvent evt) { 	//replace update
		// TODO Auto-generated method stub
		this.repaint();
	}
}
