package messages;

public class PositionMessage extends Message {
	
	private static final long serialVersionUID = 6887926961459159988L;
	private static final int POSITION_PRIORITY = 3;
	private int row;
	private int col;

	public PositionMessage(int row, int col) {
		super(Type.POSITION, POSITION_PRIORITY);
		this.setRow(row);
		this.setCol(col);
		
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
	public void handleMessage() {
		System.out.println(this.toString());
	}
	
	@Override
	public String toString(){
		return "This is a position message";
	}

}
