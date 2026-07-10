package at.orchaldir.gm.visualization.grammar

import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.visualization.AlternateRows
import at.orchaldir.gm.core.model.visualization.BrickPattern
import at.orchaldir.gm.core.model.visualization.BrickPatternGrammar
import at.orchaldir.gm.core.model.visualization.BrickSelection
import at.orchaldir.gm.core.model.visualization.HorizontalAndVerticalBricks
import at.orchaldir.gm.core.model.visualization.RectangularShapeGrammar
import at.orchaldir.gm.core.model.visualization.RowsAndColumns
import at.orchaldir.gm.core.model.visualization.ShapeGrammar
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.map.assertTilemap
import at.orchaldir.gm.visualization.grammar.brick.Brick
import at.orchaldir.gm.visualization.grammar.brick.createBrickPattern
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class BrickSelectionTest {

    private val gray = RectangularShapeGrammar(Color.Gray)
    private val blue = RectangularShapeGrammar(Color.Blue)
    private val yellow = RectangularShapeGrammar(Color.Yellow)
    private val singleBrick = Brick(gray, MapSize2d(1, 1))
    private val brickW2 = Brick(gray, MapSize2d(2, 1))
    private val brickW3 = Brick(gray, MapSize2d(3, 1))
    private val brickH2 = Brick(gray, MapSize2d(1, 2))
    private val brickH3 = Brick(gray, MapSize2d(1, 3))

    private val line_3_ExW2 = listOf(null, brickW2, null)
    private val line_3_SxW2 = listOf(singleBrick, brickW2, null)
    private val line_3_W2xS = listOf(brickW2, null, singleBrick)
    private val line_3_W2xW2 = listOf(brickW2, null, brickW2)
    private val line_4_SxW2xS = listOf(singleBrick, brickW2, null, singleBrick)
    private val line_4_W2xW2 = listOf(brickW2, null, brickW2, null)
    private val line_4_W2xH3xH3 = listOf(brickW2, null, brickH3, brickH3)
    private val line_4_ExExH3xH3 = listOf(null, null, brickH3, brickH3)
    private val line_4_H3xH3xH3xS = listOf(brickH3, brickH3, brickH3, singleBrick)
    private val line_4_H3xH3xH3xW3 = listOf(brickH3, brickH3, brickH3, brickW3)
    private val line_4_H3xH3xExE = listOf(brickH3, brickH3, null, null)
    private val line_4_ExExExH3 = listOf(null, null, null, brickH3)
    private val line_4_ExExExE = listOf(null, null, null, null)
    private val line_4_ExExW2 = listOf(null, null, brickW2, null)
    private val line_4_W3xE = listOf(brickW3, null, null, null)
    private val line_5_ExExExW2 = listOf(null, null, null, brickW2, null)
    private val line_5_ExExW2xE = listOf(null, null, brickW2, null, null)
    private val line_5_ExH2xH2xW2 = listOf(null, brickH2, brickH2, brickW2, null)
    private val line_5_ExW2xExE = listOf(null, brickW2, null, null, null)
    private val line_5_H2xH2xW2xH2 = listOf(brickH2, brickH2, brickW2, null, brickH2)
    private val line_5_H2xW2xH2xH2 = listOf(brickH2, brickW2, null, brickH2, brickH2)
    private val line_5_SxExExW2 = listOf(singleBrick, null, null, brickW2, null)
    private val line_5_SxH2xH2xW2 = listOf(singleBrick, brickH2, brickH2, brickW2, null)
    private val line_5_W2xExE = listOf(brickW2, null, null, null)
    private val line_5_W2xExExS = line_5_W2xExE + singleBrick
    private val line_5_W2xExExW2 = line_5_W2xExE + brickW2
    private val line_5_W2xH2xH2 = listOf(brickW2, null, brickH2, brickH2)
    private val line_5_W2xH2xH2xS = line_5_W2xH2xH2 + singleBrick
    private val line_5_W2xH2xH2xW2 = line_5_W2xH2xH2 + brickW2
    private val line_6_ExExExExExE = listOf(null, null, null, null, null, null)
    private val line_6_ExH3xW3xE = listOf(null, brickH3, brickW3, null, null, null)
    private val line_6_ExH2xW3xE = listOf(null, brickH2, brickW3, null, null, null)
    private val line_6_ExExH3xW3 = listOf(null, null, brickH3, brickW3, null, null)
    private val line_6_ExExSxW3 = listOf(null, null, singleBrick, brickW3, null, null)
    private val line_6_SxSxSxH2xH2xH2 = listOf(singleBrick, singleBrick, singleBrick, brickH2, brickH2, brickH2)
    private val line_6_ExExExH3xH3xH3 = listOf(null, null, null, brickH3, brickH3, brickH3)
    private val line_6_ExExExSxSxS = listOf(null, null, null, singleBrick, singleBrick, singleBrick)
    private val line_6_ExExW2xExE = listOf(null, null, brickW2, null, null, null)
    private val line_6_H2xH2xW2xH2xH2 = listOf(brickH2, brickH2, brickW2, null, brickH2, brickH2)
    private val line_6_H3xH3xH3xW3 = listOf(brickH3, brickH3, brickH3, brickW3, null, null)
    private val line_6_H3xW3xExE = listOf(brickH3, brickW3, null, null, null, null)
    private val line_5_H3xW3xE = listOf(brickH3, brickW3, null, null, null)
    private val line_6_H2xH2xH2xW3 = listOf(brickH2, brickH2, brickH2, brickW3, null, null)
    private val line_6_SxSxW2xSxS = listOf(singleBrick, singleBrick, brickW2, null, singleBrick, singleBrick)
    private val line_6_SxExExH2xW2 = listOf(singleBrick, null, null, brickH2, brickW2, null)
    private val line_6_SxExExH3xW2 = listOf(singleBrick, null, null, brickH3, brickW2, null)
    private val line_5_SxExExH2xW3 = listOf(singleBrick, null, null, brickH2, brickW3)
    private val line_5_ExExExH2xW3 = listOf(singleBrick, null, null, brickH2, brickW3)
    private val line_6_W2xExExW2 = listOf(brickW2, null, null, null, brickW2, null)
    private val line_6_W2xH2xH2xW2 = listOf(brickW2, null, brickH2, brickH2, brickW2, null)
    private val line_6_W2xExExSxS = listOf(brickW2, null, null, null, singleBrick, singleBrick)
    private val line_6_W2xExExH3xS = listOf(brickW2, null, null, null, brickH3, singleBrick)
    private val line_6_W3xExExE = listOf(brickW3, null, null, null, null, null)
    private val line_6_W3xSxH2xH3 = listOf(brickW3, null, null, singleBrick, brickH2, brickH3)
    private val line_6_W3xExExH3 = listOf(brickW3, null, null, null, null, brickH3)
    private val line_5_W3xSxH2 = listOf(brickW3, null, null, singleBrick, brickH2)
    private val line_5_H3xW3xS = listOf(brickH3, brickW3, null, null, singleBrick)
    private val line_5_ExH3xW3 = listOf(null, brickH3, brickW3, null, null)
    private val line_5_ExExH3xW2 = listOf(null, null, brickH3, brickW2, null)
    private val line_5_ExExH3xW3 = listOf(null, null, brickH3, brickW3, null)
    private val line_5_ExExExH3xS = listOf(null, null, null, brickH3, singleBrick)
    private val line_5_ExExExExH2 = listOf(null, null, null, null, brickH2)
    private val line_5_ExExExExE = listOf(null, null, null, null, null)
    private val line_5_W3xExE = listOf(brickW3, null, null, null, null)
    private val line_9_W3xH3xH3xH3xW3 = listOf(brickW3, null, null, brickH3, brickH3, brickH3, brickW3, null, null)
    private val line_9_W3xExExExW3 = listOf(brickW3, null, null, null, null, null, brickW3, null, null)
    private val line_9_H3xH3xH3xW3xH3xH3xH3 =
        listOf(brickH3, brickH3, brickH3, brickW3, null, null, brickH3, brickH3, brickH3)
    private val line_9_ExExExW3xExExE = listOf(null, null, null, brickW3, null, null, null, null, null)

    private val basketWeaves_H3_V3_H3 = listOf(line_9_W3xH3xH3xH3xW3, line_9_W3xExExExW3, line_9_W3xExExExW3)
    private val basketWeaves_V3_H3_V3 =
        listOf(line_9_H3xH3xH3xW3xH3xH3xH3, line_9_ExExExW3xExExE, line_9_ExExExW3xExExE)

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
    }

    private fun createH(grammar: ShapeGrammar, n: Int) = Brick(grammar, MapSize2d(n, 1))
    private fun createH2(grammar: ShapeGrammar) = createH(grammar, 2)
    private fun createH3(grammar: ShapeGrammar) = createH(grammar, 3)
    private fun createV3(grammar: ShapeGrammar) = Brick(grammar, MapSize2d(1, 3))


    private fun createBasketWeave(
        h0: ShapeGrammar,
        h1: ShapeGrammar,
        h2: ShapeGrammar,
        v0: ShapeGrammar,
        v1: ShapeGrammar,
        v2: ShapeGrammar,
    ): List<List<Brick?>> {
        val H3_V3_V3_v3 = listOf(createH3(h0), null, null, createV3(v0), createV3(v1), createV3(v2))
        val line_1 = listOf(createH3(h1), null, null, null, null, null)
        val line_2 = listOf(createH3(h2), null, null, null, null, null)

        return listOf(H3_V3_V3_v3, line_1, line_2)
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
    ): List<List<Brick?>> {
        val line_6_H3xH3xH3xW3 = listOf(createV3(v0), createV3(v1), createV3(v2), createH3(h1), null, null)
        val line_6_ExExExH3xH3xH3 = listOf(null, null, null, createV3(v3), createV3(v4), createV3(v5))
        val line_6_W3xExExE = listOf(createH3(h0), null, null, null, null, null)

        return listOf(line_6_H3xH3xH3xW3, line_6_ExExExH3xH3xH3, line_6_ExExExExExE, line_6_W3xExExE)
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
    ): List<List<Brick?>> {
        val line0 = listOf(Brick(b00), Brick(b01), Brick(b02))
        val line1 = listOf(Brick(b10), Brick(b11), Brick(b12))
        val line2 = listOf(Brick(b20), Brick(b21), Brick(b22))

        return listOf(line0, line1, line2)
    }

    private fun createStack(
        b00: ShapeGrammar,
        b01: ShapeGrammar,
        b10: ShapeGrammar,
        b11: ShapeGrammar,
        b20: ShapeGrammar,
        b21: ShapeGrammar,
    ): List<List<Brick?>> {
        val line0 = listOf(createH2(b00), null, createH2(b01), null)
        val line1 = listOf(createH2(b10), null, createH2(b11), null)
        val line2 = listOf(createH2(b20), null, createH2(b21), null)

        return listOf(line0, line1, line2)
    }

    private fun testBasketWeave(
        selection: BrickSelection,
        expected: List<List<Brick?>>,
    ) = test(
        MapSize2d(6, 3),
        BrickPattern.BasketWeave,
        3,
        selection,
        expected,
    )

    private fun testBasketWeaveSingle(
        selection: BrickSelection,
        expected: List<List<Brick?>>,
    ) = test(
        MapSize2d(6, 4),
        BrickPattern.BasketWeaveSingle,
        3,
        selection,
        expected,
    )

    private fun testGrid(
        selection: BrickSelection,
        expected: List<List<Brick?>>,
    ) = test(
        MapSize2d(3, 3),
        BrickPattern.Grid,
        1,
        selection,
        expected,
    )

    private fun testStack(
        selection: BrickSelection,
        expected: List<List<Brick?>>,
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
        expected: List<List<Brick?>>,
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