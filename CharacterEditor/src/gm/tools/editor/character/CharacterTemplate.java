package gm.tools.editor.character;


public class CharacterTemplate implements Character {

	private final String name;

	// attributes
	private final int strength, dexterity, intelligence, health;
	// secondary characteristics
	private final int hitPointsModifier;
	private final int willModifier;
	private final int perceptionModifier;

	protected CharacterTemplate(String name, int strength, int dexterity, int intelligence, int health, int hitPointsModifier, int willModifier, int perceptionModifier) {
		this.name = name;
		this.strength = strength;
		this.dexterity = dexterity;
		this.intelligence = intelligence;
		this.health = health;
		this.hitPointsModifier = hitPointsModifier;
		this.willModifier = willModifier;
		this.perceptionModifier = perceptionModifier;
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

	@Override
	public int getHitPointsModifier() {
		return hitPointsModifier;
	}

	@Override
	public int getWillModifier() {
		return willModifier;
	}

	@Override
	public int getPerceptionModifier() {
		return perceptionModifier;
	}
}
