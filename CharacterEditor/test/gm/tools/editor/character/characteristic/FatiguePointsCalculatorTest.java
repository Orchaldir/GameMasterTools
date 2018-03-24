package gm.tools.editor.character.characteristic;

import gm.tools.editor.character.CharacterTemplate;
import gm.tools.editor.character.CharacterTemplateBuilder;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FatiguePointsCalculatorTest {
	private CharacterTemplate template0 = new CharacterTemplateBuilder("test1").setHealth(6).setFatiguePointsModifier(3).createCharacterTemplate();
	private FatiguePointsCalculator calculator;

	@Before
	public void setUp() throws Exception {
		calculator = new FatiguePointsCalculator(new AttributeCalculator());
	}

	@Test
	public void calculate() {
		assertEquals(19, calculator.calculate(template0));
	}
}