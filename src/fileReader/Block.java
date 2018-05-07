package fileReader;

public class Block {
	private int type;
	private int processedID = 0;
	private int timeSteppedOn = 0;
	private int timesCollidedWith = 0;
	public Block(int type) {
		this.type = type;
	}
	public Block(Block block) {
		this.type = block.type;
	}
	public int getType() {
		return type;
	}
	public void addCounter() {
		if(type != 3) {
			timeSteppedOn++;
		}
	}
	public void addtimesCollidedWith() {
		timesCollidedWith++;
	}
	public void restartTimesCollidedWith() {
		timesCollidedWith = 0;
	}
	public int getProcessedID() {
		return processedID;
	}
	public void setProcessedID(int processedID) {
		this.processedID = processedID;
	}
	public int getTimesCollidedWith() {
		return timesCollidedWith;
	}
	public int getTimesSteppedOn() {
		return timeSteppedOn;
	}
}
