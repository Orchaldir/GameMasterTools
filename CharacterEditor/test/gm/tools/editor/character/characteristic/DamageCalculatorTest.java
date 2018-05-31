package gm.tools.editor.character.characteristic;

import gm.tools.editor.character.CharacterTemplate;
import gm.tools.editor.character.CharacterTemplateBuilder;
import gm.tools.editor.character.attack.Damage;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DamageCalculatorTest {
	private CharacterTemplate template0 = new CharacterTemplateBuilder("test1").createCharacterTemplate();
	private CharacterTemplate template1 = new CharacterTemplateBuilder("test2").setStrength(12).createCharacterTemplate();
	private CharacterTemplate template2 = new CharacterTemplateBuilder("test3").setStrength(15).createCharacterTemplate();
	private CharacterTemplate template3 = new CharacterTemplateBuilder("test4").setStrength(16).createCharacterTemplate();
	private DamageCalculator calculator;

	@Before
	public void setUp() throws Exception {
		calculator = new DamageCalculator(new AttributeCalculator());
	}

	private void assertThrustDamage(int strength, int dice, int modifier) {
		Damage damage = calculator.calculateThrustDamage(strength);
		assertEquals(dice, damage.getDice());
		assertEquals(modifier, damage.getModifier());
	}

	@Test
	public void testCalculateThrustDamage() {
		assertThrustDamage(1, 1, -6);
		assertThrustDamage(2, 1, -6);
		assertThrustDamage(3, 1, -5);
		assertThrustDamage(4, 1, -5);
		assertThrustDamage(5, 1, -4);
		assertThrustDamage(6, 1, -4);
		assertThrustDamage(7, 1, -3);
		assertThrustDamage(8, 1, -3);
		assertThrustDamage(9, 1, -2);
		assertThrustDamage(10, 1, -2);
		assertThrustDamage(11, 1, -1);
		assertThrustDamage(12, 1, -1);
		assertThrustDamage(13, 1, 0);
		assertThrustDamage(14, 1, 0);
		assertThrustDamage(15, 1, 1);
		assertThrustDamage(16, 1, 1);
		assertThrustDamage(17, 1, 2);
		assertThrustDamage(18, 1, 2);
		assertThrustDamage(19, 2, -1);
		assertThrustDamage(20, 2, -1);
		assertThrustDamage(21, 2, 0);
	}

	private void assertSwingDamage(int strength, int dice, int modifier) {
		Damage damage = calculator.calculateSwingDamage(strength);
		assertEquals(dice, damage.getDice());
		assertEquals(modifier, damage.getModifier());
	}

	@Test
	public void testCalculateSwingDamage() {
		assertSwingDamage(1, 1, -5);
		assertSwingDamage(2, 1, -5);
		assertSwingDamage(3, 1, -4);
		assertSwingDamage(4, 1, -4);
		assertSwingDamage(5, 1, -3);
		assertSwingDamage(6, 1, -3);
		assertSwingDamage(7, 1, -2);
		assertSwingDamage(8, 1, -2);
		assertSwingDamage(9, 1, -1);
		assertSwingDamage(10, 1, 0);
		assertSwingDamage(11, 1, 1);
		assertSwingDamage(12, 1, 2);
		assertSwingDamage(13, 2, -1);
		assertSwingDamage(14, 2, 0);
		assertSwingDamage(15, 2, 1);
		assertSwingDamage(16, 2, 2);
		assertSwingDamage(17, 3, -1);
		assertSwingDamage(18, 3, 0);
		assertSwingDamage(19, 3, 1);
		assertSwingDamage(20, 3, 2);
		assertSwingDamage(21, 4, -1);
	}
}