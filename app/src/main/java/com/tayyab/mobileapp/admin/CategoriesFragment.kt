package com.tayyab.mobileapp.admin

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.tayyab.mobileapp.R
import com.tayyab.mobileapp.activities.MainActivityAdminViewModel
import com.tayyab.mobileapp.adapters.CategoriesAdapter
import com.tayyab.mobileapp.databinding.FragmentCategoriesBinding
import com.tayyab.mobileapp.interfaces.OnCategoryItemClickListener
import com.tayyab.mobileapp.models.Category
import com.tayyab.mobileapp.utils.AutoClearedValue

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private lateinit var categoriesViewModel: CategoriesViewModel
    private var bindingx: AutoClearedValue<FragmentCategoriesBinding>? = null
    private var adapter: AutoClearedValue<CategoriesAdapter>? = null
    private var wordsadapter: CategoriesAdapter? = null
    lateinit var editText: EditText

    private val activityViewModel: MainActivityAdminViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        categoriesViewModel =
            ViewModelProvider(this).get(CategoriesViewModel::class.java)
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        bindingx = AutoClearedValue(this@CategoriesFragment, _binding!!)



        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categoriesViewModel.getProductsStart().observe(viewLifecycleOwner,
            { t ->
                Log.e("DBG:", "getProducts observed")
                bindingx!!.get().progressBar.visibility = View.VISIBLE
                adapter!!.get().insertData(t!!)
                bindingx!!.get().progressBar.visibility = View.GONE
            })
        activityViewModel.dataupdated?.observe(viewLifecycleOwner, { list ->
            // Update the list UI
            Log.e("DBG:", "dataupdated observed")
            categoriesViewModel.getProducts()
        })

        val recyclerWords: RecyclerView = bindingx!!.get().words
        val mLayoutManagerwords = LinearLayoutManager(context)
        recyclerWords.layoutManager = mLayoutManagerwords
        wordsadapter = CategoriesAdapter(R.layout.categories_list_item, recyclerWords)
        adapter = AutoClearedValue(this, wordsadapter!!)
        recyclerWords.adapter = adapter!!.get()

        val textInputCustomEndIcon: TextInputLayout = bindingx!!.get().textinputlayout
        adapter!!.get().setOnItemClickListener(object : OnCategoryItemClickListener {
            override fun onItemClick(obj: Category) {
                activityViewModel.getDialogs(false,obj)
            }
        })

        editText = _binding!!.edittext
        //  editText = TextInputEditText(fragmentDicRecyclerBinding!!.textinputlayout.getContext());
        //   editText = TextInputEditText(binding!!.get().textinputlayout.context)
        textInputCustomEndIcon.isEndIconVisible = false
        textInputCustomEndIcon.setEndIconOnClickListener {
            if (editText.text!!.isNotEmpty()) {
                editText.text = null
            }
        }



        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {

            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                textInputCustomEndIcon.isEndIconVisible = count > 0
                adapter!!.get().filter.filter(s.toString())
                //  Toast.makeText(context,s.toString(),Toast.LENGTH_SHORT).show()
            }
        })


        editText.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                // adapter!!.get().filter.filter(editText.text.toString())
                return@OnEditorActionListener true
            }
            false
        })
//        binding.buttonFirst.setOnClickListener {
//            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
//        }
    }

    override fun onResume() {
        super.onResume()
        //Toast.makeText(context,"RESUME",Toast.LENGTH_SHORT).show()

        bindingx!!.get().progressBar.visibility = View.VISIBLE
        categoriesViewModel.getProducts()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}