package at.orchaldir.gm.core.model.source

interface HasDataSources {

    fun sources(): Set<DataSourceId>

}