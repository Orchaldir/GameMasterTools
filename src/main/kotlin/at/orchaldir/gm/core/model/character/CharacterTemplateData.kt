package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.core.model.culture.CultureId
import at.orchaldir.gm.core.model.culture.language.ComprehensionLevel
import at.orchaldir.gm.core.model.culture.language.LanguageId
import at.orchaldir.gm.core.model.race.RaceId
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.model.util.source.HasDataSources
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class CharacterTemplateDataType {
    Simple,
}

@Serializable
sealed class CharacterTemplateData

@Serializable
@SerialName("Simple")
data class SimpleCharacterTemplateData(
    val race: RaceId,
    val gender: Gender? = null,
    val culture: CultureId? = null,
) : CharacterTemplateData()

@Serializable
@SerialName("Modification")
data class CharacterTemplateDataModification(
    val original: CharacterTemplateId,
    val race: RaceId?,
    val gender: Gender? = null,
    val culture: CultureId? = null,
) : CharacterTemplateData()