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
	public static final int BASIC_SPEED_COST = 5;
	public static final int BASIC_MOVE_COST = 5;

	public int getAttributeModifier(int value) {
		return value - ATTRIBUTE_DEFAULT_VALUE;
	}

	private int modifyCostWithSize(int cost, Character character) {
		if (cost > 0) {
			int sizeModifier = character.getSizeModifier();

			if (sizeModifier > 0) {
				double factor = Math.min(0.80, 0.1 * sizeModifier);
				cost *= 1.0 - factor;
			}
		}

		return cost;
	}

	public int calculateStrengthCost(Character character) {
		int strengthCost = getAttributeModifier(character.getStrength()) * STRENGTH_COST;

		return modifyCostWithSize(strengthCost, character);
	}

	public int calculateHitPointsCost(Character character) {
		int hitPointsCost = character.getHitPointsModifier() * HIT_POINTS_COST;

		return modifyCostWithSize(hitPointsCost, character);
	}

	public int calculateCostOfAttributes(Character character) {
		int strengthCost = calculateStrengthCost(character);
		int dexterityCost = getAttributeModifier(character.getDexterity()) * DEXTERITY_COST;
		int intelligenceCost = getAttributeModifier(character.getIntelligence()) * INTELLIGENCE_COST;
		int healthCost = getAttributeModifier(character.getHealth()) * HEALTH_COST;

		return strengthCost + dexterityCost + intelligenceCost + healthCost;
	}

	public int calculateCostOfSecondaryCharacteristics(Character character) {
		int hitPointsCost = calculateHitPointsCost(character);
		int willCost = character.getWillModifier() * WILL_COST;
		int perceptionCost = character.getPerceptionModifier() * PERCEPTION_COST;
		int fatiguePointsCost = character.getFatiguePointsModifier() * FATIGUE_POINTS_COST;
		int basicSpeedCost = character.getBasicSpeedModifier() * BASIC_SPEED_COST;
		int basicMoveCost = character.getBasicMoveModifier() * BASIC_MOVE_COST;

		return hitPointsCost + willCost + perceptionCost + fatiguePointsCost + basicSpeedCost + basicMoveCost;
	}

	public int calculate(Character character) {
		int attributesCost = calculateCostOfAttributes(character);
		int secondaryCharacteristicsCost = calculateCostOfSecondaryCharacteristics(character);

		return attributesCost + secondaryCharacteristicsCost;
	}
}
