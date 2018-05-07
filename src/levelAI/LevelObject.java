package levelAI;

import java.util.ArrayList;

import fileReader.Block;
import levelDisplay.Geometry;
import math.Vec2;

public class LevelObject {
	ArrayList<ArrayList<Block>> levelBlocks;
	public final int rows;
	public final int columns;
	public Geometry map;
	private Vec2 startPos,endPos;
	public LevelObject(ArrayList<ArrayList<Block>> levelBlocks) {
		rows = levelBlocks.size();
		columns = levelBlocks.get(0).size();
		this.levelBlocks = levelBlocks;
		for(int x = 0; x < levelBlocks.size(); x++) {
			for(int y = 0; y < levelBlocks.get(x).size();y++) {
//				System.out.print(levelBlocks.get(x).get(y).getType() + ",");
				if(levelBlocks.get(x).get(y).getType() == 2) {
					startPos = new Vec2(x,y);
				}else if(levelBlocks.get(x).get(y).getType() == 3) {
					endPos = new Vec2(x,y);
				}
			}
		}
		map = new Geometry();
	}
	public LevelObject(LevelObject level) {
		startPos = new Vec2(level.startPos);
		endPos = new Vec2(level.endPos);
		map = level.map;
		rows = level.rows;
		columns = level.columns;
		levelBlocks = new ArrayList<>();
		for(ArrayList<Block> blockList : level.levelBlocks) {
			ArrayList<Block> tempBlockList = new ArrayList<>();
			for(Block block : blockList) {
				tempBlockList.add(new Block(block));
			}
			levelBlocks.add(tempBlockList);
		}
	}
	public Block getBlockAt(int i, int j) {
		try {
			return levelBlocks.get(i).get(j);
		}catch(Exception e) {
			return null;
		}
	}
	public int getBlockTypeAt(int i, int j) {
		try {
			return levelBlocks.get(i).get(j).getType();
		}catch(Exception e) {
			return -1;
		}
	}
	public Vec2 getStartPos() {
		return startPos;
	}
	public Vec2 getEndPos() {
		return endPos;
	}
	public void setBlockAt(int column, int row, int type) {
		levelBlocks.get(column).set(row, new Block(type));		
	}
}
