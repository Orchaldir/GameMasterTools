package gm.tools.editor.character;

public class CostCalculator {
	public static final int ATTRIBUTE_DEFAULT_VALUE = 10;

	// attributes
	public static final int STRENGTH_COST = 10;
	public static final int DEXTERITY_COST = 20;
	public static final int INTELLIGENCE_COST = 20;
	public static final int HEALTH_COST = 10;

	// secondary characteristics
	public static final int HIT_POINTS_COST = 2;
	public static final int WILL_COST = 5;
	public static final int PERCEPTION_COST = 5;
	public static final int FATIGUE_POINTS_COST = 3;

	public int getAttributeModifier(int value) {
		return value - ATTRIBUTE_DEFAULT_VALUE;
	}

	public int calculateCostOfAttributes(Character character) {
		int strengthCost = getAttributeModifier(character.getStrength()) * STRENGTH_COST;
		int dexterityCost = getAttributeModifier(character.getDexterity()) * DEXTERITY_COST;
		int intelligenceCost = getAttributeModifier(character.getIntelligence()) * INTELLIGENCE_COST;
		int healthCost = getAttributeModifier(character.getHealth()) * HEALTH_COST;

		return strengthCost + dexterityCost + intelligenceCost + healthCost;
	}

	public int calculateCostOfSecondaryCharacteristics(Character character) {
		int hitPointsCost = character.getHitPointsModifier() * HIT_POINTS_COST;
		int willCost = character.getWillModifier() * WILL_COST;
		int perceptionCost = character.getPerceptionModifier() * PERCEPTION_COST;
		int fatiguePointsCost = character.getFatiguePointsModifier() * FATIGUE_POINTS_COST;

		return hitPointsCost + willCost + perceptionCost + fatiguePointsCost;
	}

	public int calculate(Character character) {
		int attributesCost = calculateCostOfAttributes(character);
		int secondaryCharacteristicsCost = calculateCostOfSecondaryCharacteristics(character);

		return attributesCost + secondaryCharacteristicsCost;
	}
}
