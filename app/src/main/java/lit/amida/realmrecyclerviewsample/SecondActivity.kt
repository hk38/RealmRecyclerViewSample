package lit.amida.realmrecyclerviewsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import io.realm.Realm
import java.util.*
import kotlin.random.Random

class SecondActivity : AppCompatActivity() {
    private val realm by lazy {
        Realm.getDefaultInstance()
    }

    // idをonCreate()とonDestroy()で利用するため
    var id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        // 新規作成時ランダムに画像を設定するためのリスト
        val imageList = listOf(R.drawable.ic_android_black, R.drawable.ic_baseline_directions_run, R.drawable.ic_baseline_star, R.drawable.ic_baseline_sports_esports, R.drawable.ic_baseline_tag_faces)
        // MainActivityのRecyclerViewの要素をタップした場合はidが，fabをタップした場合はnullが入っているはず
        id = intent.getStringExtra("id")

        // idがnull，つまり新規作成の場合
        if(id == null){
            // 乱数を生成し，画像をランダムに設定
            val rand = Random.nextInt(imageList.size - 1)
            findViewById<ImageView>(R.id.image_view).setImageResource(imageList[rand])

            // 新しい要素に重複しないIDを設定するため，ランダムなUUIDを生成
            id = UUID.randomUUID().toString()
            realm.executeTransaction {
                // 生成したIDを設定して新規作成
                val item = it.createObject(SaveData::class.java, id)
                item.icon = imageList[rand]
            }


        // idがnull以外，つまり既に作成された要素を編集する場合
        }else{
            // MainActivityに渡されたidを元にデータを検索して取得
            val item = realm.where(SaveData::class.java).equalTo("id", id).findFirst()

            //もしidが間違っていたりして取得に失敗したら以下の「取得したデータをViewに設定する」処理は行わない
            if(item != null) {
                findViewById<EditText>(R.id.edit_title).setText(item.title)
                findViewById<EditText>(R.id.edit_content).setText(item.content)
                findViewById<EditText>(R.id.edit_details).setText(item.details)
                findViewById<ImageView>(R.id.image_view).setImageResource(item.icon)
            }

        }
        
    }

    // 終了時にデータを保存してRealmを終了する．
    override fun onDestroy() {
        realm.executeTransaction { 
            val item = realm.where(SaveData::class.java).equalTo("id", id).findFirst()
            item?.title = findViewById<EditText>(R.id.edit_title).text.toString()
            item?.content = findViewById<EditText>(R.id.edit_content).text.toString()
            item?.details = findViewById<EditText>(R.id.edit_title).text.toString()
        }

        realm.close()
        super.onDestroy()
    }
}