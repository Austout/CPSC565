package levelAI;

import java.util.ArrayList;
import java.util.Stack;

import adversary.GAadversary;
import fileReader.Block;
import math.Vec2;

public class LevelGen {
	public final static int mutationSetSize = 1000;
	public static LevelObject mutateLevel(LevelObject myLevel, GAadversary advasary) {
		ArrayList<LevelObject> possibleLevels = new ArrayList<LevelObject>();
		ArrayList<Float> levelEvaluations = new ArrayList<Float>();
		for(int i = 0; i < myLevel.columns; i++) {
			for(int j = 0; j < myLevel.rows; j++) {
				myLevel.getBlockAt(j, i).restartTimesCollidedWith();
			}
		}
		float bestMutation = evaluateLevel(myLevel,advasary);
		float temp = bestMutation;
		LevelObject best = new LevelObject(myLevel);
		for(int i =0 ;i < mutationSetSize; i++) {
			possibleLevels.add(randomMutation(myLevel));
			levelEvaluations.add(evaluateLevel(possibleLevels.get(i),new GAadversary(advasary)));
			if(levelEvaluations.get(i) >= bestMutation) {
				best = new LevelObject(possibleLevels.get(i));
				bestMutation = levelEvaluations.get(i);
			}
		}
//		System.out.println("size: " + advasary.getTopPlayers(1).get(0).movements.length);
		System.out.println("best: " + bestMutation + " first :" +temp);
		return best;
	}
	private static Float evaluateLevel(LevelObject levelObject, GAadversary advasary) {
		advasary.level = levelObject;
		levelObject = advasary.runAdvasary(2, 2);
		int counter = 0;
		for(int i = 0; i < levelObject.columns; i++) {
			for(int j = 0; j < levelObject.rows; j++) {
				if(levelObject.getBlockAt(j, i).getType() == 4) {
					counter += levelObject.getBlockAt(j, i).getTimesCollidedWith();
				}
			}
		}
		if(!isSolveable(levelObject)) {
			return -1.0f;
		}
		return (float) counter;
	}
	private static boolean isSolveable(LevelObject levelObject) {
		Vec2 startPos = null;
		Vec2 endPos = null;
		for(int i = 0; i < levelObject.columns; i++) {
			for(int j = 0; j < levelObject.rows; j++) {
				if(levelObject.getBlockAt(j, i).getType() == 2)
					startPos = new Vec2(j,i);
				if(levelObject.getBlockAt(j, i).getType() == 3)
					endPos = new Vec2(j,i);
			}
		}
		startQueue = new Stack<Vec2>();
		endQueue = new Stack<Vec2>();
		boolean result = addBlockToStack(levelObject,(int)startPos.x,(int)startPos.y-1,1,true);
		result |= addBlockToStack(levelObject,(int)startPos.x,(int)startPos.y+1,1,true);
		result |= addBlockToStack(levelObject,(int)startPos.x+1,(int)startPos.y,1,true);
		result |= addBlockToStack(levelObject,(int)startPos.x-1,(int)startPos.y,1,true);

		result |= addBlockToStack(levelObject,(int)endPos.x,(int)endPos.y-1,2,false);
		result |= addBlockToStack(levelObject,(int)endPos.x,(int)endPos.y+1,2,false);
		result |= addBlockToStack(levelObject,(int)endPos.x+1,(int)endPos.y,2,false);
		result |= addBlockToStack(levelObject,(int)endPos.x-1,(int)endPos.y,2,false);
//		startQueue.push(new Vec2(1,1));
//		System.out.println(levelObject.getBlockAt((int)startPos.x,(int)startPos.y).getType());
		while(!startQueue.isEmpty() && !endQueue.isEmpty() && !result) {
			Vec2 startQueueVec = startQueue.pop();
			Vec2 endQueueVec = endQueue.pop();
			
			result |= addBlockToStack(levelObject,(int)startQueueVec.x,(int)startQueueVec.y-1,1,true);
			result |= addBlockToStack(levelObject,(int)startQueueVec.x,(int)startQueueVec.y+1,1,true);
			result |= addBlockToStack(levelObject,(int)startQueueVec.x+1,(int)startQueueVec.y,1,true);
			result |= addBlockToStack(levelObject,(int)startQueueVec.x-1,(int)startQueueVec.y,1,true);

			result |= addBlockToStack(levelObject,(int)endQueueVec.x,(int)endQueueVec.y-1,2,false);
			result |= addBlockToStack(levelObject,(int)endQueueVec.x,(int)endQueueVec.y+1,2,false);
			result |= addBlockToStack(levelObject,(int)endQueueVec.x+1,(int)endQueueVec.y,2,false);
			result |= addBlockToStack(levelObject,(int)endQueueVec.x-1,(int)endQueueVec.y,2,false);
//			System.out.println(levelObject.getBlockAt((int)endQueueVec.x-1,(int)endQueueVec.y).getProcessedID());
		}
		return result;
	}
	static Stack<Vec2> startQueue = new Stack<Vec2>();
	static Stack<Vec2> endQueue = new Stack<Vec2>();
	private static boolean addBlockToStack(LevelObject levelObject,int x, int f,int type,boolean useStartQueue) {
		Block blockInQuestion = levelObject.getBlockAt(x, f);
		if(blockInQuestion != null && blockInQuestion.getType() == 0 && blockInQuestion.getProcessedID() != type) {
//			System.out.println(x +" : " +f);
			if(useStartQueue) {
				startQueue.push(new Vec2(x,f));
			}else {
				endQueue.push(new Vec2(x,f));
			}
			if(blockInQuestion.getProcessedID() != 0) {
//				System.out.println(blockInQuestion.getProcessedID() + " type : " + type);
				return true;
			}else {
				blockInQuestion.setProcessedID(type);
			}
		}
//		if(blockInQuestion == null ) {
//			System.out.println(x +" : " +f);
//		}
		return false;
	}
	private static LevelObject randomMutation(LevelObject myLevel) {
		LevelObject temp = new LevelObject(myLevel);
		int column = (int) (temp.columns * Math.random());
		int row = (int) (temp.rows * Math.random());
		while(temp.getBlockAt(row, column).getType() != 0) {
			column = (int) (temp.columns * Math.random());
			row = (int) (temp.rows * Math.random());
		}
		for(int i = 0; i < temp.columns; i++) {
			for(int j = 0; j < temp.rows; j++) {
				temp.getBlockAt(j, i).restartTimesCollidedWith();
			}
		}
		temp.setBlockAt(row,column,4);
		return temp;
	}

}
