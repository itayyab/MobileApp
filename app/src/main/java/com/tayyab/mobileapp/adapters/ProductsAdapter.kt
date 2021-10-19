package com.tayyab.mobileapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tayyab.mobileapp.databinding.ProductListItemBinding
import com.tayyab.mobileapp.interfaces.OnProductItemClickListener
import com.tayyab.mobileapp.interfaces.OnSpeakClickListener
import com.tayyab.mobileapp.models.Product
import com.tayyab.mobileapp.utils.Config
import kotlin.streams.toList


class ProductsAdapter(rowLayout: Int, recyclerView: RecyclerView) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    Filterable {

    private var recyclerView: RecyclerView? = null
    private var data: ArrayList<Product>? = null
    private var original: ArrayList<Product>? = null
    private var mOnProductItemClickListener: OnProductItemClickListener? = null
    private var onSpeakClickListener: OnSpeakClickListener? = null

    private val rowLayout: Int
    var int: Int = 0

    init {
        this.recyclerView = recyclerView
        this.rowLayout = rowLayout
        this.data = ArrayList()
        this.original = ArrayList()
    }

    fun setOnItemClickListener(mProductItemClickListener: OnProductItemClickListener) {
        this.mOnProductItemClickListener = mProductItemClickListener
    }

    fun setOnSpeakClickListener(mItemClickListener: OnSpeakClickListener) {
        this.onSpeakClickListener = mItemClickListener
    }

    fun insertData(items: List<Product>) {
        if (items.size > data!!.size) {
            data!!.clear()
            original!!.clear()
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
            holder.itemBinding.clicklistner = mOnProductItemClickListener
            holder.itemBinding.speakclicklistner = onSpeakClickListener
            holder.imagelist?.loadUrl(Config.IMAGE_BASE_URL + data!![position].pr_Picture)
        }
    }

    class RecyclerViewHolder(wordListItemBinding: ProductListItemBinding) :
        RecyclerView.ViewHolder(wordListItemBinding.root) {
        var imagelist: ImageView? = wordListItemBinding.imageView

        val itemBinding: ProductListItemBinding = wordListItemBinding

    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
//                Log.e("SSS", charString)
                if (charString.isEmpty()) {
                    data = original
                } else {
//                    Log.e("EEE", "ELSE" + original!!.size)
                    val list = original?.stream()
                        ?.filter { s ->
                            s.pr_name.contains(
                                charString,
                                ignoreCase = true
                            ) || s.pr_desc.contains(charString, ignoreCase = true)
                        }?.toList()?.toCollection(ArrayList<Product>())

                    data = list!!

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

    private fun ImageView.loadUrl(url: String) {
        Picasso.with(context).load(url).into(this)
    }
}
