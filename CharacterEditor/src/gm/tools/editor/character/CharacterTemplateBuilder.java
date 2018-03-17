package gm.tools.editor.character;

public class CharacterTemplateBuilder {
	private final String name;

	private int strength = 10;
	private int dexterity = 10;
	private int intelligence = 10;
	private int health = 10;

	private int hitPointsModifier = 0;

	public CharacterTemplateBuilder(String name) {
		this.name = name;
	}

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

	public CharacterTemplateBuilder setHitPointsModifier(int hitPointsModifier) {
		this.hitPointsModifier = hitPointsModifier;
		return this;
	}

	public CharacterTemplate createCharacterTemplate() {
		return new CharacterTemplate(name, strength, dexterity, intelligence, health, hitPointsModifier);
	}
}