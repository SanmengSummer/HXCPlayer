package huaxia.demo

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast

class MyAdapter1(var context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        var recyclerView = RecyclerView(context)
        return NewActivity.NewViewHolder(recyclerView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, p: Int) {
        var recyclerView: RecyclerView = holder.itemView as RecyclerView
        var params = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        recyclerView.layoutParams = params
        recyclerView.visibility = View.GONE
        recyclerView?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = MyAdapter2(context, p)
    }

    override fun getItemCount(): Int = 10


    class MyAdapter2(private val context: Context, var p: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = TextView(context)
            return NewActivity.New2ViewHolder(view)
        }

        @SuppressLint("SetTextI18n", "ShowToast")
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var view: TextView = holder.itemView as TextView
            view.setTextColor(context.resources.getColor(R.color.black))
            view.textSize = 20F
            view.text = p.toString() + "" + position.toString()
            var params = RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            params.setMargins(20, 10, 20, 10)
            view.layoutParams = params
            if ((position == 1) and (position % 2 != 0)) {
                Toast.makeText(this.context, "position:$position",Toast.LENGTH_LONG)
            }
        }

        override fun getItemCount(): Int = 5
    }
}

data class User(var string: String, var string1: String)