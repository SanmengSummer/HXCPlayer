package huaxia.demo

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView.*
import android.view.View
import kotlinx.android.synthetic.main.activity_new.*

class NewActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new)
        setMe()
    }

    private fun setMe() {
        re.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        re?.adapter = MyAdapter1(this.applicationContext)

    }

    fun love(v: View) {
        love?.addLove()
    }

    fun go2demo(v: View) {
        startActivity(Intent(this, DemoTestActivity::class.java))
    }

    class NewViewHolder(itemView: View) : ViewHolder(itemView)

    class New2ViewHolder(itemView: View) : ViewHolder(itemView)
}
