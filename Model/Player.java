package Model;

/**
 * Player objects represent individual users playing Deadwood.
 */
class Player {

	/* Class attributes */
	private final static char[] colors = {'r', 'g', 'b', 'o', 'p', 'c', 'v', 'y', 'w'};

	/* Object attributes */
    private int playerId;
	private int rank;
    private int credits;
    private int dollars;
    private Area curPosition;
    private Role curRole;
    private int finalScore;
    private String image;
    private UIPlacement placement;
    private boolean justMoved;
	private int practiceCnt;

    /* Constructors */
	Player(){}
	
	Player(int id, int rank, int cred, int doll, Area curPos, Role curRole, int score)
	{
		playerId = id;
		this.rank = rank;
		credits = cred;
		dollars = doll;
		curPosition = curPos;
		this.curRole = curRole;
		finalScore = score;
		image = "" + colors[id-1] + rank + ".png";
		justMoved = false;
	}

	/* Accessors */
	UIPlacement getPlacement(){ return placement; }
	String getImage() {return image;}
	int getID(){return playerId;}
	int getRank() {return rank;}
	int getDollars(){return  dollars;}
	int getCredits(){return credits;}
	int getFinalScore(){return finalScore;}
	Area getPosition() {return curPosition;}
	boolean getJustMoved(){return justMoved;}
	int getPC(){return this.practiceCnt;}

	Role getRole(){return curRole;}

	private void setImage(String image) {
		this.image = image;
	}

	void resetJustMoved(){justMoved = false;}

	void setPlacement(UIPlacement placement)
	{
		this.placement = placement.clone();
		this.placement.setImagePath(image);
	}

	/**
	 * Moves the player to the given position. Ends the turn.
	 * @param newPosition Area to be
	 */
	void move(Area newPosition) {
        this.curPosition = newPosition;
        if(curPosition.sceneActive())
        	curPosition.getScene().flip();

        resetPlacement(curPosition);

        justMoved = true;
    }

	/**
	 * Resets the placement to the default placement in a given area.
	 * @param position The position to reset the player to.
	 */
	void resetPlacement(Area position)
	{
		if(position.getName().equals("trailer"))
			placement = UIPlacement.trailers.clone();
		else if(position.getName().equals("office"))
			placement = UIPlacement.office.clone();
		else
			placement = position.getCardPlacement().clone().setDimensions(20, 20);

		this.placement.setImagePath(image);
	}

	/**
	 * Upgrades the player using the Upgrade object. Ends the turn.
	 * @param u Upgrade object to be applied.
	 */
    void upgrade(Upgrade u) {
        if(u.getCurrency() == Upgrade.CUR_DOLLAR)
		{
			if(dollars >= u.getPrice() && rank < u.getRank())
			{
				dollars -= u.getPrice();
				rank = u.getRank();
			}
			else
				throw new IllegalArgumentException("Invalid upgrade.");
		}
        else if(u.getCurrency() == Upgrade.CUR_CREDIT)
		{
			if(credits >= u.getPrice() && rank < u.getRank())
			{
				credits -= u.getPrice();
				rank = u.getRank();
			}
			else
				throw new IllegalArgumentException("Invalid upgrade.");
		}
		/* Update the player picture */
        setImage("" + colors[playerId-1] + rank + ".png");
        this.placement.setImagePath(image);
		PlayerManager.getInstance().nextTurn();
    }

	/**
	 * Assigns the player a given Role. Ends the turn.
	 * @param newRole The role to be assigned.
	 */
	void takeRole(Role newRole) {
        curRole = newRole;
        newRole.setTaken(this);

        placement = newRole.getPlacement().clone();
        placement.setImagePath(image);
        PlayerManager.getInstance().nextTurn();
    }

	/**
	 * Gives up the current role.
	 */
	void giveUpRole()
	{
		curRole = null;
	}

	/**
	 * Sets the score to the given value.
	 * @param score The new score.
	 */
    void setScore(int score) {finalScore = score;}

	/**
	 * Adds a given number of credits to the player.
	 * @param cred Number of credits to be given.
	 */
	void addCredits(int cred) {
		if(cred >= 0 || credits + cred >= 0)
			credits += cred;
	}

	/**
	 * Adds a given number of dollars to the player.
	 * @param doll Number of dollars to be given.
	 */
	void addDollars(int doll) {
		if(doll >= 0 || dollars + doll >= 0)
			dollars += doll;
	}

	/**
	 * Performs act action on the current role.
	 */
	void act() {
		
		if(isWorking())
        {
        	Dice d = Dice.getInstance();
			int roll = d.rollSix();
			
			Scene curScene = curPosition.getScene();
			int budget = curScene.getBudget();

			if(roll + practiceCnt >= budget)
			{
				curPosition.increaseProg(this);
			}
			else
			{
				Board.getInstance().rewardFailShot(this);
			}
		}
		PlayerManager.getInstance().nextTurn();
    }

	/**
	 * Perform rehearse action
	 */
	void rehearse() {
		if(curPosition.sceneActive())
		{
			if(practiceCnt<curPosition.getScene().getBudget())
				practiceCnt++;
		}
		PlayerManager.getInstance().nextTurn();
    }

	/**
	 * Checks if the player has a role associated with them.
	 * @return True if the player has a job, otherwise, false.
	 */
	boolean isWorking() {
		return curRole != null;
    }


	/**
	 * Resets the practice counter
	 */
	void resetPC()
	{
		practiceCnt = 0;
	}


}