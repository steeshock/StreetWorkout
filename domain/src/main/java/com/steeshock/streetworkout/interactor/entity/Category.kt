package com.steeshock.streetworkout.interactor.entity

data class Category(
    var categoryId: Int? = null,
    var categoryName: String = "",
    var isSelected: Boolean? = false,
) {
    fun changeSelectedState() {
        if (this.isSelected == null) {
            this.isSelected = true
        } else {
            this.isSelected = !this.isSelected!!
        }
    }

    companion object {
        const val TABLE_NAME = "categories_table"
    }
}