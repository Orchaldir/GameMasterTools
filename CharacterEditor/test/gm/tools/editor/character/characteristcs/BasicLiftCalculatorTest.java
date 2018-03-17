package gm.tools.editor.character.characteristcs;

import gm.tools.editor.character.CharacterTemplate;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BasicLiftCalculatorTest {
	private CharacterTemplate template0 = new CharacterTemplate("test1", 10, 10, 10, 10, 0);
	private CharacterTemplate template1 = new CharacterTemplate("test2", 12, 10, 10, 10, 0);
	private CharacterTemplate template2 = new CharacterTemplate("test3", 15, 10, 10, 10, 0);
	private CharacterTemplate template3 = new CharacterTemplate("test4", 16, 10, 10, 10, 0);
	private BasicLiftCalculator calculator;

	@Before
	public void setUp() throws Exception {
		calculator = new BasicLiftCalculator();
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