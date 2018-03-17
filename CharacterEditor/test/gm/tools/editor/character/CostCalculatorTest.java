package gm.tools.editor.character;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CostCalculatorTest {

	private CharacterTemplate template0 = new CharacterTemplateBuilder("test1").createCharacterTemplate();
	private CharacterTemplate template1 = new CharacterTemplateBuilder("test2").setStrength(11).setDexterity(12).setIntelligence(13).setHealth(14).setHitPointsModifier(-3).setWillModifier(1).createCharacterTemplate();
	private CostCalculator costCalculator;

	@Before
	public void setUp() throws Exception {
		costCalculator = new CostCalculator();
	}

	@Test
	public void calculateCostOfAttributes() {
		assertEquals(0, costCalculator.calculateCostOfAttributes(template0));
		assertEquals(150, costCalculator.calculateCostOfAttributes(template1));
	}

	@Test
	public void calculateCostOfSecondaryCharacteristics() {
		assertEquals(0, costCalculator.calculateCostOfSecondaryCharacteristics(template0));
		assertEquals(-1, costCalculator.calculateCostOfSecondaryCharacteristics(template1));
	}

	@Test
	public void calculateCharacterPoints() {
		assertEquals(0, costCalculator.calculate(template0));
		assertEquals(149, costCalculator.calculate(template1));
	}
}