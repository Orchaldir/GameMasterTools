package at.orchaldir.gm.visualization.grammar

import at.orchaldir.gm.core.model.util.part.ItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromStone
import at.orchaldir.gm.core.model.visualization.BrickPattern
import at.orchaldir.gm.core.model.visualization.BrickPatternGrammar
import at.orchaldir.gm.core.model.visualization.GridSize
import at.orchaldir.gm.core.model.visualization.RectangularShapeGrammar
import at.orchaldir.gm.core.model.visualization.RowsAndColumns
import at.orchaldir.gm.utils.Counter
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.map.assertTilemap
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class BrickPatternCreationTest {

    private val brick = RectangularShapeGrammar(MadeFromStone())
    private val singleBlock = MapSize2d.square(1)

    @Nested
    inner class CreateGridTest {

        @Test
        fun `Test creating a grid`() {
            val size = MapSize2d(3, 2)
            val pattern = BrickPatternGrammar(
                brick,
                RowsAndColumns(size),
                BrickPattern.Grid,
            )

            val tilemap = createGrid(pattern)

            assertTilemap(tilemap, size, Brick(brick, singleBlock))
        }

    }

}