package ViewController;

import Model.*;

import javax.swing.*;
import java.util.*;

/**
 * Controller class.
 */

public class DeadwoodController {
	private DeadwoodModel model = DeadwoodModel.getInstance();
	private GUIDeadwoodView view = GUIDeadwoodView.getInstance();

	private static DeadwoodController instance = null;

	private volatile Act actionChoice;
	private volatile int optionChoice;


	private DeadwoodController() {
	}

	public static DeadwoodController getInstance() {

		if (instance == null)
			instance = new DeadwoodController();

		return instance;
	}

	/**
	 * Runs the game loop
	 */

	public void playGame() {

		// set board to visible
		setVisible();
		model.setUpModel(view.getPlayerCount());
		while (true) {
			displayPlayerIcons();
			displayPlayerInfo();
			displayCards();
			displayWraps();
			
			Act actionChosen = offerActions(model.availableActions());
			performAction(actionChosen);

			removePlayerInfo();

			if(model.isGameOver())
			{
				ArrayList<String> endGameMessage = model.endGame();
				view.showEndScreen(endGameMessage);
				System.exit(1);
			}
		}
	}

	private void performAction(Act actionChosen) {
		switch (actionChosen) {
		case MOVE:
			moveActions();
			break;
		case ACT:
			model.doAct();
			break;
		case REHEARSE:
			model.doRehearse();
			break;
		case UPGRADE:
			upgradePlayer();
			break;
		case TAKE_ROLE:
			showRoles();
			break;
		case END_TURN:
			model.doEndTurn();
			break;
		default:
			throw new RuntimeException("Error: Invalid Action.");
		}
	}

	private void moveActions() {
		ArrayList<String> options = model.wantToMove();
		
		int choice = presentChoice(options);
		if (choice == Integer.MAX_VALUE)
			return;

		model.doMove(options.get(choice));
	}
	
	private void displayPlayerInfo() {

        ArrayList<String> playerInfo = model.showPlayerInfo();
		String playerImage = model.getPlayerImage();
		view.showPlayerInfo(playerInfo, playerImage);
	}

	private void displayWraps() {
		ArrayList<UIPlacement> wrapPlacements = model.getTakePlacements();

		view.removeWrapIcons();
		view.drawWrapIcons(wrapPlacements);
	}

	private void displayPlayerIcons()
	{
		ArrayList<UIPlacement> playerPlacements = model.getPlayerPlacements();

		view.removePlayerIcons();
		view.drawPlayerIcons(playerPlacements);
	}

	private void displayCards()
	{
		ArrayList<UIPlacement> cardPlacements = model.getCardPlacement();

		view.removeCards();
		view.drawCards(cardPlacements);
	}
	
	private void removePlayerInfo() {
		view.deletePlayerInfo();
	}

	
	private void showRoles() {
		ArrayList<Role> roles = model.wantARole();
		ArrayList<String> options = new ArrayList<>();

		for (Role r : roles) {
			options.add("Rank " + r.getRank() + ". " + r.getName() + ", " + r.getSType());
		}

		int choice = presentChoice(options);

		if (choice == Integer.MAX_VALUE)
			return;

		model.doTakeRole(roles.get(choice));
	}

	private int presentChoice(ArrayList<String> options) {
		view.showChoiceMenu(options);

		optionChoice = view.notifyChoice();

		while (optionChoice == -1) {
			optionChoice = view.notifyChoice();
		}
		view.resetOptionChoice();

		return optionChoice;
	}

	private void upgradePlayer() {
		ArrayList<String> stringUpgrades = model.wantToUpgrade();
		ArrayList<String> options = new ArrayList<>();

		for (String r : stringUpgrades) {

			char rank = r.charAt(0);
			String cur;
			if (r.charAt(1) == 'c')
				cur = "credits";
			else
				cur = "dollars";
			String price = r.substring(2);

			options.add("Rank " + rank + " for " + price + " " + cur);
		}

		int choice = presentChoice(options);

		if (choice == Integer.MAX_VALUE)
			return;

		model.doUpgrade(stringUpgrades.get(choice));
	}


	private void setVisible() {
		view.setVisible(true);
		view.setResizable(true);
	}



	private Act offerActions(ArrayList<Act> availableActions) {
		view.showActionMenu(availableActions);

		actionChoice = view.notifyAction();
		//loop
		while (actionChoice == Act.BAD_ACTION) {
			actionChoice = view.notifyAction();
		}
		view.resetActionChoice();
		view.removeButtons();

		return actionChoice;
	}

}
