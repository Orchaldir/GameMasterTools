package at.orchaldir.gm.core.model.economy.material

import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.math.unit.Weight
import kotlinx.serialization.Serializable

const val MIN_HARDNESS = 0.0f
const val MAX_HARDNESS = 20.0f

@Serializable
data class MaterialProperties(
    val category: MaterialCategory = MaterialCategory.Metal,
    val color: Color = Color.Pink,
    val crystalSystem: CrystalSystem = CrystalSystem.None,
    val density: Weight = Weight.fromKilograms(1000),
    val fracture: Fracture = Fracture.Uneven,
    /**
     * Using Mohs scale. See https://en.wikipedia.org/wiki/Mohs_scale
     */
    val hardness: Float = 1.0f,
    val luster: Luster = Luster.Dull,
    val tenacity: Tenacity = Tenacity.Brittle,
    val transparency: Transparency = Transparency.Opaque,
)