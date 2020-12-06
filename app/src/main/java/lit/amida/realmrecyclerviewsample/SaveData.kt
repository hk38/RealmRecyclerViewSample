package lit.amida.realmrecyclerviewsample

import io.realm.annotations.PrimaryKey
import java.util.*

open class SaveData(
    @PrimaryKey open var id: String = UUID.randomUUID().toString(),
    open var title: String = "",
    open var content: String = "",
    open var details: String = "",
    open var icon: Int = 0
)