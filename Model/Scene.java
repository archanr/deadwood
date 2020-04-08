package Model;

import java.util.*;

class Scene extends Zone
{
	/* Object variables */
	private int sceneID;
	private int budget;
	private String sceneName;
	private String sceneDesc;
	private boolean flipped;
	private String imgFront;
	private String imgBack;

	/* Constructor */
	Scene(int sceneID, ArrayList<Role> roles, int budget, String sceneName, String sceneDesc, String imgFront)
	{
		this.sceneID = sceneID;
		this.budget = budget;
		this.sceneName = sceneName;
		this.sceneDesc = sceneDesc;
		
		if(roles == null)
			this.setRoles(new ArrayList<Role>());
		else
			this.setRoles(roles);

		this.imgBack = "CardBack.jpg";

		this.imgFront = imgFront;
	}

	/* Accessors */
	int getBudget(){return this.budget;}

	/**
	 * Give the appropriate scene card image.
	 * @return Scene image path.
	 */
	String getImage(){
		if(flipped)
			return imgFront;
		else
			return imgBack;
	}

	/**
	 * Flip the scene card.
	 */
	void flip()
	{
		flipped = true;
	}
}