package at.orchaldir.gm.core.model.economy.material

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.math.unit.Weight
import kotlinx.serialization.Serializable

const val MIN_HARDNESS = 0.0f
const val MAX_HARDNESS = 20.0f

@Serializable
data class MaterialProperties(
    val category: MaterialCategory = UndefinedMaterialCategory,
    val crystalSystem: CrystalSystem = CrystalSystem.Amorphous,
    val density: Weight = Weight.fromKilograms(1000),
    val fracture: Fracture = Fracture.Undefined,
    /**
     * Using Mohs scale. See https://en.wikipedia.org/wiki/Mohs_scale
     */
    val hardness: Float = 1.0f,
    val luster: Luster = Luster.Dull,
    val tenacity: Tenacity = Tenacity.Undefined,
) {

    fun contains(material: MaterialId) = category.contains(material)

    fun validate(state: State) {
        require(hardness >= MIN_HARDNESS) { "Hardness $hardness is below minimum $MIN_HARDNESS!" }
        require(hardness <= MAX_HARDNESS) { "Hardness $hardness is above maximum $MAX_HARDNESS!" }
    }

}