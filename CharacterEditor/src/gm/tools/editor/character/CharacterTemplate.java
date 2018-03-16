package gm.tools.editor.character;


public class CharacterTemplate {

	public static final int ATTRIBUTE_DEFAULT_VALUE = 10;

	public static final int STRENGTH_COST = 10;
	public static final int DEXTERITY_COST = 20;
	public static final int INTELLIGENCE_COST = 20;
	public static final int HEALTH_COST = 10;

	private final String name;

	private final int strength, dexterity, intelligence, health;

	public CharacterTemplate(String name, int strength, int dexterity, int intelligence, int health) {
		this.name = name;
		this.strength = strength;
		this.dexterity = dexterity;
		this.intelligence = intelligence;
		this.health = health;
	}

	public String getName() {
		return name;
	}

	public int getStrength() {
		return strength;
	}

	public int getDexterity() {
		return dexterity;
	}

	public int getIntelligence() {
		return intelligence;
	}

	public int getHealth() {
		return health;
	}

	public int getAttributeModifier(int value) {
		return value - ATTRIBUTE_DEFAULT_VALUE;
	}

	public int calculateCharacterPoints() {
		int strengthCost = getAttributeModifier(strength) * STRENGTH_COST;
		int dexterityCost = getAttributeModifier(dexterity) * DEXTERITY_COST;
		int intelligenceCost = getAttributeModifier(intelligence) * INTELLIGENCE_COST;
		int healthCost = getAttributeModifier(health) * HEALTH_COST;
		return strengthCost + dexterityCost + intelligenceCost + healthCost;
	}
}
