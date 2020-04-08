package ViewController;

import Model.UIPlacement;

import java.awt.image.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * View class.
 */

class GUIDeadwoodView extends JFrame {

	private static GUIDeadwoodView instance = null;

	// JLabels
	private ArrayList<JLabel> playerInfoLabels;
	private ArrayList<JLabel> playerIconLabels = new ArrayList<>();
	private ArrayList<JLabel> playerCardLabels = new ArrayList<>();
	private ArrayList<JLabel> wrapIconLabels = new ArrayList<>();


	// JButtons
	private ArrayList<JButton> buttons;

	// JLayered Pane
	private JLayeredPane bPane;
	private ImageIcon icon;
	private JLabel mLabel;

	private final int[] choice = { -1 };
	private final Act[] choiceAction = { Act.BAD_ACTION };

	// Constructor
	@SuppressWarnings("deprecation")
	private GUIDeadwoodView() {
		// Set the title of the JFrame
		super("Deadwood");
		// Set the exit option for the JFrame
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		// Create the JLayeredPane to hold the display, cards, dice and buttons
		bPane = getLayeredPane();

		// Create the deadwood board
		JLabel boardLabel = new JLabel();
		icon = new ImageIcon("board.jpg");
		boardLabel.setIcon(icon);
		boardLabel.setBounds(0, 0, icon.getIconWidth(), icon.getIconHeight());

		// Add the board to the lowest layer
		bPane.add(boardLabel, new Integer(0));

		// Set the size of the GUI
		setSize(icon.getIconWidth() + 360, icon.getIconHeight() + 39);
	}

    int getPlayerCount() {
        int playerCount = 0;
        do {
            String temp = JOptionPane.showInputDialog(this, "Choose a number of players! (2-8)");
            try {
                playerCount = Integer.parseInt(temp);
                if (!(2 <= playerCount && playerCount < 9)) {
                    JOptionPane.showMessageDialog(this, "Player count must be within 2-8!", "Error",
                            JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Please input a valid number of players!", "Error",
                        JOptionPane.WARNING_MESSAGE);
            }

        } while (playerCount < 2 || playerCount >= 9);

        return playerCount;
    }

    void showEndScreen(ArrayList<String> messages) {

        StringBuilder fullMessage = new StringBuilder();

        for(String m: messages)
        {
        	StringBuilder mSB = new StringBuilder(m);
			fullMessage.append(mSB.append("\n"));
        }

        JOptionPane.showMessageDialog(this, fullMessage, "GAME OVER",
                JOptionPane.PLAIN_MESSAGE);
    }

	void drawCards(ArrayList<UIPlacement> placements)
	{
		for(UIPlacement placement : placements)
		{
			if(!placement.getImagePath().equals(""))
			{

				JLabel l = new JLabel();
				ImageIcon pIcon = new ImageIcon("cards/" + placement.getImagePath());


				pIcon = getScaledIcon(pIcon, placement.getW(), placement.getH());

				l.setBounds(placement.getX(),
						placement.getY(),
						pIcon.getIconWidth(),
						pIcon.getIconHeight());

				playerCardLabels.add(l);
				l.setIcon(pIcon);
				l.setOpaque(true);

				// Add the card to the lower layer
				bPane.add(l, new Integer(1));
			}
		}
	}

	/**
	 * Draws wrap progress icons.
	 * @param placements Placements of successful takes needed to be drawn.
	 */
	void drawWrapIcons(ArrayList<UIPlacement> placements)
	{
		for(UIPlacement placement : placements)
		{
			if(!placement.getImagePath().equals(""))
			{

				JLabel l = new JLabel();
				ImageIcon wIcon = new ImageIcon(placement.getImagePath());


				wIcon = getScaledIcon(wIcon, placement.getW(), placement.getH());

				l.setBounds(placement.getX(),
						placement.getY(),
						wIcon.getIconWidth(),
						wIcon.getIconHeight());

				wrapIconLabels.add(l);
				l.setIcon(wIcon);
				l.setOpaque(true);

				// Add the card to the lower layer
				bPane.add(l, new Integer(2));
			}
		}
	}



	/**
	 * Draws all player icons in the game ui
	 * @param placements data required to draw all player icons
	 */
	void drawPlayerIcons(ArrayList<UIPlacement> placements)
	{
		playerIconLabels = new ArrayList<>();

		for(UIPlacement placement : placements)
		{
			JLabel l = new JLabel();

			ImageIcon pIcon = new ImageIcon("dice/" + placement.getImagePath());


			pIcon = getScaledIcon(pIcon, placement.getH(), placement.getW());


			/* Move the player icon down until there are no overlaps with other player icons */
			int x = placement.getX();
			int y = placement.getY();
			while(othersInThisSpot(x, y))
			{
				y += pIcon.getIconHeight();
			}

			l.setBounds(x, y, pIcon.getIconWidth(), pIcon.getIconHeight());

			//bPane.add(mLabel);
			playerIconLabels.add(l);
			l.setIcon(pIcon);
			l.setOpaque(true);

			// Add the card to the lower layer
			bPane.add(l, new Integer(2));
		}
	}

	/**
	 * Checks if there are other players in the given spot
	 * @param x coordinate to check
	 * @param y coordinate to check
	 * @return true if there is another player icon at the given coordinates
	 */
	private boolean othersInThisSpot(int x, int y)
	{
		for(JLabel playerIcon : playerIconLabels)
		{
			if(playerIcon.getY() == y && playerIcon.getX() == x)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Scales ImageIcon object to desired dimensions
	 * @param srcIcon original ImageIcon
	 * @param w desired width
	 * @param h desired height
	 * @return resized ImageIcon
	 */
	private ImageIcon getScaledIcon(ImageIcon srcIcon, int w, int h){
		Image srcImg = srcIcon.getImage();
		BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = resizedImg.createGraphics();

		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(srcImg, 0, 0, w, h, null);
		g2.dispose();

		return new ImageIcon(resizedImg);
	}

	/**
	 * Returns an instance of the GUI view
	 * @return instance
	 */
	static GUIDeadwoodView getInstance() {
		if (instance == null)
			instance = new GUIDeadwoodView();
		return instance;
	}

	/**
	 * Displays buttons with options to pick from.
	 * @param availableActions List of options.
	 */
	void showActionMenu(ArrayList<Act> availableActions) {

		if(mLabel != null)
			bPane.remove(mLabel);

		/* Construct action choice menu label */
		mLabel = new JLabel("MENU");
		mLabel.setBounds(icon.getIconWidth() + 20, 0, 150, 20);
		bPane.add(mLabel);
		int yVal = 30;

		/* Construct action buttons */
		buttons = new ArrayList<>();

		for (Act action : availableActions){

			JButton button = new JButton(action.getMessage());
			button.setBackground(Color.white);
			button.setBounds(icon.getIconWidth() + 10, yVal, 325, 40);

			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					choiceAction[0] = action;
				}
			});

			bPane.add(button);
			buttons.add(button);
			yVal += 50;
		}
	}

	/**
	 * Returns the action stored in action choice array.
	 * @return Action chosen by the user.
	 */
	Act notifyAction() {
		return choiceAction[0];
	}

	/**
	 * Resets the action choice to a default value.
	 */
	void resetActionChoice() {
		choiceAction[0] = Act.BAD_ACTION;
	}

	/**
	 * Returns the choice stored in choice array.
	 * @return Choice made by the user.
	 */
	int notifyChoice() {
		return choice[0];
	}

	/**
	 * Resets the choice to a default value.
	 */
	void resetOptionChoice() {
		choice[0] = -1;
	}

	/**
	 * Displays buttons with options to pick from.
	 * @param options List of options.
	 */
	void showChoiceMenu(ArrayList<String> options) {

		/* Construct choices label */
		choice[0] = -1;
		mLabel = new JLabel("CHOICES");
		mLabel.setBounds(icon.getIconWidth() + 20, 0, 120, 20);
		bPane.add(mLabel);
		int yVal = 30;

		/* Construct buttons for each option */
		buttons = new ArrayList<>();

		for (int i = 0; i < options.size(); i++) {

			String option = options.get(i);

			if(option.equals("trailer"))
				option = "Trailers";
			else if(option.equals("office"))
				option = "Casting Office";

			JButton button = new JButton(option);
			button.setBackground(Color.white);
			button.setBounds(icon.getIconWidth() + 10, yVal, 325, 40);

			final int index = i;

			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					choice[0] = index;
					removeButtons();
				}
			});

			bPane.add(button);
			yVal += 50;

			buttons.add(button);

		}

		/* Construct cancel button */
		JButton button = new JButton("Cancel");
		button.setBackground(Color.white);
		button.setBounds(icon.getIconWidth() + 10, yVal, 325, 40);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				choice[0] = Integer.MAX_VALUE;
				removeButtons();
			}
		});
		bPane.add(button);
		buttons.add(button);
	}

	/**
	 * Removes all currently displayed buttons.
	 */
	void removeButtons() {
		if(mLabel != null)
			bPane.remove(mLabel);
		for (JButton b : buttons) {
			b.setVisible(false);
			bPane.remove(b);
		}
	}

	/**
	 * Removes all currently displayed player icons.
	 */
	void removePlayerIcons() {
		if(playerIconLabels.size() > 0) {
			for (JLabel l : playerIconLabels) {
				l.setVisible(false);
				bPane.remove(l);
			}
		}
	}


	/**
	 * Removes all currently displayed cards
	 */
	void removeCards()
	{
		if(playerIconLabels.size() > 0) {
			for (JLabel l : playerCardLabels) {
				l.setVisible(false);
				bPane.remove(l);
			}
		}
	}

	/**
	 * Removes all currently displayed progress markers
	 */
	void removeWrapIcons()
	{
		if(wrapIconLabels.size() > 0) {
			for (JLabel l : wrapIconLabels) {
				l.setVisible(false);
				bPane.remove(l);
			}
		}
	}

	/**
	 * Shows a menu including current player information and their icon.
	 * @param playerInfo Player information.
	 * @param playerImage Path to the image of player icon.
	 */
	void showPlayerInfo(ArrayList<String> playerInfo, String playerImage) {
		playerInfoLabels = new ArrayList<>();
		int yVal = 870 - playerInfo.size() * 30 - 50;
		int xOffset = 20;

		JLabel l = new JLabel("Current turn:");
		l.setBounds(icon.getIconWidth() + xOffset, yVal, 200, 20);
		playerInfoLabels.add(l);
		bPane.add(l);
		yVal+=30;


		/* Construct player info labels */

		l = new JLabel();
		ImageIcon pIcon = new ImageIcon("dice/" + playerImage);
		l.setBounds(icon.getIconWidth() + xOffset+5, yVal, pIcon.getIconWidth(), pIcon.getIconHeight());
		bPane.add(l);
		playerInfoLabels.add(l);
		yVal += pIcon.getIconHeight();
		l.setIcon(pIcon);
		l.setOpaque(true);
		yVal+=10;

		for(String line : playerInfo)
		{
			l = new JLabel(line);
			l.setBounds(icon.getIconWidth() + xOffset, yVal, 325, 20);
			playerInfoLabels.add(l);
			bPane.add(l);
			yVal+=30;
		}
	}
	
	void deletePlayerInfo() {
		for(Component label : playerInfoLabels) {
			label.setVisible(false);
			bPane.remove(label);
		}
	}


}