package gm.tools.editor.character;

public class CharacterTemplateBuilder {
	private final String name;

	// attributes
	private int strength = 10;
	private int dexterity = 10;
	private int intelligence = 10;
	private int health = 10;

	// secondary characteristics
	private int hitPointsModifier = 0;
	private int willModifier = 0;
	private int perceptionModifier = 0;
	private int fatiguePointsModifier = 0;
	private int basicSpeedModifier = 0;

	public CharacterTemplateBuilder(String name) {
		this.name = name;
	}

	// attributes

	public CharacterTemplateBuilder setAttributes(int strength, int dexterity, int intelligence, int health) {
		this.strength = strength;
		this.dexterity = dexterity;
		this.intelligence = intelligence;
		this.health = health;
		return this;
	}

	public CharacterTemplateBuilder setStrength(int strength) {
		this.strength = strength;
		return this;
	}

	public CharacterTemplateBuilder setDexterity(int dexterity) {
		this.dexterity = dexterity;
		return this;
	}

	public CharacterTemplateBuilder setIntelligence(int intelligence) {
		this.intelligence = intelligence;
		return this;
	}

	public CharacterTemplateBuilder setHealth(int health) {
		this.health = health;
		return this;
	}

	// secondary characteristics

	public CharacterTemplateBuilder setHitPointsModifier(int hitPointsModifier) {
		this.hitPointsModifier = hitPointsModifier;
		return this;
	}

	public CharacterTemplateBuilder setWillModifier(int willModifier) {
		this.willModifier = willModifier;
		return this;
	}

	public CharacterTemplateBuilder setPerceptionModifier(int perceptionModifier) {
		this.perceptionModifier = perceptionModifier;
		return this;
	}

	public CharacterTemplateBuilder setFatiguePointsModifier(int fatiguePointsModifier) {
		this.fatiguePointsModifier = fatiguePointsModifier;
		return this;
	}

	public CharacterTemplateBuilder setBasicSpeedModifier(int basicSpeedModifier) {
		this.basicSpeedModifier = basicSpeedModifier;
		return this;
	}

	//

	public CharacterTemplate createCharacterTemplate() {
		return new CharacterTemplate(name, strength, dexterity, intelligence, health, hitPointsModifier,
				willModifier, perceptionModifier, fatiguePointsModifier, basicSpeedModifier);
	}
}