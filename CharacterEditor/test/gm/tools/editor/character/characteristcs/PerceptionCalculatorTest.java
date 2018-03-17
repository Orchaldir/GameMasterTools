package gm.tools.editor.character.characteristcs;

import gm.tools.editor.character.CharacterTemplate;
import gm.tools.editor.character.CharacterTemplateBuilder;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PerceptionCalculatorTest {
	private CharacterTemplate template0 = new CharacterTemplateBuilder("test1").setIntelligence(14).setPerceptionModifier(-3).createCharacterTemplate();
	private PerceptionCalculator calculator;

	@Before
	public void setUp() throws Exception {
		calculator = new PerceptionCalculator();
	}

	@Test
	public void calculate() {
		assertEquals(11, calculator.calculate(template0));
	}
}