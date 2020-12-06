package lit.amida.realmrecyclerviewsample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.realm.Realm
import java.util.*

class MainActivity : AppCompatActivity() {
    // by lazyは多用すると地獄を見るのでRealmの宣言以外には使わないこと．特にfindViewById()はby lazyで設定すると落ちる原因になる
    private val realm by lazy {
        Realm.getDefaultInstance()
    }

    // onCreate(), onResume()で利用するためクラス内に記述
    var adapter: MyListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // もしrealmのデータベースが空だったら初期データを作成する
        // 初期データ作成が必要なければ不要
        if(realm.where(SaveData::class.java).findAll().isNullOrEmpty()) createInitList()


        // RecyclerView用のAdapterを作成
        adapter = MyListAdapter(this, object: MyListAdapter.OnItemClickListner{
            override fun onItemClick(item: SaveData) {
                // SecondActivityに遷移するためのIntent
                val intent = Intent(applicationContext, SecondActivity::class.java)
                // RecyclerViewの要素をタップするとintentによりSecondActivityに遷移する
                // また，要素のidをSecondActivityに渡す
                intent.putExtra("id", item.id)
                startActivity(intent)
            }
        })

        // RecyclerViewの変数を作成してレイアウトを決定し，上で作ったadapterをセット
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter


        //floating action buttonタップ時の動作
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            // SecondActivityに遷移するためのIntent
            val intent = Intent(applicationContext, SecondActivity::class.java)
            // SecondActivityに遷移
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // RecyclerViewのAdapterにRealmに保存された全データのリストを設定
        // ここに書くことによりSecondActivityで変更があってもリストに反映される
        adapter?.setList(realm.where(SaveData::class.java).findAll())
    }

    // Activity終了時にRealmを終了すること
    override fun onDestroy() {
        realm.close()
        super.onDestroy()
    }

    // 初期データ作成関数
    fun createInitList(){
        createSaveData("Android", "ドロイド君の頭部", "使い所はよく分からない", R.drawable.ic_android_black)
        createSaveData("Run", "走る人のピクトグラム", "走ることを指示するときに使うらしい", R.drawable.ic_baseline_directions_run)
        createSaveData("star", "星マーク", "お気に入りとかを表すときに使う", R.drawable.ic_baseline_star)
    }

    // データ作成関数
    fun createSaveData(title: String, content: String, details: String, icon: Int){
        realm.executeTransaction {
            val item = it.createObject(SaveData::class.java, UUID.randomUUID().toString())
            item.title = title
            item.content = content
            item.details = details
            item.icon = icon
        }
    }

    // Realmの全データを削除する関数のサンプル
    fun deleteAll(){
       realm.executeTransaction { realm.deleteAll() }
    }

    // Realmの特定のデータを削除する関数のサンプル
    fun delete(id: String){
        realm.executeTransaction { it.where(SaveData::class.java).equalTo("id", id).findFirst()?.deleteFromRealm() }
    }
}