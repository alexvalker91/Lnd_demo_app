package alex.valker91.lnd_demo_app.features

import retrofit2.http.GET
import retrofit2.http.Path

interface UserApiService {

    @GET("api/v1/users/{userId}")
    suspend fun getUserById(
        @Path("userId") userId: Long
    ): UserResponse
}