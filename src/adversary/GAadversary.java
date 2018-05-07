package adversary;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.util.ArrayList;

import levelAI.LevelObject;
import levelDisplay.Geometry;
import math.SelfBalancingBinarySearchTree;
import math.Vec2;

public class GAadversary {
	public LevelObject level;
	ArrayList<MoveSet> players;
	int setSize;
	public GAadversary(LevelObject level, int setSize) {
		this.level = new LevelObject(level);
		players = new ArrayList<MoveSet>();
		this.setSize = setSize;
		for(int i = 0; i < setSize; i++) {
			players.add(new MoveSet(level.columns));
		}
	}
	public GAadversary(GAadversary advasary) {
		level = new LevelObject(advasary.level);
		setSize = advasary.setSize;
		players = new ArrayList<MoveSet>();
		for(MoveSet set : advasary.players) {
			players.add(new MoveSet(set));
		}
	}
	public ArrayList<MoveSet> getTopPlayers(int numberOfPlayers){
		SelfBalancingBinarySearchTree tree = new SelfBalancingBinarySearchTree();
		for(MoveSet set : players) {
			float evaluation = evaluateMoveSet(set);
			tree.insert(evaluation, set);
		}
		ArrayList<MoveSet> temp = new ArrayList<>();
		tree.inorder();
		int timesRan = 0;
		for(int i = tree.players.size()-1; timesRan < numberOfPlayers; i--) {
			timesRan++;
			temp.add(tree.players.get(i));
		}
		return temp;
	}
	public LevelObject runAdvasary(int times, int setIncreaseAmount) {
		for(int time = 0 ; time < times; time++) {
			int currSize = players.size();
			for(int i = 0; i < currSize; i++) {
				players.add(players.get(i).mutate(setIncreaseAmount));//TODO optimize this with the tree add below
			}
			SelfBalancingBinarySearchTree tree = new SelfBalancingBinarySearchTree();
			for(MoveSet set : players) {
				float evaluation = evaluateMoveSet(set);
				tree.insert(evaluation, set);
			}
			players = new ArrayList<>();
			tree.inorder();
			int timesRan = 0;
			for(int i = tree.players.size()-1; timesRan < setSize; i--) {
				timesRan++;
				players.add(tree.players.get(i));
			}
		}
		return level;
	}
	// should consider the distance from start,
	// distance from the end of the level
	// and the amount of moves.
	public float evaluateMoveSet(MoveSet set) {
		Vec2 startPos = level.getStartPos();
		Vec2 end = level.getEndPos();
		Vec2 currPos = new Vec2(level.getStartPos());
		int timesMoved = 0;
		for(Movement move : set.movements) {
			if(move == Movement.LEFT ) {
				if(allowedType(level.getBlockAt((int)currPos.x -1, (int)currPos.y).getType())) {
					currPos.x -= 1;
					level.getBlockAt((int)currPos.x, (int)currPos.y).addCounter();
				}else {
					level.getBlockAt((int)currPos.x-1, (int)currPos.y).addtimesCollidedWith();
					timesMoved--;
//					if(level.getBlockAt((int)currPos.x-1, (int)currPos.y).getType() == 4) {
//						break;
//					}
				}
			}else
			if(move == Movement.RIGHT) {
				if(allowedType(level.getBlockAt((int)currPos.x +1, (int)currPos.y).getType())) {
					currPos.x += 1;
					level.getBlockAt((int)currPos.x, (int)currPos.y).addCounter();
				}else {
					level.getBlockAt((int)currPos.x+1, (int)currPos.y).addtimesCollidedWith();
					timesMoved--;
//					if(level.getBlockAt((int)currPos.x+1, (int)currPos.y ).getType() == 4) {
//						break;
//					}
				}
			}else
			if(move == Movement.UP) {
				if(allowedType(level.getBlockAt((int)currPos.x , (int)currPos.y+1).getType())) {
					currPos.y += 1;
					level.getBlockAt((int)currPos.x, (int)currPos.y).addCounter();
				}else {
					level.getBlockAt((int)currPos.x, (int)currPos.y+1).addtimesCollidedWith();
					timesMoved--;
//					if(level.getBlockAt((int)currPos.x, (int)currPos.y +1).getType() == 4) {
//						break;
//					}
				}
			}else
			if(move == Movement.DOWN) {
				if( allowedType(level.getBlockAt((int)currPos.x, (int)currPos.y -1).getType()) ) {
					currPos.y -= 1;
					level.getBlockAt((int)currPos.x, (int)currPos.y).addCounter();
				}else {
					level.getBlockAt((int)currPos.x, (int)currPos.y-1).addtimesCollidedWith();
					timesMoved--;
//					if(level.getBlockAt((int)currPos.x, (int)currPos.y -1).getType() == 4) {
//						break;
//					}
				}
			}
			timesMoved++;
		}
		double heat = level.getBlockAt((int)currPos.x, (int)currPos.y).getTimesSteppedOn();
		double endDistance = Math.sqrt(Math.pow(currPos.x - end.x, 2) + Math.pow(currPos.y - end.y, 2) );
		double startDistance = Math.sqrt(Math.pow(currPos.x - startPos.x, 2) + Math.pow(currPos.y - startPos.y, 2) );
//		System.out.println("heat: " + heat + " endDistance: " + endDistance + " startDistance: "+ startDistance + " set Length: " + set.movements.length);
		if(endDistance == 0) {
			set.isCleared = true;
//			System.out.println("set length" + timesMoved);
			return 1.0f + (1.0f / (float) timesMoved);
			// the 1 + is because the other evaluation function cannot get above 1
		}
//		if((float) (1/ (endDistance + (startDistance * 0.2f) + (0.02f * heat)) ) > 1) {
//			System.out.println("greater than 1 = " + ((float) (1/ (endDistance + (startDistance * 0.2f) + (0.02f * heat)) )));
//		}
		set.isCleared = false;
		return (float) (1/ (endDistance + (startDistance * 0.2f) + (0.02f * heat)) );
	}
	public boolean allowedType(int type) {
		if(type == 0 || type == 2 || type == 3) {
			return true;
		}
		return false;
	}
	Geometry bestRun;
	public void init() {
		bestRun = new Geometry();
		float verticalSize = 2.0f / level.rows;
		float horizontalSize = 2.0f / level.columns;
		ArrayList<Float> points = new ArrayList<>();
		ArrayList<Float> colors = new ArrayList<>();
		Vec2 currPos = new Vec2(level.getStartPos());
		SelfBalancingBinarySearchTree tree = new SelfBalancingBinarySearchTree();
		for(MoveSet set : players) {
			float evaluation = evaluateMoveSet(set);
			tree.insert(evaluation, set);
		}
		players = new ArrayList<>();
		tree.inorder();
		MoveSet set = tree.players.get(tree.players.size()-1);
		for(Movement move : set.movements) {
			if(move == Movement.LEFT ) {
				if(allowedType(level.getBlockAt((int)currPos.x -1, (int)currPos.y).getType())) {
					currPos.x -= 1;
				}
			}else
			if(move == Movement.RIGHT) {
				if(allowedType(level.getBlockAt((int)currPos.x +1, (int)currPos.y).getType())) {
					currPos.x += 1;
				}
			}else
			if(move == Movement.UP) {
				if(allowedType(level.getBlockAt((int)currPos.x , (int)currPos.y+1).getType())) {
					currPos.y += 1;
				}
			}else
			if(move == Movement.DOWN) {
				if( allowedType(level.getBlockAt((int)currPos.x, (int)currPos.y -1).getType()) ) {
					currPos.y -= 1;
				}
			}
			points.add(1 - (currPos.y*horizontalSize));points.add(1 - (currPos.x*verticalSize));
			points.add(1 - (currPos.y*horizontalSize));points.add(1 - (currPos.x*verticalSize) - verticalSize);
			points.add(1 - (currPos.y*horizontalSize) - horizontalSize);points.add(1 - (currPos.x*verticalSize) - verticalSize);

			points.add(1 - (currPos.y*horizontalSize) - horizontalSize);points.add(1 - (currPos.x*verticalSize) - verticalSize);
			points.add(1 - (currPos.y*horizontalSize) - horizontalSize);points.add(1 - (currPos.x*verticalSize));
			points.add(1 - (currPos.y*horizontalSize));points.add(1 - (currPos.x*verticalSize));
			
			colors.add(1.0f);colors.add(0.0f);colors.add(1.0f);	
			colors.add(1.0f);colors.add(0.0f);colors.add(1.0f);	
			colors.add(1.0f);colors.add(0.0f);colors.add(1.0f);	
			
			colors.add(1.0f);colors.add(0.0f);colors.add(1.0f);	
			colors.add(1.0f);colors.add(0.0f);colors.add(1.0f);	
			colors.add(1.0f);colors.add(0.0f);colors.add(1.0f);	

		}
		vertices = new float[points.size()];
		colorsArray = new float[colors.size()];
		for(int i = 0; i < points.size();i++) {
			vertices[i] = points.get(i);
		}
		for(int i = 0; i < colors.size();i++) {
			colorsArray[i] = colors.get(i);
		}
	}
	public float[] vertices;
	public float[] colorsArray;
	public void render() {
		glBindVertexArray(bestRun.vertexArray);

		glDrawArrays(GL_TRIANGLES, 0, bestRun.elementCount);		
	}
}
