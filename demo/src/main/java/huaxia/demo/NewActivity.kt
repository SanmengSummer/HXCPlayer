package huaxia.demo

import android.content.ComponentName
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView.*
import android.view.View
import com.summer.chxplayer.widght.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_new.*

class NewActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        if (v?.id == R.id.btn1)
            go2player()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new)
        btn1.setOnClickListener(this)
        setMe()
    }

    private fun setMe() {
        re.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        re?.adapter = MyAdapter1(this.applicationContext)

    }

    fun love(v: View) {
        love?.addLove()
    }

    private fun go2player() {
        val intent = Intent()
        ToastUtil.getInstance(this).Long("ssss")
        intent.setClass(this, HuanPlayerDemo::class.java)
        startActivity(intent)
    }

    fun go2demo(v: View) {
        try {
            val intent = Intent()
//        intent.addCategory(Intent.CATEGORY_LAUNCHER)
//参数是包名，类全限定名，注意直接用类名不行
            val cn = ComponentName("tv.huan.tencentTV",
                    "tv.huan.tencentTV.ui.home.LoadingPage")
            intent.component = cn
            intent.putExtra("content_id", "e7hi6lep1yc51ca")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.action = Intent.ACTION_VIEW
            startActivity(intent)
        } catch (e: Exception) {
            println("程序出现了未知异常，可能是您的人品太差了。${e.message}")
        }


//        startActivity(Intent(this, DemoTestActivity::class.java))
    }

    class NewViewHolder(itemView: View) : ViewHolder(itemView)

    class New2ViewHolder(itemView: View) : ViewHolder(itemView)
}
