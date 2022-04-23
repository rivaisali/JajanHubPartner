package com.arajangstudio.jajanhub_partner.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.arajangstudio.jajanhub_partner.data.remote.RemoteDataSource
import com.arajangstudio.jajanhub_partner.data.remote.models.Review
import com.arajangstudio.jajanhub_partner.utils.Constants
import retrofit2.HttpException
import java.io.IOException

class ReviewDataSource(private val remoteDataSource: RemoteDataSource, private val merchant_id: String) :
    PagingSource<Int, Review>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Review> {
        return try {
            val nextPage = params.key ?: Constants.STARTING_PAGE_INDEX
            val response = remoteDataSource.getListReviews(merchant_id, nextPage)
            LoadResult.Page(
                data = response.results,
                prevKey = if (nextPage == 1) null else nextPage - 1,
                nextKey = if (response.results.isNullOrEmpty()) null else nextPage + 1
            )

        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }

    }

    override fun getRefreshKey(state: PagingState<Int, Review>): Int? {
        return state.anchorPosition
    }
}