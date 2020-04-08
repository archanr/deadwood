package Model;

public class Role
{
	static final int ROLE_STAR = 1;
	static final int ROLE_EXTR = 2;

	private int roleID;
	private int iRoleType;
	private String sRoleType;
	private boolean roleTaken;
	private int rank;
	private String name;
	private String line;
	private UIPlacement placement;

	Role(int roleID, int iRoleType, int rank, String name, String line, UIPlacement placement)
	{
		this.roleID = roleID;
		this.iRoleType = iRoleType;
		this.sRoleType = getSRT(iRoleType);
		this.roleTaken = false;
		this.rank = rank;
		this.name = name;
		this.line = line;
		this.placement = placement;
	}

	public String getSType(){return sRoleType;}

	int getIType(){return iRoleType;}

	public int getRank(){return rank;}

	public String getName(){return name;}

	UIPlacement getPlacement() { return placement; }

	void setPlacement(UIPlacement placement) {
		this.placement = placement;
	}

	boolean availableTo(Player p){
		return !roleTaken && p.getRank() >= rank;
	}

	void setTaken(Player p)
	{
		if(availableTo(p))
		{
			this.roleTaken = true;
		}
	}

	void setNotTaken()
	{
		this.roleTaken = false;
	}

	private String getSRT(int iType)
	{
		String retVal;
		switch(iType)
		{
			case ROLE_STAR:
				retVal = "Starring";
				break;
			case ROLE_EXTR:
				retVal = "Extra";
				break;
			default:
				retVal = "ERROR";
		}
		return retVal;
	}
}
