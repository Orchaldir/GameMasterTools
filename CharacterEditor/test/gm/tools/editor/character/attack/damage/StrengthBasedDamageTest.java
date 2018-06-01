package gm.tools.editor.character.attack.damage;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StrengthBasedDamageTest {

	private static final StrengthBasedDamage DAMAGE0 = new StrengthBasedDamage(StrengthBasedType.SWINGING, 0, DamageType.CUTTING);
	private static final StrengthBasedDamage DAMAGE1 = new StrengthBasedDamage(StrengthBasedType.SWINGING, -1, DamageType.PIERCING);
	private static final StrengthBasedDamage DAMAGE2 = new StrengthBasedDamage(StrengthBasedType.THRUSTING, 2, DamageType.IMPALING);

	@Test
	public void testToString() {
		assertEquals("sw cut", DAMAGE0.toString());
		assertEquals("sw-1 pi", DAMAGE1.toString());
		assertEquals("thr+2 imp", DAMAGE2.toString());
	}

	@Test
	public void testFromStringWithoutModifier() {
		StrengthBasedDamage damage = StrengthBasedDamage.fromString("thr cut").get();

		assertEquals(StrengthBasedType.THRUSTING, damage.getStrengthBasedType());
		assertEquals(0, damage.getStrengthBasedModifier());
		assertEquals(DamageType.CUTTING, damage.getType());
	}

	@Test
	public void testFromStringWithModifier() {
		StrengthBasedDamage damage = StrengthBasedDamage.fromString("sw+2 imp").get();

		assertEquals(StrengthBasedType.SWINGING, damage.getStrengthBasedType());
		assertEquals(2, damage.getStrengthBasedModifier());
		assertEquals(DamageType.IMPALING, damage.getType());
	}
}