package com.example.mobiledger.domain.entities

data class CategoryListEntity(
    val uid: String,
    val incomeCategoryList: IncomeCategoryListEntity,
    val expenseCategoryList: ExpenseCategoryListEntity
)

data class IncomeCategoryListEntity(
    val incomeCategoryList: List<String>
) {
    constructor() : this(incomeCategoryList = emptyList())
}

data class ExpenseCategoryListEntity(
    val expenseCategoryList: List<String>
) {
    constructor() : this(expenseCategoryList = emptyList())
}