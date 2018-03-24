package gm.tools.editor.character;

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
	private final int strength, dexterity, intelligence, health;

	// secondary characteristics
	private final int hitPointsModifier;
	private final int willModifier;
	private final int perceptionModifier;
	private final int fatiguePointsModifier;
	private final int basicSpeedModifier;
	private final int basicMoveModifier;
	private final int sizeModifier;

	// skills
	@Getter(AccessLevel.NONE)
	private final Map<Skill, Integer> skills;

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
