package com.steeshock.streetworkout.data.mappers

import com.steeshock.streetworkout.data.repository.dto.CategoryDto
import com.steeshock.streetworkout.interactor.entity.Category

fun CategoryDto.mapToEntity(): Category {
    return Category(
        categoryId = this.categoryId,
        categoryName = this.categoryName,
        isSelected = this.isSelected,
    )
}
fun Category.mapToDto(): CategoryDto {
    return CategoryDto(
        categoryId = this.categoryId,
        categoryName = this.categoryName,
        isSelected = this.isSelected,
    )
}