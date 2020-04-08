package Model;
import java.util.ArrayList;

class Zone
{
	private ArrayList<Role> roles;


	ArrayList<Role> getRoles(){return this.roles;}

	void setRoles(ArrayList<Role> roles)
	{
		this.roles = roles;
	}
}