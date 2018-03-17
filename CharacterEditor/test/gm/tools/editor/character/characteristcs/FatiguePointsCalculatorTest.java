package gm.tools.editor.character.characteristcs;

import gm.tools.editor.character.CharacterTemplate;
import gm.tools.editor.character.CharacterTemplateBuilder;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FatiguePointsCalculatorTest {
	private CharacterTemplate template0 = new CharacterTemplateBuilder("test1").setHealth(16).setFatiguePointsModifier(3).createCharacterTemplate();
	private FatiguePointsCalculator calculator;

	@Before
	public void setUp() throws Exception {
		calculator = new FatiguePointsCalculator();
	}

	@Test
	public void calculate() {
		assertEquals(19, calculator.calculate(template0));
	}
}