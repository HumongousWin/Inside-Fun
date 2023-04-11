package com.github.humongouswin.insidefun.browser

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.humongouswin.insidefun.R
import com.github.humongouswin.insidefun.VideoBrowserActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView

class CategoriesActivity : AppCompatActivity() {
    private var recyclerview: RecyclerView? = null
    private val categoryList: ArrayList<CategoryMode> = ArrayList()
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var myAdapter: CategoryAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.category_activity)
        recyclerview = findViewById<View>(R.id.recyclerview) as RecyclerView?

        val db = Firebase.firestore
        db.collection("cast_videos")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if (document.id.equals("oORfCdmnpEvM6eIbuEdf")) {
//                        Log.d("Sagar--->", "${document.id} => ${document.data}")
                        val variable = document.get("Categories") as List<CategoryMode>
                        for (i in 0 until variable.size) {
                            val obj = variable[i] as HashMap<*, *>
                            categoryList.add(
                                CategoryMode(

                                    obj.get("cat_name").toString(),
                                    obj.get("cat_url").toString(),
                                    obj.get("cat_image").toString()
                                    )
                            )
                        }
                    }
                }

                layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                myAdapter = CategoryAdapter(this@CategoriesActivity, categoryList)
                recyclerview!!.layoutManager = layoutManager
                recyclerview!!.adapter = myAdapter
            }
            .addOnFailureListener { exception ->
                Log.w("Sagar--->", "Error getting documents.", exception)
            }
    }

    class CategoryMode {
        var cat_name: String? = null
            get() = field
            set(value) {
                field = value
            }
        var cat_url: String? = null
            get() = field
            set(value) {
                field = value
            }
        var cat_image: String? = null
            get() = field
            set(value) {
                field = value
            }
        constructor(cat_name: String?, cat_url: String?,cat_image: String?) {
            this.cat_name = cat_name
            this.cat_url = cat_url
            this.cat_image = cat_image

        }
    }

    class CategoryAdapter(val context: Context, private val userList: ArrayList<CategoryMode>) :
        RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

        //this method is returning the view for each item in the list
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v =
                LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
            return ViewHolder(v)
        }

        //this method is binding the data on the list
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.txt_name.text = userList[position].cat_name
            if(userList[position].cat_image.isNullOrEmpty() || userList[position].cat_image=="null")
                holder.image.visibility=View.GONE
            else
                holder.image.visibility=View.VISIBLE
            Glide
                .with(holder.itemView.context)
                .load(userList[position].cat_image)
                .centerCrop()
                .into(holder.image);

            holder.itemView.setOnClickListener {
                val intent = Intent(context, VideoBrowserActivity::class.java)
                intent.putExtra("video_url", userList[position].cat_url)
                context.startActivity(intent)
            }
        }

        //this method is giving the size of the list
        override fun getItemCount(): Int {
            return userList.size
        }

        //the class is hodling the list view
        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var txt_name: TextView
            var image: CircleImageView

            init {
                txt_name = itemView.findViewById<TextView>(R.id.txt_name)
                image=itemView.findViewById<CircleImageView>(R.id.image)
            }
        }
    }
}