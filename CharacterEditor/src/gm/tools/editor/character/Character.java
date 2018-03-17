package gm.tools.editor.character;

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
}
