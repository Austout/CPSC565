package adversary;

public class MoveSet {
	public Movement[] movements;
	public boolean isCleared = false;

	public MoveSet() {
		movements = new Movement[0];
	}

	public MoveSet(Movement[] movements) {
		this.movements = movements;
	}

	public MoveSet(int i) {
		movements = new Movement[0];
		MoveSet temp = addSet(i);
		this.movements = temp.movements;
	}

	public MoveSet(MoveSet set) {
		movements = new Movement[set.movements.length];
		for(int i = 0 ; i < set.movements.length;i++) {
			movements[i] = set.movements[i];
		}
	}

	private MoveSet addSet(int setIncreaseAmount) {
		// TODO Auto-generated method stub
		// TODO upon muation keep track of the current placement in x and y coords
		// to save on processing time
		Movement[] temp = new Movement[movements.length + setIncreaseAmount];
		int i;
		for (i = 0; i < movements.length; i++) {
			temp[i] = movements[i];
		}
		for(int j = 0; j < setIncreaseAmount;j++) {
			temp[i + j] = randomMovement();
		}

		return new MoveSet(temp);
	}
	private static final float AddProb = 0.2f;
	private static final float MutationProb = 0.05f;
	public MoveSet ChangeSet(float mutationProbibility) {
		Movement[] temp = new Movement[movements.length];
		int i;
		for (i = 0; i < movements.length; i++) {
			if(Math.random() < mutationProbibility) 
				temp[i] = randomMovement();
			else
				temp[i] = movements[i];
		}
		return new MoveSet(temp);
	}
	public MoveSet mutate(int setIncreaseAmount) {
		if(Math.random() < AddProb && !isCleared) {
			return addSet(setIncreaseAmount);
		}
		return ChangeSet(MutationProb);
	}

	private Movement randomMovement() {
		int random = (int) Math.floor(Math.random() * 4);
		switch (random) {
		case 0:
			return Movement.LEFT;
		case 1:
			return Movement.RIGHT;
		case 2:
			return Movement.UP;
		case 3:
			return Movement.DOWN;
		}
		return null;
	}

}
