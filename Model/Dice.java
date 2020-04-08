package Model;

import java.util.*;

/**
 * Dice represents dice.
 */
class Dice {

    /* Class attributes */
	private static Dice instance = null;

	/* Object attributes */
    private Random rand;

    /* Constructors */
    private Dice(){
		rand = new Random();
	}

    /**
     * Grab an instance of Dice.
     * @return Dice instance.
     */
	static Dice getInstance()
	{
		if(instance == null)
			instance = new Dice();
		return instance;
	}

    /**
     * Roll a single six-sided die.
     * @return Resulting dice roll.
     */
	int rollSix()
	{
		return rollDice(1)[0];
	}

    /**
     * Roll a given number of dice with a given amount of sides.
     * @param diceCount Number of dice to be rolled.
     * @return List of dice roll results.
     */
	Integer[] rollDice(int diceCount) {
		Integer[] diceArr = new Integer[diceCount];

		for(int i = 0; i<diceCount; i++)
		{
			diceArr[i] = 1 + rand.nextInt(6);
		}

		Arrays.sort(diceArr, Collections.reverseOrder());

		return diceArr;
	}
}
