package gm.tools.editor.character;

import gm.tools.editor.character.skill.Skill;

import java.util.HashMap;
import java.util.Map;

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
	private int basicMoveModifier = 0;
	private int sizeModifier = 0;

	// skills
	private Map<Skill, Integer> skills = new HashMap<>();

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

	public CharacterTemplateBuilder setBasicMoveModifier(int basicMoveModifier) {
		this.basicMoveModifier = basicMoveModifier;
		return this;
	}

	public CharacterTemplateBuilder setSizeModifier(int sizeModifier) {
		this.sizeModifier = sizeModifier;
		return this;
	}

	// skills

	public CharacterTemplateBuilder addSkill(Skill skill, int relativeLevel) {
		if (relativeLevel < 1) {
			throw new IllegalArgumentException(String.format("Relative level %d of skill %s is too low!", relativeLevel, skill.getName()));
		}
		this.skills.put(skill, relativeLevel);
		return this;
	}

	//

	public CharacterTemplate createCharacterTemplate() {
		return new CharacterTemplate(name, strength, dexterity, intelligence, health, hitPointsModifier,
				willModifier, perceptionModifier, fatiguePointsModifier,
				basicSpeedModifier, basicMoveModifier, sizeModifier,
				skills);
	}
}