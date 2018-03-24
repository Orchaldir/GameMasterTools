package gm.tools.editor.character;

import gm.tools.editor.character.skill.Skill;

import java.util.HashMap;
import java.util.Map;

public class CharacterTemplateBuilder {
	private final String name;

	// attributes
	private int dexterityModifier = 0;
	private int healthModifier = 0;
	private int intelligenceModifier = 0;
	private int perceptionModifier = 0;
	private int strengthModifier = 0;
	private int willModifier = 0;

	// secondary characteristics
	private int hitPointsModifier = 0;
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
		this.strengthModifier = strength;
		this.dexterityModifier = dexterity;
		this.intelligenceModifier = intelligence;
		this.healthModifier = health;
		return this;
	}

	public CharacterTemplateBuilder setStrength(int strengthModifier) {
		this.strengthModifier = strengthModifier;
		return this;
	}

	public CharacterTemplateBuilder setDexterity(int dexterityModifier) {
		this.dexterityModifier = dexterityModifier;
		return this;
	}

	public CharacterTemplateBuilder setIntelligence(int intelligenceModifier) {
		this.intelligenceModifier = intelligenceModifier;
		return this;
	}

	public CharacterTemplateBuilder setHealth(int healthModifier) {
		this.healthModifier = healthModifier;
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

	// secondary characteristics

	public CharacterTemplateBuilder setHitPointsModifier(int hitPointsModifier) {
		this.hitPointsModifier = hitPointsModifier;
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
		return new CharacterTemplate(name,
				dexterityModifier, healthModifier, intelligenceModifier, perceptionModifier, strengthModifier, willModifier,
				hitPointsModifier, fatiguePointsModifier,
				basicSpeedModifier, basicMoveModifier, sizeModifier,
				skills);
	}
}