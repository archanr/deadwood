package Model;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import java.io.*;
import java.util.*;

/**
 * Parsing reads the xml files and stores that data in a data structure.
 */

class Parsing {
	private static final String CARDS_PATH = "Model/cards.xml";
	private static final String BOARD_PATH = "Model/board.xml";

	private static int roleID = 0;

    /**
     * Construct a list of Scene objects from an xml file.
     * @return List of scenes constructed from the document at CARDS_PATH.
     */
	static ArrayList<Scene> parseScenes() {
		ArrayList<Scene> scenes = new ArrayList<>();
		try
		{
			Document doc = createDoc(CARDS_PATH);

			NodeList cardList = doc.getElementsByTagName("card");

			for (int i = 0; i < cardList.getLength(); i++) {
				Node cardNode = cardList.item(i);
				if (cardNode.getNodeType() == Node.ELEMENT_NODE) {
					Element cardElement = (Element) cardNode;

					String name = cardElement.getAttribute("name");

					String img = cardElement.getAttribute("img");

					int budget = Integer.parseInt(cardElement.getAttribute("budget"));

					int id = Integer
							.parseInt(((Element) cardElement.getElementsByTagName("scene").item(0)).getAttribute("number"));

					String cardDescription = cardElement.getElementsByTagName("scene").item(0).getTextContent();

					// arraylist containing parts
					ArrayList<Role> roles = parseRoles(cardElement, Role.ROLE_STAR);

					Scene scene = new Scene(id, roles, budget, name, cardDescription, img);
					scenes.add(scene);
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("Error while parsing the cards. " + e);
		}

		return scenes;
	}

    /**
     * Construct a list of Area objects from an xml file.
     * @return List of scenes constructed from the document at BOARD_PATH.
     */
	static ArrayList<Area> parseAreas() {
		ArrayList<Area> areas = new ArrayList<>();
		try {

			Document doc = createDoc(BOARD_PATH);

			NodeList boardList = doc.getElementsByTagName("board");
			Node boardNode = boardList.item(0);

			if (boardNode.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) boardNode;

				NodeList setList = element.getElementsByTagName("set");
				for (int areaID = 0; areaID < setList.getLength(); areaID++) {
					Node set = setList.item(areaID);

					if (set.getNodeType() == Node.ELEMENT_NODE) {
						Element setElement = (Element) set;
						String setName = setElement.getAttribute("name");

						ArrayList<String> neighborList = parseNeighbors(setElement);


						ArrayList<Take> takeList = parseTakes(setElement);
						UIPlacement cardPlacement = parseUIP(setElement);

						int wrapProg = takeList.size();

						ArrayList<Role> roles = parseRoles(setElement, Role.ROLE_EXTR);

						areas.add(new Area(areaID, roles, neighborList, setName, wrapProg, takeList, cardPlacement));
					}
				}
				setList = element.getElementsByTagName("trailer");
				Node trailerN = setList.item(0);
				if (trailerN.getNodeType() == Node.ELEMENT_NODE) {
					Element setElement = (Element) trailerN;
					int areaID = areas.size();
					String areaName = "trailer";
					ArrayList<String> neighborList = parseNeighbors(setElement);

					areas.add(new Area(areaID, null, neighborList, areaName));
				}

				setList = element.getElementsByTagName("office");
				Node officeN = setList.item(0);
				if (officeN.getNodeType() == Node.ELEMENT_NODE) {
					Element setElement = (Element) officeN;
					int areaID = areas.size();

					ArrayList<String> neighborList = parseNeighbors(setElement);

					ArrayList<Upgrade> upgrades = parseUpgrades(setElement);

					areas.add(new UpgradeOffice(areaID, neighborList, upgrades));
				}
			}
		}
		catch (Exception e) {
			System.out.println("Error for parsing the board: " + e);
			e.printStackTrace();
		}

		return areas;
	}

    /**
     * Constructs a list of take objects from an Element.
     * @param setElement Element from which to get takes.
     * @return List of takes constructed from the given Element.
     */
	private static ArrayList<Take> parseTakes(Element setElement)
	{
		ArrayList<Take> takeList = new ArrayList<>();

		NodeList takes = setElement.getElementsByTagName("take");

		for(int takeCount = 0 ; takeCount < takes.getLength(); takeCount++)
		{
			Node takeNode = takes.item(takeCount);

			Element takeElement = (Element) takeNode;
			int takeID = Integer.parseInt(takeElement.getAttribute("number"));
			UIPlacement takeUIP = parseUIP(takeElement);
			takeUIP.setImagePath("shot.png");

			takeList.add(new Take(takeID, takeUIP));
		}
		return takeList;
	}

    /**
     * Constructs a UI placement object from a passed element.
     * @param elem Element containing the area to be put inside the UIP.
     * @return User Interface Placement object.
     */
	private static UIPlacement parseUIP(Element elem)
	{
		UIPlacement UIP;

		NodeList areaNodes = elem.getElementsByTagName("area");

		if(areaNodes.getLength() > 0)
		{
			Element area = (Element) areaNodes.item(0);
			int x = Integer.parseInt(area.getAttribute("x"));
			int y = Integer.parseInt(area.getAttribute("y"));
			int h = Integer.parseInt(area.getAttribute("h"));
			int w = Integer.parseInt(area.getAttribute("w"));
			UIP = new UIPlacement(x, y, h, w);
		}
		else
			throw new RuntimeException("Number of areas is too small.");

		return UIP;
	}

    /**
     * Constructs a list of neighbors.
     * @param setElement Element containing neighbors.
     * @return List of neighbors.
     */
	private static ArrayList<String> parseNeighbors(Element setElement)
	{
		ArrayList<String> neighborList = new ArrayList<String>();
		NodeList setNeighbor = setElement.getElementsByTagName("neighbor");

		for (int k = 0; k < setNeighbor.getLength(); k++) {
			Element neighbor = (Element) setNeighbor.item(k);
			String neighborName = neighbor.getAttribute("name");
			neighborList.add(neighborName);
		}

		return neighborList;
	}

    /**
     * Constructs a list of upgrades.
     * @param setElement Element containing upgrades.
     * @return List of upgrades.
     */
	private static ArrayList<Upgrade> parseUpgrades(Element setElement)
	{
		ArrayList<Upgrade> upgradeList = new ArrayList<Upgrade>();

		NodeList setNeighbor = setElement.getElementsByTagName("upgrade");

		for (int k = 0; k < setNeighbor.getLength(); k++) {
			Element upgrade = (Element) setNeighbor.item(k);
			char cCurrency = upgrade.getAttribute("currency").toLowerCase().charAt(0);
			int rank = Integer.parseInt(upgrade.getAttribute("level"));
			int price = Integer.parseInt(upgrade.getAttribute("amt"));

			upgradeList.add(new Upgrade(rank, price, cCurrency));
		}

		return upgradeList;
	}

    /**
     * Creates a document object given the path name.
     * @param pathname Path to the document.
     * @return Document object.
     * @throws Exception That it does.
     */
	private static Document createDoc(String pathname) throws Exception
	{
		File inputFile = new File(pathname);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();

		Document doc = db.parse(inputFile);
		doc.getDocumentElement().normalize();

		return doc;
	}

    /**
     * Constructs a list of Roles given an Element and a role type.
     * @param elem Element containing Roles.
     * @param iRoleType Type of roles (Star/Extra).
     * @return List of roles.
     */
	private static ArrayList<Role> parseRoles(Element elem, int iRoleType)
	{
		// arraylist containing parts
		ArrayList<Role> roles = new ArrayList<Role>();

		NodeList part = elem.getElementsByTagName("part");

		for (int j = 0; j < part.getLength(); j++) {
			Node partNode = part.item(j);
			if (partNode.getNodeType() == Node.ELEMENT_NODE) {
				Element partElement = (Element) partNode;

				String partName = partElement.getAttribute("name");
				int partLevel = Integer.parseInt(partElement.getAttribute("level"));
				UIPlacement roleUIP = parseUIP(partElement);
				String partLine = partElement.getElementsByTagName("line").item(0).getTextContent();

				// create new instance of parts class
				roles.add(new Role(roleID, iRoleType, partLevel, partName, partLine, roleUIP));
				roleID++;
			}
		}
		return roles;
	}

}
