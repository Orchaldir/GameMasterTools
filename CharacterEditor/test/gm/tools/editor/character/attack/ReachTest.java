package gm.tools.editor.character.attack;

import org.junit.Test;

import static org.junit.Assert.*;

public class ReachTest {

	private final static Reach REACH0 = new Reach(2);
	private final static Reach REACH1 = new Reach(1, 2, false);
	private final static Reach REACH2 = new Reach(2, 4, true);

	@Test
	public void testIsInReach() {
		assertFalse(REACH2.isInReach(0));
		assertFalse(REACH2.isInReach(1));
		assertTrue(REACH2.isInReach(2));
		assertTrue(REACH2.isInReach(3));
		assertTrue(REACH2.isInReach(4));
		assertFalse(REACH2.isInReach(5));
		assertFalse(REACH2.isInReach(6));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIsInReachWithNegativeDistance() {
		REACH2.isInReach(-1);
	}

	@Test
	public void testToString() {
		assertEquals("2", REACH0.toString());
		assertEquals("1-2", REACH1.toString());
		assertEquals("2-4*", REACH2.toString());
	}

	@Test
	public void testFromEmptyString() {
		assertFalse(Reach.fromString("").isPresent());
	}

	@Test
	public void testFromStringSimple() {
		Reach reach = Reach.fromString("2").get();
		assertEquals(2, reach.getMinReach());
		assertEquals(2, reach.getMaxReach());
		assertFalse(reach.isAwkward());
	}

	@Test
	public void testFromStringRange() {
		Reach reach = Reach.fromString("1-3").get();
		assertEquals(1, reach.getMinReach());
		assertEquals(3, reach.getMaxReach());
		assertFalse(reach.isAwkward());
	}

	@Test
	public void testFromStringWithIsAwkward() {
		Reach reach = Reach.fromString("3-9*").get();
		assertEquals(3, reach.getMinReach());
		assertEquals(9, reach.getMaxReach());
		assertTrue(reach.isAwkward());
	}
}