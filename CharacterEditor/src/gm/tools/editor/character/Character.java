package gm.tools.editor.character;

import gm.tools.editor.character.characteristic.Appearance;
import gm.tools.editor.character.characteristic.Attribute;
import gm.tools.editor.character.characteristic.Characteristic;
import gm.tools.editor.character.skill.Skill;
import gm.tools.editor.character.trait.Trait;

import java.util.Collection;

public interface Character {
	String getName();

	//
	Appearance getAppearance();

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

	int getCharacteristicModifier(Characteristic characteristic);

	// skills
	Collection<Skill> getSkills();
	int getRelativeSkillLevel(Skill skill);

	// traits
	Collection<Trait> getTraits();
}
