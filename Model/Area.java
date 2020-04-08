package Model;

import java.util.ArrayList;

/**
 * Area represents a single room on the Deadwood board.
 */
class Area extends Zone
{
    /* Object attributes */

	private int areaID;
	private boolean active;
	private String name;
	private Scene curScene;
	private ArrayList<String> neighbors;
	private int curProg;
	private int wrapProg;
	private UIPlacement cardPlacement;
	private ArrayList<Take> takes;

	/* Constructors */

	Area(int id, ArrayList<Role> roles, ArrayList<String> neighbors, String name)
	{
		this(id, null, roles, neighbors, name, 0, Integer.MAX_VALUE, null, null);
	}

	Area(int id, ArrayList<Role> roles, ArrayList<String> neighbors, String name, int wrapProg, ArrayList<Take> takes, UIPlacement cardPlacement)
	{
		this(id, null, roles, neighbors, name, 0, wrapProg, takes, cardPlacement);
	}
	
	private Area(int id, Scene scene, ArrayList<Role> roles, ArrayList<String> neighbors, String name, int curProg,
				int wrapProg, ArrayList<Take> takes, UIPlacement cardPlacement)
	{
		this.areaID = id;
		this.curScene = scene;
		this.name = name;
		this.curProg = curProg;
		this.wrapProg = wrapProg;

		this.active = (scene != null);

		if(roles == null)
			this.setRoles(new ArrayList<>());
		else
			this.setRoles(roles);
		
		if(neighbors == null)
			this.neighbors = new ArrayList<>();
		else
			this.neighbors = neighbors;

		this.takes = takes;
		// Make sure the takes are sorted in reverse order: ..., 3, 2, 1
		if(takes != null && takes.size() > 0)
			takes.sort((a, b) -> b.getTakeID() - a.getTakeID());

		this.cardPlacement = cardPlacement;

	}

	/* Accessors */

	boolean sceneActive(){return this.active;}
    Scene getScene(){return this.curScene;}
	ArrayList<String> getNeighbors(){return this.neighbors;}
	String getName(){return name;}
	UIPlacement getCardPlacement() { return cardPlacement; }
    ArrayList<Take> getTakes() {return takes;}


    /**
     * Discards the current scene
     */
	private void discardScene()
	{
		this.curScene = null;
		this.active = false;
		this.curProg = 0;
		this.cardPlacement.setImagePath("");
		for(Role r : getRoles())
		{
			r.setNotTaken();
		}
		PlayerManager.getInstance().resetPlayerPositions();
	}

    /**
     * Places the given scene to the area
     * @param sc the scene to be attached
     */
	void setScene(Scene sc)
	{
		this.curScene = sc;
		this.active = true;
		this.cardPlacement.setImagePath(curScene.getImage());
	}

    /**
     * Resets all takes in the area.
     */
	void resetTakes()
	{
		if(takes != null)
		{
			for (Take t : takes)
			{
				t.setDone(false);
			}
		}
	}

    /**
     * Increments the scene progress, distributes the reward to the player responsible for the increase.
     * @param p The player who caused the the increment.
     */
	void increaseProg(Player p)
	{
		Board b = Board.getInstance();
		curProg++;

		// Set the next take to be finished
		for(Take t: takes)
		{
			if(!t.isDone())
			{
				t.setDone(true);
				break;
			}
		}

		// Distribute reward
		b.rewardSuccessShot(p);

		// The case for a wrap
		if(curProg >= wrapProg)
		{
			b.rewardWrap(this);
			discardScene();
			resetTakes();

			// The case for the end of the day
			if(b.isEndOfDay())
				b.endDay();
		}

		p.resetPC();
	}
}