package Model;

import java.util.ArrayList;
import ViewController.Act;

/**
 * This class talks with the controller
 */

public class DeadwoodModel {

	/* Class attributes */
	private static DeadwoodModel instance = null;

	/* Object attributes */
	private PlayerManager pm;
	private Board board;
	private Database db;
	private boolean gameOver = false;

	/**
	 * Private constructor.
	 */
	private DeadwoodModel() {
		pm = PlayerManager.getInstance();
		board = Board.getInstance();
		db = Database.getInstance();
	}

	/**
	 * Instantiate model.
	 * @return Model instance.
	 */
	public static DeadwoodModel getInstance() {
		if (instance == null)
			instance = new DeadwoodModel();
		return instance;
	}

	public boolean isGameOver() {
		return gameOver;
	}

	void setGameOver() {
		this.gameOver = true;
	}

	/**
	 * Prepares the model for the game.
	 * @param playerCount number of players in the game
	 */
	public void setUpModel(int playerCount) {
		pm.setUpPM(playerCount);

		int dayCount = 4;
		if (playerCount < 4)
			dayCount = 3;

		board.setUpBoard(dayCount);
	}

	/**
	 * Produces the list of areas where the current player can move.
	 * @return The list of areas.
	 */
	public ArrayList<String> wantToMove() {
		return pm.getCurPlayer().getPosition().getNeighbors();
	}

	/**
	 * Perform action "Move" for the current player.
	 * @param area The area to be moved to.
	 */
	public void doMove(String area) {
		if (!pm.getCurPlayer().isWorking()) {
			pm.getCurPlayer().move(db.getArea(area));
		}
		else
		    throw new RuntimeException("Cannot move while working.");
	}

	/**
	 * Construct a list of available roles for the current player
	 * @return List of roles.
	 */
	public ArrayList<Role> wantARole() {
		Player curPlayer = pm.getCurPlayer();
		ArrayList<Role> availableRoles = new ArrayList<>();
		ArrayList<Role> extrRoles = curPlayer.getPosition().getRoles();
		ArrayList<Role> starRoles = curPlayer.getPosition().getScene().getRoles();

		for (Role r: extrRoles) {
			if (r.availableTo(curPlayer))
				availableRoles.add(r);
		}

		for (Role r: starRoles) {
			if (r.availableTo(curPlayer))
				availableRoles.add(r);
		}

		return availableRoles;
	}

	/**
	 * Perform action "Take Role" for the current player.
	 * @param role The role to be taken.
	 */
	public void doTakeRole(Role role) {
		if (!pm.getCurPlayer().isWorking()) {
			pm.getCurPlayer().takeRole(role);
		} else
			throw new RuntimeException("Cannot take role while working on a role.");
	}

	/**
	 * Perform action "Rehearse" for the current player.
	 */
	public void doRehearse() {
		if (pm.getCurPlayer().isWorking()) {
			pm.getCurPlayer().rehearse();
		} else
			throw new RuntimeException("Player cannot rehearse if they don't have a role.");
	}

	/**
	 * Perform action "Act" for the current player.
	 */
	public void doAct() {
		if (pm.getCurPlayer().isWorking()) {
			pm.getCurPlayer().act();
		} else
			throw new RuntimeException("Player cannot act if they don't have a role.");
	}

	/**
	 * Constructs a list of available upgrades for the current player.
	 * @return list of upgrades
	 */
	public ArrayList<String> wantToUpgrade() {
		ArrayList<String> stringUpgrades = new ArrayList<>();

		if (pm.getCurPlayer().getPosition().getName().equals("office")) {
			UpgradeOffice office = (UpgradeOffice) db.getArea("office");
			ArrayList<Upgrade> upgrades = office.getAvailableUpgrades(pm.getCurPlayer());

			for (Upgrade u: upgrades) {
				stringUpgrades.add("" + u.getRank() + u.getCurrency() + u.getPrice());
			}
		} else
			throw new RuntimeException("Cannot upgrade unless in office");

		return stringUpgrades;
	}

	/**
	 * Upgrades the current player using the given upgrade String value
	 * @param upgradeStr String value of an upgrade
	 */
	public void doUpgrade(String upgradeStr) {
		if (pm.getCurPlayer().getPosition().getName().equals("office")) {
			Upgrade u = Upgrade.getFromString(upgradeStr);
			pm.getCurPlayer().upgrade(u);
		} else
			throw new RuntimeException("Cannot upgrade unless in office");
	}

	/**
	 * Ends current player's turn
	 */
	public void doEndTurn() {
		pm.nextTurn();
	}

	/**
	 * Returns the image name of the current player
	 * @return current player's image name
	 */
	public String getPlayerImage()
    {
        return pm.getCurPlayer().getImage();
    }

	/**
	 * Constructs a list of messages that can be shown to the player.
	 * @return list of messages
	 */
	public ArrayList<String> showPlayerInfo() {
		Player curPlayer = pm.getCurPlayer();
		ArrayList<String> playerInfo = new ArrayList<>();

		playerInfo.add("Day " + board.getCurDay() + " out of " + board.getDayCount());
		playerInfo.add("Player " + curPlayer.getID());
		playerInfo.add("Rank: " + curPlayer.getRank());
		playerInfo.add("Dollars: " + curPlayer.getDollars());
		playerInfo.add("Credits: " + curPlayer.getCredits());

		if (curPlayer.isWorking()) {
			playerInfo.add("Current role: " + curPlayer.getRole().getName());
			playerInfo.add("Role type: " + curPlayer.getRole().getSType());
			if(curPlayer.getPosition().sceneActive())
			{
				playerInfo.add("Rehearsal points: " + curPlayer.getPC() + " out of " + curPlayer.getPosition().getScene().getBudget() + " possible");
			}
		}

		return playerInfo;
	}

	/**
	 * Constructs a list of UI placements for all wrap markers on the board
	 * @return list of wrap UI placements
	 */
	public ArrayList<UIPlacement> getTakePlacements()
	{
		ArrayList<UIPlacement> positions = new ArrayList<>();

		for(Area a : db.getAreas())
		{
			if(a.getTakes() != null) {
				for (Take t : a.getTakes()) {

					if (t.isDone())
						positions.add(t.getPlacement());
				}
			}
		}

		return positions;
	}

	/**
	 * Constructs a list of UI placements for all player icons on the board
	 * @return list of player icon UI placements
	 */
	public ArrayList<UIPlacement> getPlayerPlacements()
	{
		ArrayList<UIPlacement> positions = new ArrayList<>();

		for(Player p : pm.getAllPlayers())
		{
			positions.add(p.getPlacement());
		}

		return positions;
	}

	/**
	 * Constructs a list of UI placements for all cards on the board
	 * @return list of card UI placements
	 */
	public ArrayList<UIPlacement> getCardPlacement()
	{
		ArrayList<UIPlacement> positions = new ArrayList<>();

		for(Area a : db.getAreas())
		{
			if(a.getCardPlacement() != null && a.sceneActive())
			{
				UIPlacement position = a.getCardPlacement().clone();

				position.setImagePath(a.getScene().getImage());

				positions.add(position);
			}
		}
		return positions;
	}

	/**
	 * Constructs a list of available actions for the current player
	 * @return list of actions the current player can perform
	 */
	public ArrayList<Act> availableActions() {
		ArrayList<Act> actionArray = new ArrayList<>();
		Player curPlayer = pm.getCurPlayer();

		actionArray.add(Act.END_TURN);

		if (curPlayer.isWorking()) {

			int budget = curPlayer.getPosition().getScene().getBudget();
			int PC = curPlayer.getPC();
			if (PC < budget)
				actionArray.add(Act.REHEARSE);

			actionArray.add(Act.ACT);
		} else {
			if(!curPlayer.getJustMoved())
				actionArray.add(Act.MOVE);

			if (curPlayer.getPosition().sceneActive() && wantARole().size() != 0)
				actionArray.add(Act.TAKE_ROLE);
			if (curPlayer.getPosition().getName().equals("office") && wantToUpgrade().size() != 0)
				actionArray.add(Act.UPGRADE);
		}

		return actionArray;
	}


    /**
     * Accesses the end game messages in a form of a list of strings.
     * @return End game message list.
     */
	public ArrayList<String> endGame()
	{
        ArrayList<String> messages = new ArrayList<>();
		PlayerManager pm = PlayerManager.getInstance();
		ArrayList<Player> winners = pm.getLeaders();

		if(winners.size() > 1)
		{
            messages.add("The winners are:");
			for(Player p: winners)
                messages.add("Player " + p.getID() + ", final score : " + p.getFinalScore());
		}
		else if(winners.size() > 0)
		{
			Player winner = winners.get(0);
            messages.add("The winner is:");
            messages.add("Player " + winner.getID() + ", final score : " + winner.getFinalScore());
		}
        messages.add("Congratulations!");

		return messages;
	}
}
