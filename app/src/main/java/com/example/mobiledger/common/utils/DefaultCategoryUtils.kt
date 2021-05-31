package com.example.mobiledger.common.utils

import com.example.mobiledger.R
import com.example.mobiledger.domain.enums.TransactionType

object DefaultCategoryUtils {
    // Expense Tags
    private const val EXP_FOOD = "Food"
    private const val EXP_BILLS = "Bills"
    private const val EXP_TRANSPORTATION = "Transportation"
    private const val EXP_HOME = "Home"
    private const val EXP_CAR = "Car"
    private const val EXP_ENTERTAINMENT = "Entertainment"
    private const val EXP_SHOPPING = "Shopping"
    private const val EXP_INSURANCE = "Insurance"
    private const val EXP_TAX = "Tax"
    private const val EXP_TELEPHONE = "Telephone"
    private const val EXP_HEALTH = "Health"
    private const val EXP_SPORT = "Sport"
    private const val EXP_PET = "Pet"
    private const val EXP_BABY = "Baby"
    private const val EXP_BEAUTY = "Beauty"
    private const val EXP_ELECTRONICS = "Electronics"
    private const val EXP_ALCOHOL = "Alcohol"
    private const val EXP_VEGETABLE = "Vegetable"
    private const val EXP_GROCERIES = "Groceries"
    private const val EXP_SOCIAL = "Social"
    private const val EXP_EDUCATION = "Education"
    private const val EXP_TRAVEL = "Travel"
    private const val EXP_BOOKS = "Books"
    private const val EXP_OFFICE = "Office"
    const val EXP_OTHERS = "Other Expenses"

    // Income Tags
    private const val INC_SALARY = "Salary"
    private const val INC_AWARDS = "Awards"
    private const val INC_SALE = "Sale"
    private const val INC_RENTAL = "Rental"
    private const val INC_REFUND = "Refund"
    private const val INC_COUPONS = "Coupons"
    private const val INC_INVESTMENT = "Investment"
    const val INC_OTHERS = "Other Incomes"

    fun getDefaultExpenseList() = listOf(EXP_FOOD, EXP_BILLS, EXP_TRANSPORTATION, EXP_HOME, EXP_CAR, EXP_ENTERTAINMENT, EXP_SHOPPING,
        EXP_INSURANCE, EXP_TAX, EXP_TELEPHONE, EXP_HEALTH, EXP_SPORT, EXP_PET, EXP_BABY, EXP_BEAUTY, EXP_ELECTRONICS, EXP_ALCOHOL,
        EXP_VEGETABLE, EXP_GROCERIES, EXP_SOCIAL, EXP_EDUCATION, EXP_TRAVEL, EXP_BOOKS, EXP_OFFICE, EXP_OTHERS)

    fun getDefaultIncomeList() = listOf(INC_SALARY, INC_AWARDS, INC_SALE, INC_RENTAL, INC_REFUND, INC_COUPONS, INC_INVESTMENT, INC_OTHERS)

    fun getOtherCategoryName(transactionType: TransactionType) =
        if(transactionType == TransactionType.Expense) EXP_OTHERS
        else INC_OTHERS

    fun getCategoryIcon(category: String, transactionType: TransactionType) =
        when(category){
            EXP_BABY -> R.drawable.exp_baby_icon
            EXP_ALCOHOL -> R.drawable.exp_alcohol_icon
            EXP_BEAUTY -> R.drawable.exp_beauty_icon
            EXP_BILLS -> R.drawable.exp_bills_icon
            EXP_BOOKS -> R.drawable.exp_books_icon
            EXP_CAR -> R.drawable.exp_car_icon
            EXP_EDUCATION -> R.drawable.exp_education_icon
            EXP_ELECTRONICS -> R.drawable.exp_electronics_icon
            EXP_ENTERTAINMENT -> R.drawable.exp_entertain_icon
            EXP_FOOD -> R.drawable.exp_food_icon
            EXP_GROCERIES -> R.drawable.exp_groceries_icon
            EXP_HEALTH -> R.drawable.exp_health_icon
            EXP_HOME -> R.drawable.exp_home_icon
            EXP_INSURANCE -> R.drawable.exp_insurance_icon
            EXP_OFFICE -> R.drawable.exp_office_icon
            EXP_PET -> R.drawable.exp_pet_icon
            EXP_SHOPPING -> R.drawable.exp_shopping_icon
            EXP_SOCIAL -> R.drawable.exp_social_icon
            EXP_SPORT -> R.drawable.exp_sports_icon
            EXP_TAX -> R.drawable.exp_tax_icon
            EXP_TELEPHONE -> R.drawable.exp_mobile_icon
            EXP_TRANSPORTATION -> R.drawable.exp_transport_icon
            EXP_TRAVEL -> R.drawable.exp_travel_icon
            EXP_VEGETABLE -> R.drawable.exp_vegetable_icon
            INC_AWARDS -> R.drawable.inc_awards_icon
            INC_COUPONS -> R.drawable.inc_coupan_icon
            INC_INVESTMENT -> R.drawable.inc_investment_icon
            INC_REFUND -> R.drawable.inc_refund_icon
            INC_RENTAL -> R.drawable.inc_rental_icon
            INC_SALE -> R.drawable.inc_sale_icon
            INC_SALARY -> R.drawable.inc_salary_icon
            EXP_OTHERS -> R.drawable.exp_others_icon
            INC_OTHERS -> R.drawable.inc_others_icon
            else -> {
                if(transactionType == TransactionType.Expense) R.drawable.exp_others_icon
                else R.drawable.inc_others_icon
            }
        }
}