package com.ssafy.smile.data.remote.model

import android.os.Parcelable
import com.ssafy.smile.common.util.CommonUtils
import com.ssafy.smile.domain.model.CustomPhotographerDomainDto
import kotlinx.parcelize.Parcelize
import java.io.File

@Parcelize
data class PhotographerResponseDto(
    val profileImg: File,
    val photographerDto : PhotographerDto
) : Parcelable


data class PhotographerByAddressResponseDto(
    val photographerId: Long = 0,
    val name: String = "",
    val profileImg: String? = "",
    val places: ArrayList<Place> = arrayListOf(),
    val categories: ArrayList<CategoryNoDes> = arrayListOf(),
    val hasHeart: Boolean = false,
    val heart: Int = 0,
    val score: Double = 0.0,
    val reviews: Int = 0
){
    fun toCustomPhotographerDomainDto(): CustomPhotographerDomainDto {
        val categoryNames = arrayListOf<String>()
        val categoryPrices = arrayListOf<Int>()
        categories.forEach { data ->
            categoryNames.add(data.name)
            categoryPrices.add(data.price.toInt())
        }

        val sPlaces = arrayListOf<String>()
        places.forEach { place ->
            sPlaces.add(place.place)
        }

        return CustomPhotographerDomainDto(
            photographerId,
            profileImg,
            CommonUtils.getCategoryName(categoryNames),
            name,
            CommonUtils.getPlace(sPlaces),
            CommonUtils.getCategoryPrice(categoryPrices),
            hasHeart,
            heart
        )
    }
}

data class CategoryNoDes(
    val name: String = "",
    val price: String = ""
)

data class Place (
    val place: String = ""
)