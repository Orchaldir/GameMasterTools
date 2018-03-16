package gm.tools.editor.character;


public class CharacterTemplate implements Character {
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

	@Override
	public int getStrength() {
		return strength;
	}

	@Override
	public int getDexterity() {
		return dexterity;
	}

	@Override
	public int getIntelligence() {
		return intelligence;
	}

	@Override
	public int getHealth() {
		return health;
	}
}
