package com.tayyab.mobileapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.tayyab.mobileapp.databinding.CategoriesListItemBinding
import com.tayyab.mobileapp.interfaces.OnCategoryItemClickListener
import com.tayyab.mobileapp.models.Category
import java.util.stream.Collectors
import kotlin.streams.toList


class CategoriesAdapter(rowLayout: Int, recyclerView: RecyclerView) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    Filterable {

    private var recyclerView: RecyclerView? = null

    private var data: ArrayList<Category>? = null
    private var original: ArrayList<Category>? = null
    private var mOnCategoryItemClickListener: OnCategoryItemClickListener? = null

    private val rowLayout: Int

    var int: Int = 0

    init {
        this.recyclerView = recyclerView
        this.rowLayout = rowLayout
        this.data = ArrayList()
        this.original = ArrayList()
    }

    fun setOnItemClickListener(mCategoryItemClickListener: OnCategoryItemClickListener) {
        this.mOnCategoryItemClickListener = mCategoryItemClickListener
    }

    fun insertData(items: List<Category>) {
        //val found: Boolean = data?.stream().anyMatch { p -> p.name.equals(personName) }
        val common: List<Category> = items
            .stream()
            .filter(data!!::contains)
            .collect(Collectors.toList())
//        val gson = Gson()
//        val arrayData = gson.toJson(common)
//        Log.e("TEST:", arrayData)
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
        } else if (data!!.size != common.size) {
            Log.e("TEST:", "else block")
            data!!.clear()
            original!!.clear()
            // int positionStart = getItemCount();
            // int itemCount = items.size();
            this.data!!.addAll(items)
            //notifyDataSetChanged()
            recyclerView!!.post { notifyItemRangeChanged(common.size, data!!.size) }
            this.original!!.addAll(items)
            // this.original!!.addAll(items)
            int = data!!.size
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val wordListItemBinding: CategoriesListItemBinding =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), rowLayout, parent, false)
        return RecyclerViewHolder(wordListItemBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RecyclerViewHolder) {
            val word: Category = data!![position]
            holder.itemBinding.category = word
            holder.itemBinding.clicklistner = mOnCategoryItemClickListener
        }
    }

    class RecyclerViewHolder(wordListItemBinding: CategoriesListItemBinding) :
        RecyclerView.ViewHolder(wordListItemBinding.root) {
        val itemBinding: CategoriesListItemBinding = wordListItemBinding

    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                // Log.e("SSS", charString);
                if (charString.isEmpty()) {
                    data = original
                } else {
                    val list = original?.stream()
                        ?.filter { s ->
                            s.cat_name.contains(
                                charString,
                                ignoreCase = true
                            ) || s.cat_id.toString().contains(charString, ignoreCase = true)
                        }?.toList()?.toCollection(ArrayList<Category>())
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

}
