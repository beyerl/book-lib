package com.lspace.booklib.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResponseDto(
    @SerialName("numFound") val numFound: Int = 0,
    @SerialName("docs") val docs: List<SearchDocDto> = emptyList(),
)

@Serializable
data class SearchDocDto(
    @SerialName("key") val key: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("author_name") val authorNames: List<String> = emptyList(),
    @SerialName("first_publish_year") val firstPublishYear: Int? = null,
    @SerialName("cover_i") val coverId: Long? = null,
    @SerialName("isbn") val isbns: List<String> = emptyList(),
    @SerialName("number_of_pages_median") val pageCountMedian: Int? = null,
)
