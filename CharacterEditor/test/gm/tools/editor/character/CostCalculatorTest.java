package gm.tools.editor.character;

import gm.tools.editor.character.characteristic.Characteristic;
import gm.tools.editor.character.skill.Difficulty;
import gm.tools.editor.character.skill.Skill;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CostCalculatorTest {

	private Skill skill0 = new Skill("skill0", Characteristic.DEXTERITY, Difficulty.VERY_HARD);
	private Skill skill1 = new Skill("skill1", Characteristic.INTELLIGENCE, Difficulty.VERY_HARD);
	private CharacterTemplate template0 = new CharacterTemplateBuilder("test1").createCharacterTemplate();
	private CharacterTemplate template1 = new CharacterTemplateBuilder("test2").setStrength(11).setDexterity(12).setIntelligence(13).setHealth(14).
			setHitPointsModifier(-3).setWillModifier(1).setPerceptionModifier(-2).setFatiguePointsModifier(-1).createCharacterTemplate();
	private CharacterTemplate template2 = new CharacterTemplateBuilder("test3").setStrength(11).setBasicSpeedModifier(-3).setBasicMoveModifier(-1).createCharacterTemplate();
	private CharacterTemplate template3;
	private CostCalculator costCalculator;

	@Before
	public void setUp() throws Exception {
		costCalculator = new CostCalculator();

		CharacterTemplateBuilder builder = new CharacterTemplateBuilder("test");
		builder.setStrength(11);
		builder.addSkill(skill0, 1);
		builder.addSkill(skill1, 2);

		template3 = builder.createCharacterTemplate();
	}

	// strength & size

	@Test
	public void testLargeCharacterWithIncreasedStrength() {
		CharacterTemplate template = new CharacterTemplateBuilder("test").setStrength(20).setSizeModifier(5).createCharacterTemplate();
		assertEquals(50, costCalculator.calculate(template));
	}

	@Test
	public void testLargeCharacterWithDecreasedStrength() {
		CharacterTemplate template = new CharacterTemplateBuilder("test").setStrength(5).setSizeModifier(5).createCharacterTemplate();
		assertEquals(-50, costCalculator.calculate(template));
	}

	@Test
	public void testSmallCharacterWithIncreasedStrength() {
		CharacterTemplate template = new CharacterTemplateBuilder("test").setStrength(20).setSizeModifier(-5).createCharacterTemplate();
		assertEquals(100, costCalculator.calculate(template));
	}

	@Test
	public void testSmallCharacterWithDecreasedStrength() {
		CharacterTemplate template = new CharacterTemplateBuilder("test").setStrength(5).setSizeModifier(-5).createCharacterTemplate();
		assertEquals(-50, costCalculator.calculate(template));
	}

	// hit points & size

	@Test
	public void testLargeCharacterWithIncreasedHitPoints() {
		CharacterTemplate template = new CharacterTemplateBuilder("test").setHitPointsModifier(20).setSizeModifier(5).createCharacterTemplate();
		assertEquals(20, costCalculator.calculate(template));
	}

	@Test
	public void testLargeCharacterWithDecreasedHitPoints() {
		CharacterTemplate template = new CharacterTemplateBuilder("test").setHitPointsModifier(-10).setSizeModifier(5).createCharacterTemplate();
		assertEquals(-20, costCalculator.calculate(template));
	}

	@Test
	public void testSmallCharacterWithIncreasedHitPoints() {
		CharacterTemplate template = new CharacterTemplateBuilder("test").setHitPointsModifier(20).setSizeModifier(-5).createCharacterTemplate();
		assertEquals(40, costCalculator.calculate(template));
	}

	@Test
	public void testSmallCharacterWithDecreasedHitPoints() {
		CharacterTemplate template = new CharacterTemplateBuilder("test").setHitPointsModifier(-10).setSizeModifier(-5).createCharacterTemplate();
		assertEquals(-20, costCalculator.calculate(template));
	}

	// skill

	@Test
	public void testCalculateCostOfSkill() {
		assertEquals(1, costCalculator.calculateCostOfSkill(1));
		assertEquals(2, costCalculator.calculateCostOfSkill(2));
		assertEquals(4, costCalculator.calculateCostOfSkill(3));
		assertEquals(8, costCalculator.calculateCostOfSkill(4));
		assertEquals(12, costCalculator.calculateCostOfSkill(5));
		assertEquals(16, costCalculator.calculateCostOfSkill(6));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCalculateCostOfSkillWithZeroLevel() {
		costCalculator.calculateCostOfSkill(0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCalculateCostOfSkillWithNegativeLevel() {
		costCalculator.calculateCostOfSkill(-1);
	}

	@Test
	public void testCalculateCostOfSkills() {
		assertEquals(3, costCalculator.calculateCostOfSkills(template3));
	}

	//

	@Test
	public void calculateCostOfAttributes() {
		assertEquals(0, costCalculator.calculateCostOfAttributes(template0));
		assertEquals(150, costCalculator.calculateCostOfAttributes(template1));
		assertEquals(10, costCalculator.calculateCostOfAttributes(template2));
	}

	@Test
	public void calculateCostOfSecondaryCharacteristics() {
		assertEquals(0, costCalculator.calculateCostOfSecondaryCharacteristics(template0));
		assertEquals(-14, costCalculator.calculateCostOfSecondaryCharacteristics(template1));
		assertEquals(-20, costCalculator.calculateCostOfSecondaryCharacteristics(template2));
	}

	@Test
	public void calculateCharacterPoints() {
		assertEquals(0, costCalculator.calculate(template0));
		assertEquals(136, costCalculator.calculate(template1));
		assertEquals(-10, costCalculator.calculate(template2));
		assertEquals(13, costCalculator.calculate(template3));
	}
}