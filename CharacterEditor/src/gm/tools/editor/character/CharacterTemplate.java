package gm.tools.editor.character;

import gm.tools.editor.character.characteristic.Appearance;
import gm.tools.editor.character.characteristic.Attribute;
import gm.tools.editor.character.characteristic.Characteristic;
import gm.tools.editor.character.skill.Skill;
import gm.tools.editor.character.trait.Trait;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.Collection;
import java.util.Map;

@AllArgsConstructor
@Data
public class CharacterTemplate implements Character {
	private final String name;

	//
	private final Appearance appearance;

	// attributes
	private final int dexterityModifier;
	private final int healthModifier;
	private final int intelligenceModifier;
	private final int perceptionModifier;
	private final int strengthModifier;
	private final int willModifier;

	// secondary characteristics
	private final int hitPointsModifier;
	private final int fatiguePointsModifier;
	private final int basicSpeedModifier;
	private final int basicMoveModifier;
	private final int sizeModifier;

	// skills
	@Getter(AccessLevel.NONE)
	private final Map<Skill, Integer> skills;

	// traits
	@Getter(AccessLevel.NONE)
	private final Map<String, Trait> traits;

	// attribute

	@Override
	public int getAttributeModifier(Attribute attribute) {
		switch (attribute) {
			case DEXTERITY:
				return dexterityModifier;
			case HEALTH:
				return healthModifier;
			case INTELLIGENCE:
				return intelligenceModifier;
			case PERCEPTION:
				return perceptionModifier;
			case STRENGTH:
				return strengthModifier;
			case WILL:
				return willModifier;
		}

		throw new IllegalArgumentException(String.format("Unsupported attribute %s!", attribute));
	}

	// characteristics

	@Override
	public int getCharacteristicModifier(Characteristic characteristic) {
		switch (characteristic) {
			case BASIC_MOVE:
				return basicMoveModifier;
			case BASIC_SPEED:
				return basicSpeedModifier;
			case FATIGUE_POINTS:
				return fatiguePointsModifier;
			case HIT_POINTS:
				return hitPointsModifier;
			case SIZE_MODIFIER:
				return sizeModifier;
		}

		throw new IllegalArgumentException(String.format("Unsupported characteristic %s!", characteristic));
	}

	// skill

	@Override
	public Collection<Skill> getSkills() {
		return skills.keySet();
	}

	@Override
	public int getRelativeSkillLevel(Skill skill) {
		return skills.getOrDefault(skill, -1);
	}

	// traits

	@Override
	public Collection<Trait> getTraits() {
		return traits.values();
	}
}
