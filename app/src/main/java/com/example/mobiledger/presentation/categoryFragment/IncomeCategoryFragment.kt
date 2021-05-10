import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseFragment
import com.example.mobiledger.common.base.BaseNavigator
import com.example.mobiledger.databinding.FragmentIncomeCategoryBinding
import com.example.mobiledger.databinding.SnackViewErrorBinding
import com.example.mobiledger.presentation.OneTimeObserver
import com.example.mobiledger.presentation.categoryFragment.IncomeCategoryViewModel
import com.example.mobiledger.presentation.categoryFragment.adapter.CategoryAdapter
import com.example.mobiledger.presentation.recordtransaction.AddTransactionDialogFragmentViewModel

class IncomeCategoryFragment : BaseFragment<FragmentIncomeCategoryBinding, BaseNavigator>(R.layout.fragment_income_category) {

    private val viewModel: IncomeCategoryViewModel by viewModels { viewModelFactory }

    override fun getSnackBarErrorView(): SnackViewErrorBinding = viewBinding.includeIncomeErrorView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObserver()
        viewModel.getIncomeCategoryList()
    }

    private fun setUpObserver() {
        viewModel.incomeCategoryList.observe(viewLifecycleOwner, OneTimeObserver {
            initRecyclerView(it.incomeCategoryList)
        })

        viewModel.loadingState.observe(viewLifecycleOwner, Observer {
            if (it) {
                viewBinding.incomeCategoryProgressBar.visibility = View.VISIBLE
            } else {
                viewBinding.incomeCategoryProgressBar.visibility = View.GONE
            }
        })

        viewModel.errorLiveData.observe(viewLifecycleOwner, OneTimeObserver {
            when (it.viewErrorType) {
                AddTransactionDialogFragmentViewModel.ViewErrorType.NON_BLOCKING -> {
                    showSnackBarErrorView(it.message ?: getString(it.resID), true)
                }
            }
        })
    }

    private fun initRecyclerView(incomeCategoryList: List<String>) {
        val incomeCategoryAdapter = CategoryAdapter(incomeCategoryList)
        val linearLayoutManager = LinearLayoutManager(activity)
        viewBinding.rvIncomeCategory.apply {
            layoutManager = linearLayoutManager
            adapter = incomeCategoryAdapter
        }
    }

    companion object {
        fun newInstance() = IncomeCategoryFragment()
    }
}