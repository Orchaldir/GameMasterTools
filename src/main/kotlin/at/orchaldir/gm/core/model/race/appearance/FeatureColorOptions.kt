package at.orchaldir.gm.core.model.race.appearance

import at.orchaldir.gm.core.model.character.appearance.FeatureColorType
import at.orchaldir.gm.core.model.character.appearance.SkinType
import at.orchaldir.gm.core.model.material.MaterialId
import kotlinx.serialization.Serializable

@Serializable
data class FeatureColorOptions(
    val types: FeatureColorType = FeatureColorType.Overwrite,
    val skin: SkinOptions = SkinOptions(),
) {

    fun contains(material: MaterialId): Boolean {
        if (types == FeatureColorType.Overwrite) {
            return skin.contains(material)
        }

        return false
    }

}
