package Model;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Database stores of all Scene cards and all Areas for easy access.
 */

class Database
{
    /* Class attributes */
	private static Database instance = null;

	/* Object attributes */
	private ArrayList<Scene> allScenes;
	private ArrayList<Area> allAreas;

	/* Constructor */
	private Database(){
		allScenes = Parsing.parseScenes();
		allAreas = Parsing.parseAreas();
		Collections.shuffle(allScenes);
	}

    /* Accessors */
    ArrayList<Scene> getScenes(){ return allScenes; }
    ArrayList<Area> getAreas(){ return allAreas; }

    /**
     * Returns an Area object corresponding to the given name.
     * @param areaName Name of the area to be found.
     * @return Sought-for area object.
     */
    Area getArea(String areaName){
        int match = -1;
        for(int i = 0; i<allAreas.size(); i++)
        {
            if(allAreas.get(i).getName().equals(areaName)) {
                match = i;
                break;
            }
        }
        return allAreas.get(match);
    }

    /**
     * Grab an instance of the Database object.
     * @return Database instance.
     */
	static Database getInstance()
	{
		if(instance == null)
			instance = new Database();
		return instance;
	}
}