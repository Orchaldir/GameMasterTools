package gm.tools.editor.character.attack;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ParryTest {

	private final static Parry PARRY0 = new Parry(Parry.ParryType.NORMAL, -1);
	private final static Parry PARRY1 = new Parry(Parry.ParryType.FENCING, 2);
	private final static Parry PARRY2 = new Parry(Parry.ParryType.UNBALANCED, 0);
	private final static Parry PARRY3 = new Parry(Parry.ParryType.NO_PARRY);

	@Test
	public void testToString() {
		assertEquals("-1", PARRY0.toString());
		assertEquals("2F", PARRY1.toString());
		assertEquals("0U", PARRY2.toString());
		assertEquals("No", PARRY3.toString());
	}

	@Test
	public void testFromStringUnbalanced() {
		Parry parry = Parry.fromString("-1U").get();
		assertEquals(-1, parry.getModifier());
		assertEquals(Parry.ParryType.UNBALANCED, parry.getType());
	}

	@Test
	public void testFromStringFencing() {
		Parry parry = Parry.fromString("1F").get();
		assertEquals(1, parry.getModifier());
		assertEquals(Parry.ParryType.FENCING, parry.getType());
	}

	@Test
	public void testFromStringNo() {
		Parry parry = Parry.fromString("No").get();
		assertEquals(0, parry.getModifier());
		assertEquals(Parry.ParryType.NO_PARRY, parry.getType());
	}

	@Test
	public void testFromStringNormal() {
		Parry parry = Parry.fromString("3").get();
		assertEquals(3, parry.getModifier());
		assertEquals(Parry.ParryType.NORMAL, parry.getType());
	}
}