package quickbeer.android.feature.beerdetails.usecase

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onStart
import quickbeer.android.data.repository.Accept
import quickbeer.android.data.repository.FetchIfNull
import quickbeer.android.data.repository.NoFetch
import quickbeer.android.data.state.State
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.repository.BeerRepository
import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.domain.brewer.repository.BrewerRepository
import quickbeer.android.domain.country.Country
import quickbeer.android.domain.country.repository.CountryRepository
import quickbeer.android.domain.rating.Rating
import quickbeer.android.domain.ratinglist.repository.UserBeerRatingRepository
import quickbeer.android.domain.style.Style
import quickbeer.android.domain.style.repository.StyleRepository
import quickbeer.android.domain.stylelist.repository.StyleListRepository
import quickbeer.android.domain.user.User
import quickbeer.android.domain.user.repository.CurrentUserRepository
import quickbeer.android.feature.beerdetails.model.Address
import quickbeer.android.feature.beerdetails.model.BeerDetailsState
import quickbeer.android.feature.beerdetails.model.RatingState
import quickbeer.android.feature.beerdetails.model.Tick
import quickbeer.android.util.coroutines.combine

class GetBeerDetailsUseCase @Inject constructor(
    private val currentUserRepository: CurrentUserRepository,
    private val beerRepository: BeerRepository,
    private val brewerRepository: BrewerRepository,
    private val styleRepository: StyleRepository,
    private val styleListRepository: StyleListRepository,
    private val countryRepository: CountryRepository,
    private val userBeerRatingRepository: UserBeerRatingRepository
) {

    fun getBeerDetails(beerId: Int): Flow<State<BeerDetailsState>> {
        val beerFlow = beerRepository
            .getStream(beerId, Beer.DetailsDataValidator())
            .distinctUntilChanged()

        val userFlow = currentUserRepository
            .getStream(NoFetch())
            .distinctUntilChanged()

        val brewerFlow = getBrewer(beerFlow)
        val countryFlow = getCountry(beerFlow)
        val addressFlow = getAddress(brewerFlow, countryFlow)
        val styleFlow = getStyle(beerFlow)
        val ratingFlow = getRating(beerId, userFlow)
        val tickFlow = getTick(beerFlow, userFlow)

        return combine(
            beerFlow,
            brewerFlow,
            styleFlow,
            addressFlow,
            userFlow,
            ratingFlow,
            tickFlow
        ) { beer, brewer, style, address, user, rating, tick ->
            val v = BeerDetailsState.create(beer, brewer, style, address, user, rating, tick)
            if (v != null) State.from(v) else State.Initial
        }.distinctUntilChanged()
    }

    private fun getBrewer(beerFlow: Flow<State<Beer>>): Flow<State<Brewer>> {
        return beerFlow
            .mapNotNull { it.valueOrNull()?.brewerId }
            .flatMapLatest { brewerId ->
                brewerRepository.getStream(brewerId, Brewer.DetailsDataValidator())
            }
            .onStart { emit(State.Initial) }
    }

    private fun getCountry(beerFlow: Flow<State<Beer>>): Flow<State<Country>> {
        return beerFlow
            .mapNotNull { it.valueOrNull()?.countryId }
            .flatMapLatest { countryId ->
                countryRepository.getStream(countryId, Accept())
            }
            .onStart { emit(State.Initial) }
    }

    private fun getAddress(
        brewerFlow: Flow<State<Brewer>>,
        countryFlow: Flow<State<Country>>
    ): Flow<State<Address>> {
        return brewerFlow
            .combine(countryFlow) { b, c -> mergeAddress(b, c) }
            .onStart { emit(State.Initial) }
    }

    private fun mergeAddress(
        brewerFlow: State<Brewer>,
        countryFlow: State<Country>
    ): State<Address> {
        return if (brewerFlow is State.Success && countryFlow is State.Success) {
            State.Success(Address.from(brewerFlow.value, countryFlow.value))
        } else {
            State.Loading()
        }
    }

    private fun getRating(beerId: Int, userFlow: Flow<State<User>>): Flow<RatingState<Rating>> {
        return userFlow
            .map { it.valueOrNull() }
            .flatMapLatest { user ->
                if (user == null) {
                    flowOf(RatingState.Hide)
                } else {
                    getRating(user, beerId)
                }
            }
    }

    private fun getRating(user: User, beerId: Int): Flow<RatingState<Rating>> {
        return userBeerRatingRepository.getStream(Pair(user, beerId), FetchIfNull())
            .map { it.valueOrNull() }
            .map { ratingValue ->
                when {
                    ratingValue != null -> RatingState.ShowRating(ratingValue)
                    else -> RatingState.ShowAction
                }
            }
    }

    private fun getTick(
        beerFlow: Flow<State<Beer>>,
        userFlow: Flow<State<User>>
    ): Flow<RatingState<Tick>> {
        return beerFlow
            .combine(userFlow) { beerState, userState ->
                val user = userState.valueOrNull()
                val beer = beerState.valueOrNull()
                val tick = Tick.create(beer)
                when {
                    user != null && tick.tick != null -> RatingState.ShowRating(tick)
                    user != null -> RatingState.ShowAction
                    else -> RatingState.Hide
                }
            }
    }

    private fun getStyle(beerFlow: Flow<State<Beer>>): Flow<State<Style>> {
        return beerFlow
            .mapNotNull { it.valueOrNull() }
            .mapNotNull {
                when {
                    it.styleId != null -> Pair(it.styleId, null)
                    it.styleName != null -> Pair(null, it.styleName)
                    else -> null
                }
            }
            .flatMapLatest { (styleId, styleName) ->
                when {
                    styleId != null -> getStyle(styleId)
                    styleName != null -> getStyle(styleName)
                    else -> error("Invalid style state")
                }
            }
    }

    private fun getStyle(styleId: Int): Flow<State<Style>> {
        return styleRepository.getStream(styleId, Accept())
    }

    private fun getStyle(styleName: String): Flow<State<Style>> {
        return styleListRepository.getStream(Accept())
            .mapNotNull { it.valueOrNull() }
            .mapNotNull { styles -> styles.firstOrNull { it.name == styleName } }
            .flatMapLatest { getStyle(it.id) }
    }
}
