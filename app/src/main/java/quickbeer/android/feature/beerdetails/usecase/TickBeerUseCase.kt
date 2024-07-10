package quickbeer.android.feature.beerdetails.usecase

import javax.inject.Inject
import org.threeten.bp.ZonedDateTime
import quickbeer.android.data.result.Result
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.network.BeerTickFetcher
import quickbeer.android.domain.beer.repository.BeerRepository
import quickbeer.android.domain.user.User
import quickbeer.android.domain.user.repository.CurrentUserRepository
import quickbeer.android.network.result.ApiResult

class TickBeerUseCase @Inject constructor(
    private val beerRepository: BeerRepository,
    private val beerTickFetcher: BeerTickFetcher,
    private val currentUserRepository: CurrentUserRepository
) {

    suspend fun tickBeer(beerId: Int, tick: Int): Result<Unit> {
        val beer = beerRepository.get(beerId) ?: error("Error getting beer")
        val user = currentUserRepository.get() ?: error("Not logged in")
        val result = tickBeer(user, beerId, tick)

        if (result is Result.Success) {
            updateBeer(beer, tick)
        }

        return result
    }

    private suspend fun tickBeer(user: User, beerId: Int, tick: Int): Result<Unit> {
        val fetchKey = BeerTickFetcher.TickKey(beerId, user.id, tick)
        val result = when (val apiResult = beerTickFetcher.fetch(fetchKey)) {
            is ApiResult.Success -> Result.Success(Unit)
            is ApiResult.HttpError -> Result.Failure(apiResult.cause)
            is ApiResult.NetworkError -> Result.Failure(apiResult.cause)
            is ApiResult.UnknownError -> Result.Failure(apiResult.cause)
        }

        return result
    }

    private suspend fun updateBeer(beer: Beer, tick: Int) {
        // Tick isn't part of regular beer JSON, so we can't just fetch the updated data.
        // Instead, do a local update. Remote fetch updates via the tick listing.
        val update = beer.copy(tickValue = tick, tickDate = ZonedDateTime.now())
        beerRepository.persist(beer.id, update)
    }
}
