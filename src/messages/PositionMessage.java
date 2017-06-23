package messages;

import java.io.Serializable;

public class PositionMessage extends Message implements Serializable{
	
	private static final long serialVersionUID = 6887926961459159988L;
	private static final int POSITION_PRIORITY = 2;
	private int row;
	private int col;

	public PositionMessage(int row, int col) {
		super(Type.POSITION, POSITION_PRIORITY);
		this.setRow(row);
		this.setCol(col);
		
	}

	@Override
	public void handleMessage() {
		System.out.println(this.toString());
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}
	
	@Override
	public String toString(){
		return "This is a position message";
	}

}
