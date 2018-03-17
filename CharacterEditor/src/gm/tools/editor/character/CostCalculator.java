package gm.tools.editor.character;

public class CostCalculator {
	public static final int ATTRIBUTE_DEFAULT_VALUE = 10;

	public static final int STRENGTH_COST = 10;
	public static final int DEXTERITY_COST = 20;
	public static final int INTELLIGENCE_COST = 20;
	public static final int HEALTH_COST = 10;

	public static final int HIT_POINTS_COST = 2;

	public int getAttributeModifier(int value) {
		return value - ATTRIBUTE_DEFAULT_VALUE;
	}

	public int calculateCostOfAttributes(Character charcater) {
		int strengthCost = getAttributeModifier(charcater.getStrength()) * STRENGTH_COST;
		int dexterityCost = getAttributeModifier(charcater.getDexterity()) * DEXTERITY_COST;
		int intelligenceCost = getAttributeModifier(charcater.getIntelligence()) * INTELLIGENCE_COST;
		int healthCost = getAttributeModifier(charcater.getHealth()) * HEALTH_COST;

		return strengthCost + dexterityCost + intelligenceCost + healthCost;
	}

	public int calculate(Character charcater) {
		int attributesCost = calculateCostOfAttributes(charcater);
		int hitPointsCost = charcater.getHitPointsModifier() * HIT_POINTS_COST;

		return attributesCost + hitPointsCost;
	}
}
