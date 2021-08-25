package com.example.mobiledger.common.utils

import androidx.fragment.app.FragmentManager
import com.example.mobiledger.common.extention.getName
import com.example.mobiledger.domain.entities.NewBudgetTemplateEntity
import com.example.mobiledger.domain.entities.TransactionEntity
import com.example.mobiledger.domain.enums.TransactionDetailScreenSource
import com.example.mobiledger.presentation.addtransaction.AddTransactionDialogFragment
import com.example.mobiledger.presentation.auth.ForgetPasswordDialogFragment
import com.example.mobiledger.presentation.budget.MonthlyBudgetData
import com.example.mobiledger.presentation.budget.addbudget.AddBudgetDialogFragment
import com.example.mobiledger.presentation.budget.addbudget.applyTemplate.ApplyTemplateDialogFragment
import com.example.mobiledger.presentation.budget.updatebudget.UpdateBudgetDialogFragment
import com.example.mobiledger.presentation.budgetTemplate.AddBudgetTemplateDialogFragment
import com.example.mobiledger.presentation.budgetTemplate.EditBudgetTemplateDialogFragment
import com.example.mobiledger.presentation.categoryFragment.AddCategoryDialogFragment
import com.example.mobiledger.presentation.onBoarding.TermsAndConditionFragment
import com.example.mobiledger.presentation.profile.profilePicUpdateDialog.ProfilePicUpdateDialogFragment
import com.example.mobiledger.presentation.transactiondetail.TransactionDetailDialogFragment
import java.util.*

fun showAddTransactionDialogFragment(fragmentManager: FragmentManager) {
    val dialog = AddTransactionDialogFragment.newInstance()
    dialog.isCancelable = true
    dialog.show(
        fragmentManager,
        dialog.getName()
    )
}

fun showTransactionDetailDialogFragment(
    transactionEntity: TransactionEntity,
    fragmentManager: FragmentManager,
    sourceScreen: TransactionDetailScreenSource
) {
    val dialog = TransactionDetailDialogFragment.newInstance(transactionEntity, sourceScreen)
    dialog.isCancelable = true
    dialog.show(
        fragmentManager,
        dialog.getName()
    )
}

fun showAddCategoryDialogFragment(fragmentManager: FragmentManager, list: List<String>, isIncome: Boolean) {
    val dialog = AddCategoryDialogFragment.newInstance(list, isIncome)
    dialog.isCancelable = true
    dialog.show(
        fragmentManager,
        dialog.getName()
    )
}

fun showAddBudgetDialogFragment(
    fragmentManager: FragmentManager,
    monthlyLimit: Long,
    list: ArrayList<String>,
    month: String,
    budgetTotal: Long,
    purpose: String
) {
    val dialog = AddBudgetDialogFragment.newInstance(monthlyLimit, list, month, budgetTotal, purpose)
    dialog.show(
        fragmentManager,
        dialog.getName()
    )
}

fun showUpdateBudgetDialogFragment(
    fragmentManager: FragmentManager,
    month: Calendar,
    category: String,
    categoryBudget: Long,
    monthlyBudgetData: MonthlyBudgetData
) {
    val dialog = UpdateBudgetDialogFragment.newInstance(month, category, categoryBudget, monthlyBudgetData)
    dialog.show(
        fragmentManager,
        dialog.getName()
    )
}

fun showAddNewTemplateDialogFragment(
    fragmentManager: FragmentManager,
    templateList: MutableList<NewBudgetTemplateEntity>,
) {
    val dialog = AddBudgetTemplateDialogFragment.newInstance(templateList)
    dialog.show(
        fragmentManager,
        dialog.getName()
    )
}

fun showEditBudgetTemplateDialogFragment(
    fragmentManager: FragmentManager,
    templateId: String,
    list: ArrayList<String>,
    category: String,
    oldBudget: Long,
    totalBudget: Long,
    maxLimit: Long,
    isAddCategory: Boolean,
    isUpdateMaxLimit: Boolean
) {
    val dialog = EditBudgetTemplateDialogFragment.newInstance(
        templateId,
        isAddCategory,
        category,
        oldBudget,
        list,
        maxLimit,
        totalBudget,
        isUpdateMaxLimit
    )
    dialog.show(
        fragmentManager,
        dialog.getName()
    )
}

fun showApplyTemplateDialogFragment(
    fragmentManager: FragmentManager,
    month: String
) {
    val dialog = ApplyTemplateDialogFragment.newInstance(month)
    dialog.show(
        fragmentManager,
        dialog.getName()
    )
}

fun showForgetPasswordDialog(
    fragmentManager: FragmentManager,
) {
    val dialog = ForgetPasswordDialogFragment.newInstance()
    dialog.show(
        fragmentManager,
        dialog.getName()
    )
}

fun showTermsAndConditionDialogFragment(
    fragmentManager: FragmentManager,
) {
    val dialog = TermsAndConditionFragment.newInstance()
    dialog.show(
        fragmentManager,
        dialog.getName()
    )
}

fun showUpdateProfilePicDialogFragment(
    fragmentManager: FragmentManager,
) {
    val dialog = ProfilePicUpdateDialogFragment.newInstance()
    dialog.show(
        fragmentManager,
        dialog.getName()
    )
}
