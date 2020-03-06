package entities;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class GridBox implements Comparable <GridBox> {
	
	private int x;
    private int y;
    private int dist;
	private boolean obstacle = false;
	private boolean visited = false;
	private boolean changed = false;
	private boolean last = false;
	private int prob = 0;
	private PropertyChangeSupport support;

	
	//constructor
	public GridBox(int x_cord, int y_cord){
		x= x_cord;
		y=y_cord;
        support = new PropertyChangeSupport(this);
	}
		
	//getters and setters	
	public boolean getIsObstacle() {
		return obstacle;
	}

	public void setObstacle(boolean obstacle) {
		support.firePropertyChange("obstacle",this.obstacle,obstacle);
		this.obstacle = obstacle;
	}

	public boolean getIsVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		support.firePropertyChange("visited",this.visited,visited);
		this.visited = visited;
	}

	public boolean getIsChanged() {return changed;}

	public int getX() {
	        return x;
	}

	public int getY() {
		return y;
	}

	public void setDistance(int n_distance) {
		dist = n_distance;
	}

	private int getDistance() {
		return dist;
	}
	

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }
 
    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }
    

	//plotting
	public void printGrid(){
		
		if(obstacle == false) {
			System.out.print(" ");
		}
		else {
			System.out.print("X");
		}
		
	}
	
	

	  
    public boolean equals(Object obj) {
        if (obj instanceof GridBox) {
            GridBox otherCell = (GridBox)obj;
            if (otherCell.getX() == getX() && otherCell.getY() == getY())
                return true;
        }
        return false;
    }
	
    @Override
    public int compareTo(GridBox o) {
        if (dist < o.getDistance())
            return -1;
        else if (dist > o.getDistance())
            return 1;
        else
            return 0;
    }

    
    void updateCounter(int value) {
        prob += value;
        obstacle = prob > 0;
		if (last^obstacle){
			this.changed = true;
		} else{
			this.changed = false;
		}
		System.out.println(prob+"\t"+obstacle+"\t"+changed);
		last = obstacle;
	}
	

}
