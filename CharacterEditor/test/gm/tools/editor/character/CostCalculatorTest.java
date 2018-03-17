package gm.tools.editor.character;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CostCalculatorTest {

	private CharacterTemplate template0 = new CharacterTemplateBuilder("test1").createCharacterTemplate();
	private CharacterTemplate template1 = new CharacterTemplateBuilder("test2").setStrength(11).setDexterity(12).setIntelligence(13).setHealth(14).setHitPointsModifier(-1).createCharacterTemplate();
	private CostCalculator costCalculator;

	@Before
	public void setUp() throws Exception {
		costCalculator = new CostCalculator();
	}

	@Test
	public void calculateCharacterPoints() {
		assertEquals(0, costCalculator.calculate(template0));
		assertEquals(148, costCalculator.calculate(template1));
	}
}