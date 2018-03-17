package gm.tools.editor.character.characteristcs;

import gm.tools.editor.character.CharacterTemplate;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class HitPointsCalculatorTest {
	private CharacterTemplate template0 = new CharacterTemplate("test1", 15, 10, 10, 10, 2);
	private HitPointsCalculator calculator;

	@Before
	public void setUp() throws Exception {
		calculator = new HitPointsCalculator();
	}

	@Test
	public void calculate() {
		assertEquals(17, calculator.calculate(template0));
	}
}