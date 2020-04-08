package Model;

import java.util.ArrayList;

class UpgradeOffice extends Area{

    private ArrayList<Upgrade> upgrades;

    UpgradeOffice(int id, ArrayList<String> neighbors, ArrayList<Upgrade> upgrades)
    {
        super(id, null, neighbors, "office");
        this.upgrades = upgrades;
    }

    ArrayList<Upgrade> getAvailableUpgrades(Player p)
    {
        ArrayList<Upgrade> available = new ArrayList<>();

        for(int i = 0; i<upgrades.size(); i++)
        {
            Upgrade u = upgrades.get(i);

            if(u.getCurrency() == Upgrade.CUR_CREDIT)
            {
                if(u.getPrice() <= p.getCredits() && u.getRank() > p.getRank())
                    available.add(u);
            }
            else if((u.getCurrency() == Upgrade.CUR_DOLLAR))
            {
                if(u.getPrice() <= p.getDollars() && u.getRank() > p.getRank())
                    available.add(u);
            }
        }

        return available;
    }
}
