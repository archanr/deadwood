package Model;
import java.util.*;

/**
 * Board manages the big picture game progression and rewards.
 */
class Board {

    /* Class attributes */
	private static Board instance = null;

	/* Object attributes */
    private int curDay = 1;
	private int dayCount;

	/* Constructors */
	private Board() {}

    /**
     * Grab instance of board.
     * @return Board instance.
     */
	static Board getInstance()
	{
		if(instance == null)
			instance = new Board();

		return instance;
	}

	/* Accessors */
	int getDayCount()
    {
        return dayCount;
    }
    int getCurDay()
    {
        return curDay;
    }

    /**
     * Sets up the board for the game.
     */
    private void setUpBoard()
    {
        ArrayList<Area> areas = Database.getInstance().getAreas();
        ArrayList<Scene> scenes = Database.getInstance().getScenes();
        int sceneID = scenes.size()-1;
        for(Area a : areas)
        {
            if(!a.getName().equals("trailer") && !a.getName().equals("office"))
            {
                Scene ithScene = scenes.remove(sceneID);
                /* Assign the on-card roles the combined coordinates of the card and the role */
                for(Role r : ithScene.getRoles())
                {
                    r.setPlacement(UIPlacement.combine(a.getCardPlacement(), r.getPlacement()));
                }
                a.setScene(ithScene);
                sceneID--;
            }
        }
    }

    /**
     * Dispense the reward for a successful shot.
     * @param player Player that succeeded.
     */
    void rewardSuccessShot(Player player) {
        int roleType = player.getRole().getIType();
        if(roleType == Role.ROLE_STAR)
        {
            player.addCredits(2);
        }
        else if(roleType == Role.ROLE_EXTR)
        {
            player.addDollars(1);
            player.addCredits(1);
        }
    }

    /**
     * Dispense the reward for a failed shot (if necessary).
     * @param player Player that failed a shot.
     */
    void rewardFailShot(Player player) {
        if(player.getRole().getIType() == Role.ROLE_EXTR)
        {
            player.addDollars(1);
        }
    }

    /**
     * Dispense the reward for a successful scene wrap.
     * @param wrappingArea The area, in which there is a wrap.
     */
    void rewardWrap(Area wrappingArea)
    {
        Dice d = Dice.getInstance();
        int budget = wrappingArea.getScene().getBudget();

        /* Group players by their job type */
        ArrayList<Player> stars = PlayerManager.getInstance().getPlayersAt(wrappingArea.getName(), Role.ROLE_STAR);
        ArrayList<Player> extras = PlayerManager.getInstance().getPlayersAt(wrappingArea.getName(), Role.ROLE_EXTR);

        /* Wrap rewards are only distributed if there are players with on-card jobs (stars) */
        if(stars.size() > 0) {

            /* Sort stars in descending order of their job ranks */
            stars.sort((a, b) -> b.getRole().getRank() - a.getRole().getRank());

            /* Roll the number of dice equal to the budget of the movie */
            Integer[] rolls = d.rollDice(budget);

            /* Award star-roles the highest die in descending order of their jobs' ranks (with roll-over) */
            int player = 0;
            int[] starPayout = new int[stars.size()];
            for (int i = 0; i < rolls.length; i++)
            {
                Player p = stars.get(player);

                p.addDollars(rolls[i]);
                starPayout[player] += rolls[i];

                player++;
                if(player == stars.size())
                    player = 0;
            }

            /* Award extra-roles */
            for(int i = 0; i<extras.size(); i++)
            {
                int rank = extras.get(i).getRole().getRank();
                extras.get(i).addDollars(rank);
            }
        }

        /* Remove the stars' role */
        for(Player p : stars)
        {
            p.giveUpRole();
        }
        /* Remove the stars' role */
        for(Player p : extras)
        {
            p.giveUpRole();
        }
    }

    /**
     * Prepares the board, given the number of days in the game.
     * @param dayCount The number of days the game will go on for.
     */
    void setUpBoard(int dayCount)
    {
        this.dayCount = dayCount;
        setUpBoard();
    }

    /**
     * Checks if the end of day condition is met.
     * @return Returns true if it is the end of day, otherwise, returns false.
     */
    boolean isEndOfDay() {
        ArrayList<Area> areas = Database.getInstance().getAreas();
        int activeCount = 0;
        boolean retVal = true;
        for(Area a : areas)
        {
            if(a.sceneActive())
            {
                /* If there are more than area with a Scene card active, it is not the end of day. */
                activeCount++;
                if (activeCount > 1)
                {
                    retVal = false;
                    break;
                }
            }
        }
        return retVal;
    }

    /**
     * Ends the current day.
     */
    void endDay()
    {
        /* Ensure that the end of day condition is met */
        if(isEndOfDay())
        {
            PlayerManager pm = PlayerManager.getInstance();

            curDay++;

            /* Starting a new day if it was not the last day */
            if(curDay <= dayCount) {
                setUpBoard();

                /* Reset the takes in each area */
                for(Area a : Database.getInstance().getAreas())
                {
                    a.resetTakes();
                }

                /* Put all players into the trailer area */
                Area trailers = Database.getInstance().getArea("trailer");
                ArrayList<Player> players = pm.getAllPlayers();
                for (Player p : players) {
                    p.move(trailers);
                    p.giveUpRole();
                    p.resetJustMoved();
                }

            }
            else
                DeadwoodModel.getInstance().setGameOver();
        }
    }
}
