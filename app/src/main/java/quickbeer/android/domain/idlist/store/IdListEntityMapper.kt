package quickbeer.android.domain.idlist.store

import quickbeer.android.domain.idlist.IdList
import quickbeer.android.util.Mapper

object IdListEntityMapper : Mapper<IdList, IdListEntity> {

    override fun mapFrom(source: IdList): IdListEntity {
        return IdListEntity(source.key, source.values, source.updated)
    }

    override fun mapTo(source: IdListEntity): IdList {
        return IdList(source.id, source.values, source.updated)
    }
}
