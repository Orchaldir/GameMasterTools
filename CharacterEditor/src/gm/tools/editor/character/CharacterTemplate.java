package gm.tools.editor.character;


public class CharacterTemplate implements Character {

	private final String name;

	// attributes
	private final int strength, dexterity, intelligence, health;
	// secondary characteristics
	private final int hitPointsModifier;
	private final int willModifier;
	private final int perceptionModifier;
	private final int fatiguePointsModifier;
	private final int basicSpeedModifier;
	private final int basicMoveModifier;
	private final int sizeModifier;

	protected CharacterTemplate(String name, int strength, int dexterity, int intelligence, int health,
								int hitPointsModifier, int willModifier, int perceptionModifier, int fatiguePointsModifier,
								int basicSpeedModifier, int basicMoveModifier, int sizeModifier) {
		this.name = name;
		// attributes
		this.strength = strength;
		this.dexterity = dexterity;
		this.intelligence = intelligence;
		this.health = health;
		// secondary characteristics
		this.hitPointsModifier = hitPointsModifier;
		this.willModifier = willModifier;
		this.perceptionModifier = perceptionModifier;
		this.fatiguePointsModifier = fatiguePointsModifier;
		this.basicSpeedModifier = basicSpeedModifier;
		this.basicMoveModifier = basicMoveModifier;
		this.sizeModifier = sizeModifier;
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

	@Override
	public int getFatiguePointsModifier() {
		return fatiguePointsModifier;
	}

	@Override
	public int getBasicSpeedModifier() {
		return basicSpeedModifier;
	}

	@Override
	public int getBasicMoveModifier() {
		return basicMoveModifier;
	}

	@Override
	public int getSizeModifier() {
		return sizeModifier;
	}
}
