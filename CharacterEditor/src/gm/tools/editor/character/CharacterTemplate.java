package gm.tools.editor.character;

import gm.tools.editor.character.characteristic.Attribute;
import gm.tools.editor.character.skill.Skill;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.Map;
import java.util.Set;

@AllArgsConstructor
@Data
public class CharacterTemplate implements Character {
	private final String name;

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

		throw new IllegalArgumentException(String.format("Unknown attribute %s!", attribute));
	}

	// skill

	@Override
	public Set<Skill> getSkills() {
		return skills.keySet();
	}

	@Override
	public int getRelativeSkillLevel(Skill skill) {
		return skills.getOrDefault(skill, -1);
	}
}
