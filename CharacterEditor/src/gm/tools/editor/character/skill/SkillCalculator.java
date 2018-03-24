package gm.tools.editor.character.skill;

import gm.tools.editor.character.Character;
import gm.tools.editor.character.characteristic.Attribute;
import gm.tools.editor.character.characteristic.AttributeCalculator;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class SkillCalculator {
	private final AttributeCalculator attributeCalculator;

	public Map<Skill, Integer> calculate(Character character) {
		Map<Skill, Integer> skills = new HashMap<>();

		for (Skill skill : character.getSkills()) {
			int level = calculateLevel(character, skill);

			skills.put(skill, level);
		}

		return skills;
	}

	public int calculateLevel(Character character, Skill skill) {
		int relativeLevel = character.getRelativeSkillLevel(skill);
		int attribute = attributeCalculator.calculate(character, skill.getControllingAttribute());

		return attribute + skill.getDifficulty().getStartLevel() + relativeLevel;
	}
}
