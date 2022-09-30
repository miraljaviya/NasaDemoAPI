package spartons.com.koinmvvm.backend

import retrofit2.http.GET
import retrofit2.http.Query
import spartons.com.koinmvvm.activities.NeoFeed


/**
 * Ahsen Saeed}
 * ahsansaeed067@gmail.com}
 * 10/24/19}
 */

interface ServiceUtil {


    @GET("feed")
    suspend fun retrieveNearEarthObjects(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("api_key") apiKey: String
    ): NeoFeed


}