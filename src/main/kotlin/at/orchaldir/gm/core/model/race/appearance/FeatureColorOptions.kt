package at.orchaldir.gm.core.model.race.appearance

import at.orchaldir.gm.core.model.character.appearance.FeatureColorType
import kotlinx.serialization.Serializable

@Serializable
data class FeatureColorOptions(
    val types: FeatureColorType = FeatureColorType.Overwrite,
    val skin: SkinOptions = SkinOptions(),
)
