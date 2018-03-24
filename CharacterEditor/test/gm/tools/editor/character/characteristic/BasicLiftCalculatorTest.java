package gm.tools.editor.character.characteristic;

import gm.tools.editor.character.CharacterTemplate;
import gm.tools.editor.character.CharacterTemplateBuilder;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BasicLiftCalculatorTest {
	private CharacterTemplate template0 = new CharacterTemplateBuilder("test1").createCharacterTemplate();
	private CharacterTemplate template1 = new CharacterTemplateBuilder("test2").setStrength(12).createCharacterTemplate();
	private CharacterTemplate template2 = new CharacterTemplateBuilder("test3").setStrength(15).createCharacterTemplate();
	private CharacterTemplate template3 = new CharacterTemplateBuilder("test4").setStrength(16).createCharacterTemplate();
	private BasicLiftCalculator calculator;

	@Before
	public void setUp() throws Exception {
		calculator = new BasicLiftCalculator(new AttributeCalculator());
	}

	@Test
	public void testCalculate() {
		assertEquals(10, calculator.calculate(template0));
	}

	@Test
	public void testCalculateRoundDown() {
		assertEquals(14, calculator.calculate(template1));
	}

	@Test
	public void testCalculateRoundUpHalf() {
		assertEquals(23, calculator.calculate(template2));
	}

	@Test
	public void testCalculateRoundUp() {
		assertEquals(26, calculator.calculate(template3));
	}
}