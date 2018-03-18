package gm.tools.editor.character;

import gm.tools.editor.character.skill.Skill;

import java.util.Set;

public interface Character {
	// attributes
	int getStrength();
	int getDexterity();
	int getIntelligence();
	int getHealth();

	// secondary characteristics
	int getHitPointsModifier();
	int getWillModifier();
	int getPerceptionModifier();
	int getFatiguePointsModifier();
	int getBasicSpeedModifier();
	int getBasicMoveModifier();
	int getSizeModifier();

	// skills
	Set<Skill> getSkills();
	int getRelativeSkillLevel(Skill skill);
}
