package gm.tools.editor.character;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CostCalculatorTest {

	private CharacterTemplate template0 = new CharacterTemplate("test1", 10, 10, 10, 10, 0);
	private CharacterTemplate template1 = new CharacterTemplate("test2", 11, 12, 13, 14, 0);
	private CostCalculator costCalculator;

	@Before
	public void setUp() throws Exception {
		costCalculator = new CostCalculator();
	}

	@Test
	public void calculateCharacterPoints() {
		assertEquals(0, costCalculator.calculate(template0));
		assertEquals(150, costCalculator.calculate(template1));
	}
}