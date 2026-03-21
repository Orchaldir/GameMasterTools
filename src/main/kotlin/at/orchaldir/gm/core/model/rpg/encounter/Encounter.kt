package at.orchaldir.gm.core.model.rpg.encounter

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val ENCOUNTER_TYPE = "Encounter"

@JvmInline
@Serializable
value class EncounterId(val value: Int) : Id<EncounterId> {

    override fun next() = EncounterId(value + 1)
    override fun type() = ENCOUNTER_TYPE
    override fun value() = value

}

@Serializable
data class Encounter(
    val id: EncounterId,
    val name: Name = Name.init(id),
    val entry: EncounterEntry = NoEncounter,
) : ElementWithSimpleName<EncounterId> {

    override fun id() = id
    override fun name() = name.text

    override fun validate(state: State) {
    }
}