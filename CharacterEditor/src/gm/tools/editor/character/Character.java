package gm.tools.editor.character;

import gm.tools.editor.character.characteristic.Attribute;
import gm.tools.editor.character.skill.Skill;

import java.util.Set;

public interface Character {
	// attributes
	int getDexterityModifier();

	int getHealthModifier();

	int getIntelligenceModifier();

	int getPerceptionModifier();

	int getStrengthModifier();

	int getWillModifier();

	int getAttributeModifier(Attribute attribute);

	// secondary characteristics
	int getHitPointsModifier();
	int getFatiguePointsModifier();
	int getBasicSpeedModifier();
	int getBasicMoveModifier();
	int getSizeModifier();

	// skills
	Set<Skill> getSkills();
	int getRelativeSkillLevel(Skill skill);
}
