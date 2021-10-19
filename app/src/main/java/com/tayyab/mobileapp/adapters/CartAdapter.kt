package com.tayyab.mobileapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tayyab.mobileapp.databinding.CartdetailsListItemBinding
import com.tayyab.mobileapp.interfaces.OnCartItemClickListener
import com.tayyab.mobileapp.interfaces.OnSpeakClickListener
import com.tayyab.mobileapp.models.CartDetail
import com.tayyab.mobileapp.utils.Config


class CartAdapter(rowLayout: Int, recyclerView: RecyclerView) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var recyclerView: RecyclerView? = null
    private var data: ArrayList<CartDetail>? = null
    private var mOnProductItemClickListener: OnCartItemClickListener? = null
    private var onSpeakClickListener: OnSpeakClickListener? = null
    private val rowLayout: Int
    var int: Int = 0

    init {
        this.recyclerView = recyclerView
        this.rowLayout = rowLayout
        this.data = ArrayList()
    }

    fun setOnItemClickListener(mProductItemClickListener: OnCartItemClickListener) {
        this.mOnProductItemClickListener = mProductItemClickListener
    }

    fun setOnSpeakClickListener(mItemClickListener: OnSpeakClickListener) {
        this.onSpeakClickListener = mItemClickListener
    }

    fun insertData(items: List<CartDetail>) {
        if (items.size > data!!.size) {
            data!!.clear()
//             original!!.clear()
            // int positionStart = getItemCount();
            // int itemCount = items.size();
            this.data!!.addAll(items)
            //notifyDataSetChanged()
            recyclerView!!.post { notifyItemRangeChanged(int, data!!.size) }
//            this.original!!.addAll(items)
            // this.original!!.addAll(items)

            int = data!!.size
        } else {
            data!!.clear()
//             original!!.clear()
            // int positionStart = getItemCount();
            // int itemCount = items.size();
            this.data!!.addAll(items)
            notifyDataSetChanged()
//            recyclerView!!.post { notifyDataSetChanged() }
//            this.original!!.addAll(items)
            // this.original!!.addAll(items)

            int = data!!.size
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val wordListItemBinding: CartdetailsListItemBinding =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), rowLayout, parent, false)
        return RecyclerViewHolder(wordListItemBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RecyclerViewHolder) {
            // holder.itemBinding.shift = shift
//            val word: Carts = data!![position]
//            holder.itemBinding.carts = data!![position]
            holder.itemBinding.cartdetails = data!![position]
            holder.itemBinding.clicklistner = mOnProductItemClickListener
            holder.itemBinding.speakclicklistner = onSpeakClickListener
            holder.imagelist?.loadUrl(Config.IMAGE_BASE_URL + data!![position].product?.pr_Picture)
        }
    }

    class RecyclerViewHolder(wordListItemBinding: CartdetailsListItemBinding) :
        RecyclerView.ViewHolder(wordListItemBinding.root) {
        var imagelist: ImageView? = wordListItemBinding.imageView

        val itemBinding: CartdetailsListItemBinding = wordListItemBinding

    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    private fun ImageView.loadUrl(url: String) {
        Picasso.with(context).load(url).into(this)
    }
}
