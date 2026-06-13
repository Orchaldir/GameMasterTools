package at.orchaldir.gm.visualization.grammar

import at.orchaldir.gm.core.model.util.part.MadeFromStone
import at.orchaldir.gm.core.model.visualization.BrickPattern
import at.orchaldir.gm.core.model.visualization.BrickPatternGrammar
import at.orchaldir.gm.core.model.visualization.RectangularShapeGrammar
import at.orchaldir.gm.core.model.visualization.RowsAndColumns
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.map.assertTilemap
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class BrickPatternCreationTest {

    private val brickGrammar = RectangularShapeGrammar(MadeFromStone())
    private val brick1 = Brick(brickGrammar, MapSize2d(1, 1))
    private val brick2 = Brick(brickGrammar, MapSize2d(2, 1))

    @Nested
    inner class CreateGridPatternTest {

        @Test
        fun `Test creating a grid`() {
            val size = MapSize2d(3, 2)
            val pattern = BrickPatternGrammar(
                brickGrammar,
                RowsAndColumns(size),
                BrickPattern.Grid,
            )

            val tilemap = createGridPattern(pattern)

            assertTilemap(tilemap, size, brick1)
        }

    }

    @Nested
    inner class CreateStackPatternTest {

        @Test
        fun `A stack pattern without partial bricks`() {
            val size = MapSize2d(4, 2)
            val pattern = BrickPatternGrammar(
                brickGrammar,
                RowsAndColumns(size),
                BrickPattern.Grid,
                2,
            )

            val tilemap = createStackPattern(pattern, Borders(true))


            assertTilemap(tilemap, size, listOf(brick2, null, brick2, null, brick2, null, brick2, null))
        }

        @Test
        fun `A stack pattern with partial bricks on the right border`() {
            val size = MapSize2d(3, 2)
            val pattern = BrickPatternGrammar(
                brickGrammar,
                RowsAndColumns(size),
                BrickPattern.Grid,
                2,
            )

            val tilemap = createStackPattern(pattern, Borders(true))


            assertTilemap(tilemap, size, listOf(brick2, null, brick1, brick2, null, brick1))
        }

    }

}