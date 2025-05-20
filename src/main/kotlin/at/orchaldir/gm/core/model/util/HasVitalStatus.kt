package at.orchaldir.gm.core.model.util

interface HasVitalStatus : HasStartAndEndDate {
    fun vitalStatus(): VitalStatus

    override fun endDate() = vitalStatus().getDeathDate()
}