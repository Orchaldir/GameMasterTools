package gm.tools.editor.character;

import gm.tools.editor.character.characteristic.Attribute;
import gm.tools.editor.character.skill.Skill;
import gm.tools.editor.character.trait.Trait;

public class CostCalculator {
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
		int strengthCost = character.getStrengthModifier() * STRENGTH_COST;

		return modifyCostWithSize(strengthCost, character);
	}

	public int calculateHitPointsCost(Character character) {
		int hitPointsCost = character.getHitPointsModifier() * HIT_POINTS_COST;

		return modifyCostWithSize(hitPointsCost, character);
	}

	public int calculateCostOfAttributes(Character character) {
		int strengthCost = calculateStrengthCost(character);
		int dexterityCost = character.getDexterityModifier() * DEXTERITY_COST;
		int intelligenceCost = character.getIntelligenceModifier() * INTELLIGENCE_COST;
		int healthCost = character.getHealthModifier() * HEALTH_COST;

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

	public int calculateCostOfSkill(int level) {
		int cost = 0;

		if (level < 1) {
			throw new IllegalArgumentException(String.format("Relative level %d of skill is too low!", level));
		} else if (level == 1) {
			return 1;
		} else if (level == 2) {
			return 2;
		} else if (level == 3) {
			return 4;
		} else {
			return (level - 2) * 4;
		}
	}

	public int calculateCostOfSkills(Character character) {
		int cost = 0;

		for (Skill skill : character.getSkills()) {
			int level = character.getRelativeSkillLevel(skill);
			cost += calculateCostOfSkill(level);
		}

		return cost;
	}

	public int calculateCostOfTraits(Character character) {
		int cost = 0;

		for (Trait trait : character.getTraits()) {
			cost += trait.getCost();
		}

		return cost;
	}

	public int calculate(Character character) {
		int attributesCost = calculateCostOfAttributes(character);
		int secondaryCharacteristicsCost = calculateCostOfSecondaryCharacteristics(character);
		int skillCost = calculateCostOfSkills(character);
		int traitCost = calculateCostOfTraits(character);

		return attributesCost + secondaryCharacteristicsCost + skillCost + traitCost;
	}
}
