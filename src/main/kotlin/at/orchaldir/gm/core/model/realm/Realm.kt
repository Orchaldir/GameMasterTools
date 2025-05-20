package at.orchaldir.gm.core.model.realm

import at.orchaldir.gm.core.model.economy.money.CurrencyId
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.source.DataSourceId
import at.orchaldir.gm.core.model.source.HasDataSources
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val REALM_TYPE = "Realm"

@JvmInline
@Serializable
value class RealmId(val value: Int) : Id<RealmId> {

    override fun next() = RealmId(value + 1)
    override fun type() = REALM_TYPE
    override fun value() = value

}

@Serializable
data class Realm(
    val id: RealmId,
    val name: Name = Name.init("Realm ${id.value}"),
    val founder: Creator = UndefinedCreator,
    val date: Date? = null,
    val status: VitalStatus = Alive,
    val capital: History<TownId?> = History(null),
    val owner: History<RealmId?> = History(null),
    val currency: History<CurrencyId?> = History(null),
    val legalCode: History<LegalCodeId?> = History(null),
    val sources: Set<DataSourceId> = emptySet(),
) : ElementWithSimpleName<RealmId>, Creation, HasDataSources, HasVitalStatus {

    override fun id() = id
    override fun name() = name.text
    override fun creator() = founder
    override fun sources() = sources
    override fun startDate() = date
    override fun vitalStatus() = status

}
