package Model;

import java.util.*;

class PlayerManager {

	/* Constants */
	private static final int RANK_SCORE_MULTIPLIER = 5;

	/* Class attributes */
	private static PlayerManager instance = null;

	/* Object attributes */
	private ArrayList<Player> players;
	private int curTurn;

	/* Constructors */
	private PlayerManager() {
		players = new ArrayList<>();
	}

	/**
	 * Instantiate player manager.
	 * @return PM instance.
	 */
	static PlayerManager getInstance() {
		if (instance == null)
			instance = new PlayerManager();

		return instance;
	}

	/**
	 * Sets the current turn to the next player.
	 */
	void nextTurn() {
		players.get(curTurn).resetJustMoved();
		curTurn++;
		if (curTurn == players.size())
			curTurn = 0;
	}

	/**
	 * Sets up player manager according to the player count.
	 * @param playerCount Number of players in the game.
	 */
	void setUpPM(int playerCount) {
		int initDollar = 0;
		int initCredit = 0;
		int initRank = 1;

		if (playerCount == 5)
			initCredit = 2;
		else if (playerCount == 6)
			initCredit = 4;
		else if (playerCount > 6 && playerCount < 9) {
			initRank = 2;
		} else if (playerCount > 8 || playerCount < 2)
			throw new IllegalArgumentException("Illegal number of players: " + playerCount + ".");

		int initScore = initCredit + initDollar + initRank * RANK_SCORE_MULTIPLIER;

		for (int i = 0; i < playerCount; i++) {
			Database.getInstance().getArea("trailer");
			players.add(new Player(i + 1, initRank, initCredit, initDollar, Database.getInstance().getArea("trailer"),
					null, initScore));
			players.get(players.size()-1).setPlacement(UIPlacement.trailers.clone().setDimensions(20, 20));
		}

	}

	/**
	 * Resets all players' position.
	 */
	void resetPlayerPositions()
	{
		for(Player p : players)
		{
			if(!p.isWorking())
				p.resetPlacement(p.getPosition());
		}
	}

	/**
	 * Accesses all players in the game.
	 * @return List of players.
	 */
	ArrayList<Player> getAllPlayers() {
		return players;
	}

	/**
	 * Accesses the current player.
	 * @return Current player.
	 */
	Player getCurPlayer() {
		return players.get(curTurn);
	}

	/**
	 * Accesses all players with a certain role type at a given area.
	 * @param areaName Area name.
	 * @param roleType Role type of the players.
	 * @return List of players.
	 */
	ArrayList<Player> getPlayersAt(String areaName, int roleType) {
		ArrayList<Player> pList = new ArrayList<>();

		for (Player p : players) {
			if (p.isWorking()) {
				int pRoleType = p.getRole().getIType();
				if (p.getPosition().getName().equals(areaName) && roleType == pRoleType)
					pList.add(p);
			}
		}

		return pList;
	}

	/**
	 * Accesses the list of players with the highest score.
	 * @return List of leaders.
	 */
	ArrayList<Player> getLeaders() {
		ArrayList<Player> leaders = new ArrayList<>();

		calcScores();

		int maxScore = -1;

		for (Player p : players) {
			if (p.getFinalScore() > maxScore)
				maxScore = p.getFinalScore();
		}

		for (Player p : players) {
			if (p.getFinalScore() == maxScore)
				leaders.add(p);
		}
		if (leaders.size() < 1)
			throw new RuntimeException("Error: no leaders found");

		return leaders;
	}

	/**
	 * Calculates the scores of each player and updates the players.
	 */
	private void calcScores() {
		for (Player p : players) {
			int rank = p.getRank();
			int dollars = p.getDollars();
			int credits = p.getCredits();
			p.setScore(rank * RANK_SCORE_MULTIPLIER + dollars + credits);
		}
	}

}