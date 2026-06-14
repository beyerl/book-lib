package com.lspace.booklib.data.remote

import com.lspace.booklib.data.remote.dto.SearchResponseDto
import com.lspace.booklib.data.remote.dto.WorkDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * OpenLibrary HTTP API — the same open book source family BookWyrm connects to.
 * No API key required.
 */
interface OpenLibraryApi {

    @GET("search.json")
    suspend fun search(
        @Query("q") query: String,
        @Query("limit") limit: Int = 25,
        @Query("fields") fields: String = DEFAULT_FIELDS,
    ): SearchResponseDto

    /** [workId] is the bare work id, e.g. "OL45883W". */
    @GET("works/{workId}.json")
    suspend fun getWork(@Path("workId") workId: String): WorkDto

    companion object {
        const val BASE_URL = "https://openlibrary.org/"
        const val COVERS_BASE_URL = "https://covers.openlibrary.org/b/id/"
        const val DEFAULT_FIELDS =
            "key,title,author_name,first_publish_year,cover_i,isbn,number_of_pages_median"

        fun coverUrl(coverId: Long?, size: Char = 'M'): String? =
            coverId?.let { "$COVERS_BASE_URL$it-$size.jpg" }
    }
}
