package alex.valker91.lnd_demo_app.features

import javax.inject.Inject

class UserNetworkDataSource @Inject constructor(
    private val userApiService: UserApiService
) {
    suspend fun getUserById(userId: Long): Result<UserResponse> {
        return try {
            val user = userApiService.getUserById(userId)
            Result.Success(user)
        } catch (ex: Exception) {
            Result.Error(ex)
        }
    }
}