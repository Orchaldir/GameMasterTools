package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.*
import at.orchaldir.gm.core.action.UpdateAction
import at.orchaldir.gm.core.model.Data
import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.culture.Culture
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.health.Disease
import at.orchaldir.gm.core.model.race.Race
import at.orchaldir.gm.core.model.realm.Battle
import at.orchaldir.gm.core.model.realm.Catastrophe
import at.orchaldir.gm.core.model.realm.Realm
import at.orchaldir.gm.core.model.realm.Town
import at.orchaldir.gm.core.model.realm.War
import at.orchaldir.gm.core.model.time.Time
import at.orchaldir.gm.core.model.time.date.Day
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.world.moon.Moon
import at.orchaldir.gm.core.reducer.REDUCER
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class VitalStatusTest {

    @Test
    fun `Cannot delete a character that destroyed a business`() {
        testCanDeleteDestroyer(
            CHARACTER_ID_0,
            KilledBy(CharacterReference(CHARACTER_ID_0)),
            DeleteResult(CHARACTER_ID_0).addId(BUSINESS_ID_0),
        ) { status ->
            Business(BUSINESS_ID_0, status = status)
        }
    }

    @Test
    fun `Cannot delete a character that destroyed a character`() {
        testCanDeleteDestroyer(
            CHARACTER_ID_0,
            KilledBy(CharacterReference(CHARACTER_ID_0)),
            DeleteResult(CHARACTER_ID_0).addId(CHARACTER_ID_1),
        ) { status ->
            Character(CHARACTER_ID_1, vitalStatus = status)
        }
    }

    @Test
    fun `Cannot delete a character that destroyed a moon`() {
        testCanDeleteDestroyer(
            CHARACTER_ID_0,
            KilledBy(CharacterReference(CHARACTER_ID_0)),
            DeleteResult(CHARACTER_ID_0).addId(MOON_ID_0),
        ) { status ->
            Moon(MOON_ID_0, status = status)
        }
    }

    @Test
    fun `Cannot delete a character that destroyed a realm`() {
        testCanDeleteDestroyer(
            CHARACTER_ID_0,
            KilledBy(CharacterReference(CHARACTER_ID_0)),
            DeleteResult(CHARACTER_ID_0).addId(REALM_ID_0),
        ) { status ->
            Realm(REALM_ID_0, status = status)
        }
    }

    @Test
    fun `Cannot delete a character that destroyed a town`() {
        testCanDeleteDestroyer(
            CHARACTER_ID_0,
            KilledBy(CharacterReference(CHARACTER_ID_0)),
            DeleteResult(CHARACTER_ID_0).addId(TOWN_ID_0),
        ) { status ->
            Town(TOWN_ID_0, status = status)
        }
    }
}

fun <ID0 : Id<ID0>, ID1 : Id<ID1>, ELEMENT0 : Element<ID0>> testCanDeleteDestroyer(
    destroyer: ID1,
    cause: CauseOfDeath,
    expected: DeleteResult,
    createDestroyed: (VitalStatus) -> ELEMENT0,
) {
    VitalStatusType.entries.forEach { type ->
        val status = when (type) {
            VitalStatusType.Abandoned -> Abandoned(DAY2, cause)
            VitalStatusType.Alive -> return@forEach
            VitalStatusType.Closed -> return@forEach
            VitalStatusType.Dead -> Dead(DAY2, cause)
            VitalStatusType.Destroyed -> Destroyed(DAY2, cause)
            VitalStatusType.Vanished -> return@forEach
        }
        val destroyedElement = createDestroyed(status)
        val result = DeleteResult(destroyer)

        State(Storage(destroyedElement)).canDeleteDestroyer(destroyer, result)

        assertEquals(expected, result)
    }
}