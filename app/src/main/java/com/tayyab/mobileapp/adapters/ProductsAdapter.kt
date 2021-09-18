package com.tayyab.mobileapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tayyab.mobileapp.Config
import com.tayyab.mobileapp.databinding.ProductListItemBinding
import com.tayyab.mobileapp.interfaces.OnItemClickListener
import com.tayyab.mobileapp.interfaces.OnSpeakClickListener
import com.tayyab.mobileapp.models.Product
import java.util.*
import kotlin.collections.ArrayList


class ProductsAdapter(rowLayout: Int, recyclerView: RecyclerView) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    Filterable {

    private var recyclerView: RecyclerView? = null
   // private var shift: Boolean = false
    private var data: ArrayList<Product>? = null
    private var original: ArrayList<Product>? = null
    private var mOnItemClickListener: OnItemClickListener? = null
    private var onSpeakClickListener: OnSpeakClickListener? = null

    private val rowLayout: Int

    // internal var searchText = ""
    var int: Int = 0

    init {
        this.recyclerView = recyclerView
        this.rowLayout = rowLayout
       // this.shift = shift
        this.data = ArrayList()
        this.original = ArrayList()
    }

    fun setOnItemClickListener(mItemClickListener: OnItemClickListener) {
        this.mOnItemClickListener = mItemClickListener
    }
    fun setOnSpeakClickListener(mItemClickListener: OnSpeakClickListener) {
        this.onSpeakClickListener = mItemClickListener
    }

    fun insertData(items: List<Product>) {
        if (items.size > data!!.size) {
            data!!.clear()
            // original!!.clear()
            // int positionStart = getItemCount();
            // int itemCount = items.size();
            this.data!!.addAll(items)
            //notifyDataSetChanged()
            recyclerView!!.post { notifyItemRangeChanged(int, data!!.size) }
            this.original!!.addAll(items)
            // this.original!!.addAll(items)

            int = data!!.size
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val wordListItemBinding: ProductListItemBinding =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), rowLayout, parent, false)
        return RecyclerViewHolder(wordListItemBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RecyclerViewHolder) {
           // holder.itemBinding.shift = shift
            val word: Product = data!![position]
            holder.itemBinding.product = word
            holder.itemBinding.clicklistner = mOnItemClickListener
            holder.itemBinding.speakclicklistner = onSpeakClickListener
            holder?.image_list?.loadUrl(Config.IMAGE_BASE_URL + data!![position].pr_Picture)
        }
    }

    class RecyclerViewHolder(wordListItemBinding: ProductListItemBinding) :
        RecyclerView.ViewHolder(wordListItemBinding.root) {
        var image_list: ImageView? = wordListItemBinding.imageView

        val itemBinding: ProductListItemBinding = wordListItemBinding

    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString().toLowerCase(Locale.getDefault())
               // Log.e("SSS", charString);
                if (charString.isEmpty()) {
                    //  searchText = ""
                    data = original
                } else {
                    //    searchText = charString
                    val filteredList = ArrayList<Product>()
//                    for (item in original!!) {
//                        if (item.synonym.isNullOrEmpty()) {
//                            if (item.Word!!.contains(charString) || item.Meaning!!.contains(
//                                    charString
//                                )
//                            ) {
//                                filteredList.add(item)
//                                //  Log.e("EEE",item.Word!!);
//                            }
//                        } else {
//                            if (item.Word!!.contains(charString) || item.Meaning!!.contains(
//                                    charString
//                                ) || item.synonym!!.contains(charString)
//                            ) {
//                                filteredList.add(item)
//                                //     Log.e("EEE",item.Word!!);
//                            }
//                        }
//                    }
                    data = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = data
                return filterResults
            }

            override fun publishResults(
                charSequence: CharSequence,
                filterResults: FilterResults
            ) {
                notifyDataSetChanged()
            }
        }

    }
    fun ImageView.loadUrl(url: String) {
        Picasso.with(context).load(url).into(this)
    }
}
