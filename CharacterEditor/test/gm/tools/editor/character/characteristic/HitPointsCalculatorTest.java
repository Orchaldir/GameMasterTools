package gm.tools.editor.character.characteristic;

import gm.tools.editor.character.CharacterTemplate;
import gm.tools.editor.character.CharacterTemplateBuilder;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class HitPointsCalculatorTest {
	private CharacterTemplate template0 = new CharacterTemplateBuilder("test1").setStrength(15).setHitPointsModifier(2).createCharacterTemplate();
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