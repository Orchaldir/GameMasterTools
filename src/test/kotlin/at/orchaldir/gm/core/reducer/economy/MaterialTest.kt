package at.orchaldir.gm.core.reducer.economy

import at.orchaldir.gm.MATERIAL_ID_0
import at.orchaldir.gm.assertIllegalArgument
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.Material
import at.orchaldir.gm.core.model.economy.material.MaterialProperties
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Test

class MaterialTest {
    val state = State(Storage(Material(MATERIAL_ID_0)))

    @Test
    fun `Hardness is too low`() {
        val properties = MaterialProperties(hardness = -0.1f)
        val material = Material(MATERIAL_ID_0, properties = properties)

        assertIllegalArgument("Hardness -0.1 is below minimum 0.0!") { material.validate(state) }
    }

    @Test
    fun `Material is valid`() {
        val properties = MaterialProperties(color = Color.Green)
        val material = Material(MATERIAL_ID_0, properties = properties)

        material.validate(state)
    }

}