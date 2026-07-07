package alex.valker91.lnd_demo_app.features

import javax.inject.Inject

class GetUserByIdUseCase @Inject constructor(
    private val userNetworkDataSource: UserNetworkDataSource
) {
    suspend fun execute(userId: Long): Result<UserResponse> {
        return userNetworkDataSource.getUserById(userId)
    }
}