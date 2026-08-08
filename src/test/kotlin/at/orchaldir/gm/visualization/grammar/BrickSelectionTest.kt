package at.orchaldir.gm.visualization.grammar

import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.visualization.*
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.map.assertTilemap
import at.orchaldir.gm.visualization.grammar.brick.BrickStart
import at.orchaldir.gm.visualization.grammar.brick.BrickTile
import at.orchaldir.gm.visualization.grammar.brick.OccupiedTile
import at.orchaldir.gm.visualization.grammar.brick.createBrickPattern
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class BrickSelectionTest {

    private val occupied = OccupiedTile
    private val blue = RectangularShapeGrammar(Color.Blue)
    private val red = RectangularShapeGrammar(Color.Red)
    private val yellow = RectangularShapeGrammar(Color.Yellow)

    @Nested
    inner class AlternateRowsTest {

        private val selection = AlternateRows(listOf(blue, yellow))

        @Test
        fun `Test BasketWeave`() {
            val expected = createBasketWeave(
                blue,
                yellow,
                blue,
                blue,
                yellow,
                blue,
            )

            testBasketWeave(selection, expected)
        }

        @Test
        fun `Test BasketWeaveSingle`() {
            val expected = createBasketWeaveSingle(
                blue,
                blue,
                blue,
                yellow,
                blue,
                blue,
                yellow,
                blue,
            )

            testBasketWeaveSingle(selection, expected)
        }

        @Test
        fun `Test Grid`() {
            val expected = createGrid(
                blue,
                blue,
                blue,
                yellow,
                yellow,
                yellow,
                blue,
                blue,
                blue,
            )

            testGrid(selection, expected)
        }

        @Test
        fun `Test Herringbone`() {
            val expected = createHerringbone(
                blue,
                yellow,
                blue,
                blue,
                yellow,
                yellow,
                blue,
            )

            testHerringbone(selection, expected)
        }

        @Test
        fun `Test Running`() {
            val expected = createRunning(
                blue,
                blue,
                yellow,
                yellow,
                yellow,
                blue,
                blue,
            )

            testRunning(selection, expected)
        }

        @Test
        fun `Test Stack`() {
            val expected = createStack(
                blue,
                blue,
                yellow,
                yellow,
                blue,
                blue,
            )

            testStack(selection, expected)
        }
    }

    @Nested
    inner class HorizontalAndVerticalBricksTest {

        private val selection = HorizontalAndVerticalBricks(blue, yellow)

        @Test
        fun `Test BasketWeave`() {
            val expected = createBasketWeave(
                blue,
                blue,
                blue,
                yellow,
                yellow,
                yellow,
            )

            testBasketWeave(selection, expected)
        }

        @Test
        fun `Test BasketWeaveSingle`() {
            val expected = createBasketWeaveSingle(
                blue,
                blue,
                yellow,
                yellow,
                yellow,
                yellow,
                yellow,
                yellow,
            )

            testBasketWeaveSingle(selection, expected)
        }

        @Test
        fun `Test Grid`() {
            val expected = createGrid(
                blue,
                blue,
                blue,
                blue,
                blue,
                blue,
                blue,
                blue,
                blue,
            )

            testGrid(selection, expected)
        }

        @Test
        fun `Test Herringbone`() {
            val expected = createHerringbone(
                blue,
                blue,
                blue,
                yellow,
                yellow,
                yellow,
                yellow,
            )

            testHerringbone(selection, expected)
        }

        @Test
        fun `Test Pinwheel`() {
            testPinwheel(BrickPattern.Pinwheel)
        }

        @Test
        fun `Test PinwheelSplit`() {
            testPinwheel(BrickPattern.PinwheelSplit)
        }

        @Test
        fun `Test PinwheelWithBigCenter`() {
            testPinwheel(BrickPattern.PinwheelWithBigCenter)
        }

        @Test
        fun `Test Running`() {
            val expected = createRunning(
                blue,
                blue,
                blue,
                blue,
                blue,
                blue,
                blue,
            )

            testRunning(selection, expected)
        }

        @Test
        fun `Test Stack`() {
            val expected = createStack(
                blue,
                blue,
                blue,
                blue,
                blue,
                blue,
            )

            testStack(selection, expected)
        }

        private fun testPinwheel(pattern: BrickPattern) {
            val expected = createPinwheel(
                blue,
                blue,
                yellow,
                yellow,
                blue,
            )

            testPinwheel(pattern, selection, expected)
        }
    }

    @Nested
    inner class BrickSelectionWithCenterTest {

        private val selection = BrickSelectionWithCenter(red, HorizontalAndVerticalBricks(blue, yellow))


        @Test
        fun `Test Pinwheel`() {
            testPinwheel(BrickPattern.Pinwheel)
        }

        @Test
        fun `Test PinwheelSplit`() {
            testPinwheel(BrickPattern.PinwheelSplit)
        }

        @Test
        fun `Test PinwheelWithBigCenter`() {
            testPinwheel(BrickPattern.PinwheelWithBigCenter)
        }


        private fun testPinwheel(pattern: BrickPattern) {
            val expected = createPinwheel(
                red,
                blue,
                yellow,
                yellow,
                blue,
            )

            testPinwheel(pattern, selection, expected)
        }
    }

    private fun createH(grammar: ShapeGrammar, n: Int) = BrickStart(grammar, MapSize2d(n, 1))
    private fun createH2(grammar: ShapeGrammar) = createH(grammar, 2)
    private fun createH3(grammar: ShapeGrammar) = createH(grammar, 3)
    private fun createV(grammar: ShapeGrammar, n: Int) = BrickStart(grammar, MapSize2d(1, n))
    private fun createV2(grammar: ShapeGrammar) = createV(grammar, 2)
    private fun createV3(grammar: ShapeGrammar) = createV(grammar, 3)

    private fun createBasketWeave(
        h0: ShapeGrammar,
        h1: ShapeGrammar,
        h2: ShapeGrammar,
        v0: ShapeGrammar,
        v1: ShapeGrammar,
        v2: ShapeGrammar,
    ): List<List<BrickTile>> {
        val line0 = listOf(createH3(h0), occupied, occupied, createV3(v0), createV3(v1), createV3(v2))
        val line1 = listOf(createH3(h1), occupied, occupied, occupied, occupied, occupied)
        val line2 = listOf(createH3(h2), occupied, occupied, occupied, occupied, occupied)

        return listOf(line0, line1, line2)
    }

    private fun createBasketWeaveSingle(
        h0: ShapeGrammar,
        h1: ShapeGrammar,
        v0: ShapeGrammar,
        v1: ShapeGrammar,
        v2: ShapeGrammar,
        v3: ShapeGrammar,
        v4: ShapeGrammar,
        v5: ShapeGrammar,
    ): List<List<BrickTile>> {
        val line0 = listOf(createV3(v0), createV3(v1), createV3(v2), createH3(h1), occupied, occupied)
        val line1 = listOf(occupied, occupied, occupied, createV3(v3), createV3(v4), createV3(v5))
        val line2 = listOf(occupied, occupied, occupied, occupied, occupied, occupied)
        val line3 = listOf(createH3(h0), occupied, occupied, occupied, occupied, occupied)

        return listOf(line0, line1, line2, line3)
    }

    private fun createGrid(
        b00: ShapeGrammar,
        b01: ShapeGrammar,
        b02: ShapeGrammar,
        b10: ShapeGrammar,
        b11: ShapeGrammar,
        b12: ShapeGrammar,
        b20: ShapeGrammar,
        b21: ShapeGrammar,
        b22: ShapeGrammar,
    ): List<List<BrickTile>> {
        val line0 = listOf(BrickStart(b00), BrickStart(b01), BrickStart(b02))
        val line1 = listOf(BrickStart(b10), BrickStart(b11), BrickStart(b12))
        val line2 = listOf(BrickStart(b20), BrickStart(b21), BrickStart(b22))

        return listOf(line0, line1, line2)
    }

    private fun createHerringbone(
        h0: ShapeGrammar,
        h1: ShapeGrammar,
        h2: ShapeGrammar,
        v0: ShapeGrammar,
        v1: ShapeGrammar,
        v2: ShapeGrammar,
        v3: ShapeGrammar,
    ): List<List<BrickTile>> {
        val line0 = listOf(createH3(h0), occupied, occupied, BrickStart(v2), createV2(v3))
        val line1 = listOf(createV2(v0), createH3(h1), occupied, occupied, occupied)
        val line2 = listOf(occupied, BrickStart(v1), createH3(h2), occupied, occupied)

        return listOf(line0, line1, line2)
    }

    private fun createPinwheel(
        center: ShapeGrammar,
        bottom: ShapeGrammar,
        left: ShapeGrammar,
        right: ShapeGrammar,
        top: ShapeGrammar,
    ): List<List<BrickTile>> {
        val line0 = listOf(createV2(left), createH2(top), occupied)
        val line1 = listOf(occupied, BrickStart(center), createV2(right))
        val line2 = listOf(createH2(bottom), occupied, occupied)

        return listOf(line0, line1, line2)
    }

    private fun createRunning(
        b00: ShapeGrammar,
        b01: ShapeGrammar,
        b10: ShapeGrammar,
        b11: ShapeGrammar,
        b12: ShapeGrammar,
        b20: ShapeGrammar,
        b21: ShapeGrammar,
    ): List<List<BrickTile>> {
        val line0 = listOf(createH2(b00), occupied, createH2(b01), occupied)
        val line1 = listOf(BrickStart(b10), createH2(b11), occupied, BrickStart(b12))
        val line2 = listOf(createH2(b20), occupied, createH2(b21), occupied)

        return listOf(line0, line1, line2)
    }

    private fun createStack(
        b00: ShapeGrammar,
        b01: ShapeGrammar,
        b10: ShapeGrammar,
        b11: ShapeGrammar,
        b20: ShapeGrammar,
        b21: ShapeGrammar,
    ): List<List<BrickTile>> {
        val line0 = listOf(createH2(b00), occupied, createH2(b01), occupied)
        val line1 = listOf(createH2(b10), occupied, createH2(b11), occupied)
        val line2 = listOf(createH2(b20), occupied, createH2(b21), occupied)

        return listOf(line0, line1, line2)
    }

    private fun testBasketWeave(
        selection: BrickSelection,
        expected: List<List<BrickTile>>,
    ) = test(
        MapSize2d(6, 3),
        BrickPattern.BasketWeave,
        3,
        selection,
        expected,
    )

    private fun testBasketWeaveSingle(
        selection: BrickSelection,
        expected: List<List<BrickTile>>,
    ) = test(
        MapSize2d(6, 4),
        BrickPattern.BasketWeaveSingle,
        3,
        selection,
        expected,
    )

    private fun testGrid(
        selection: BrickSelection,
        expected: List<List<BrickTile>>,
    ) = test(
        MapSize2d(3, 3),
        BrickPattern.Grid,
        1,
        selection,
        expected,
    )

    private fun testHerringbone(
        selection: BrickSelection,
        expected: List<List<BrickTile>>,
    ) = test(
        MapSize2d(5, 3),
        BrickPattern.Herringbone,
        3,
        selection,
        expected,
    )

    private fun testPinwheel(
        pattern: BrickPattern,
        selection: BrickSelection,
        expected: List<List<BrickTile>>,
    ) = test(
        MapSize2d.square(3),
        pattern,
        2,
        selection,
        expected,
    )

    private fun testRunning(
        selection: BrickSelection,
        expected: List<List<BrickTile>>,
    ) = test(
        MapSize2d(4, 3),
        BrickPattern.Running,
        2,
        selection,
        expected,
    )

    private fun testStack(
        selection: BrickSelection,
        expected: List<List<BrickTile>>,
    ) = test(
        MapSize2d(4, 3),
        BrickPattern.Stack,
        2,
        selection,
        expected,
    )

    private fun test(
        size: MapSize2d,
        pattern: BrickPattern,
        length: Int,
        selection: BrickSelection,
        expected: List<List<BrickTile>>,
    ) {
        val grammar = BrickPatternGrammar(
            selection,
            RowsAndColumns(size),
            pattern,
            length,
        )

        val result = createBrickPattern(grammar, Borders(true))

        assertTilemap(result, size, expected)
    }
}