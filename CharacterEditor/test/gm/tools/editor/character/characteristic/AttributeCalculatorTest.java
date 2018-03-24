package gm.tools.editor.character.characteristic;

import gm.tools.editor.character.CharacterTemplate;
import gm.tools.editor.character.CharacterTemplateBuilder;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AttributeCalculatorTest {
	private CharacterTemplate template0 = new CharacterTemplateBuilder("test1").setIntelligence(14).setWillModifier(-2).createCharacterTemplate();
	private AttributeCalculator calculator;

	@Before
	public void setUp() throws Exception {
		calculator = new AttributeCalculator();
	}

	@Test
	public void calculate() {
		//assertEquals(12, calculator.calculate(template0));
	}
}