package gm.tools.editor.character.characteristic;

import gm.tools.editor.character.CharacterTemplate;
import gm.tools.editor.character.CharacterTemplateBuilder;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BasicMoveCalculatorTest {
	private CharacterTemplate template0 = new CharacterTemplateBuilder("test1").setBasicMoveModifier(1).createCharacterTemplate();
	private CharacterTemplate template1 = new CharacterTemplateBuilder("test2").setAttributes(1, 2, 3, 4).setBasicMoveModifier(-2).createCharacterTemplate();
	private CharacterTemplate template2 = new CharacterTemplateBuilder("test3").setBasicSpeedModifier(-1).createCharacterTemplate();
	private CharacterTemplate template3 = new CharacterTemplateBuilder("test4").setDexterity(-1).setHealth(2).setBasicSpeedModifier(2).setBasicMoveModifier(3).createCharacterTemplate();
	private BasicMoveCalculator calculator;

	@Before
	public void setUp() throws Exception {
		calculator = new BasicMoveCalculator(new BasicSpeedCalculator(new AttributeCalculator()));
	}

	@Test
	public void testCalculate() {
		assertEquals(6, calculator.calculate(template0));
		assertEquals(4, calculator.calculate(template1));
		assertEquals(4, calculator.calculate(template2));
		assertEquals(8, calculator.calculate(template3));
	}
}