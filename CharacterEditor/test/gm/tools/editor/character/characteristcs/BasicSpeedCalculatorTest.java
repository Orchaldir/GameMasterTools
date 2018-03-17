package gm.tools.editor.character.characteristcs;

import gm.tools.editor.character.CharacterTemplate;
import gm.tools.editor.character.CharacterTemplateBuilder;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BasicSpeedCalculatorTest {
	private CharacterTemplate template0 = new CharacterTemplateBuilder("test1").createCharacterTemplate();
	private CharacterTemplate template1 = new CharacterTemplateBuilder("test2").setAttributes(11, 12, 13, 14).createCharacterTemplate();
	private CharacterTemplate template2 = new CharacterTemplateBuilder("test3").setBasicSpeedModifier(-1).createCharacterTemplate();
	private CharacterTemplate template3 = new CharacterTemplateBuilder("test4").setDexterity(9).setHealth(12).setBasicSpeedModifier(2).createCharacterTemplate();
	private BasicSpeedCalculator calculator;
	private static final double DELTA = 0.0001;

	@Before
	public void setUp() throws Exception {
		calculator = new BasicSpeedCalculator();
	}

	@Test
	public void testCalculate() {
		assertEquals(5.0, calculator.calculate(template0), DELTA);
		assertEquals(6.5, calculator.calculate(template1), DELTA);
		assertEquals(4.75, calculator.calculate(template2), DELTA);
		assertEquals(5.75, calculator.calculate(template3), DELTA);
	}
}