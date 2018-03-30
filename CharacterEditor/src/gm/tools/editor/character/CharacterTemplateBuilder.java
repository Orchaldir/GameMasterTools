package gm.tools.editor.character;

import gm.tools.editor.character.characteristic.Attribute;
import gm.tools.editor.character.characteristic.Characteristic;
import gm.tools.editor.character.skill.Skill;
import gm.tools.editor.character.trait.Trait;

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

	// traits
	private final Map<String, Trait> traits = new HashMap<>();

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

	public CharacterTemplateBuilder setAttribute(Attribute attribute, int value) {
		switch (attribute) {
			case DEXTERITY:
				this.dexterityModifier = value;
			case HEALTH:
				this.healthModifier = value;
			case INTELLIGENCE:
				this.intelligenceModifier = value;
			case PERCEPTION:
				this.perceptionModifier = value;
			case STRENGTH:
				this.strengthModifier = value;
			case WILL:
				this.willModifier = value;
		}
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

	public void setCharacteristicModifier(Characteristic characteristic, int value) {
		switch (characteristic) {
			case BASIC_MOVE:
				basicMoveModifier = value;
				return;
			case BASIC_SPEED:
				basicSpeedModifier = value;
				return;
			case FATIGUE_POINTS:
				fatiguePointsModifier = value;
				return;
			case HIT_POINTS:
				hitPointsModifier = value;
				return;
			case SIZE_MODIFIER:
				sizeModifier = value;
				return;
		}

		throw new IllegalArgumentException(String.format("Unsupported characteristic %s!", characteristic));
	}

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
		skills.put(skill, relativeLevel);
		return this;
	}

	// traits

	public CharacterTemplateBuilder addTrait(Trait trait) {
		traits.put(trait.getName(), trait);
		return this;
	}

	//

	public CharacterTemplate createCharacterTemplate() {
		return new CharacterTemplate(name,
				dexterityModifier, healthModifier, intelligenceModifier, perceptionModifier, strengthModifier, willModifier,
				hitPointsModifier, fatiguePointsModifier,
				basicSpeedModifier, basicMoveModifier, sizeModifier,
				skills, traits);
	}
}