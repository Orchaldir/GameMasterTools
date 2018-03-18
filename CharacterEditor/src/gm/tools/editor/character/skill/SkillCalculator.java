package gm.tools.editor.character.skill;

import gm.tools.editor.character.Character;
import gm.tools.editor.character.characteristic.PerceptionCalculator;
import gm.tools.editor.character.characteristic.WillCalculator;

import java.util.HashMap;
import java.util.Map;

public class SkillCalculator {
	private final PerceptionCalculator perceptionCalculator;
	private final WillCalculator willCalculator;

	public SkillCalculator(PerceptionCalculator perceptionCalculator, WillCalculator willCalculator) {
		this.perceptionCalculator = perceptionCalculator;
		this.willCalculator = willCalculator;
	}

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
		int attribute = getAttribute(character, skill);

		return attribute + skill.getDifficulty().getStartLevel() + relativeLevel;
	}

	private int getAttribute(Character character, Skill skill) {
		switch (skill.getControllingAttribute()) {
			case DEXTERITY:
				return character.getDexterity();
			case HEALTH:
				return character.getHealth();
			case INTELLIGENCE:
				return character.getIntelligence();
			case PERCEPTION:
				return perceptionCalculator.calculate(character);
			case STRENGTH:
				return character.getStrength();
			case WILL:
				return willCalculator.calculate(character);
		}

		throw new IllegalArgumentException(String.format("Controlling attribute %s of skill %s is not an attribute!", skill.getControllingAttribute(), skill.getName()));
	}
}
