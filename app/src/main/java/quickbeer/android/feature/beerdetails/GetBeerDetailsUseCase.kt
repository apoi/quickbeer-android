package quickbeer.android.feature.beerdetails

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onStart
import quickbeer.android.data.repository.Accept
import quickbeer.android.data.repository.NoFetch
import quickbeer.android.data.state.State
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.repository.BeerRepository
import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.domain.brewer.repository.BrewerRepository
import quickbeer.android.domain.country.Country
import quickbeer.android.domain.country.repository.CountryRepository
import quickbeer.android.domain.rating.Rating
import quickbeer.android.domain.ratinglist.repository.UserRatingRepository
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
    private val userRatingRepository: UserRatingRepository
) {

    fun getBeerDetails(beerId: Int): Flow<State<BeerDetailsState>> {
        val beerFlow = beerRepository.getStream(beerId, Beer.DetailsDataValidator())
        val userFlow = currentUserRepository.getStream(NoFetch())
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
        }
    }

    private fun getBrewer(beer: Flow<State<Beer>>): Flow<State<Brewer>> {
        return beer
            .mapNotNull { it.valueOrNull()?.brewerId }
            .distinctUntilChanged()
            .flatMapLatest { brewerId ->
                brewerRepository.getStream(brewerId, Brewer.DetailsDataValidator())
            }
            .onStart { emit(State.Initial) }
            .distinctUntilChanged()
    }

    private fun getCountry(beer: Flow<State<Beer>>): Flow<State<Country>> {
        return beer
            .mapNotNull { it.valueOrNull()?.countryId }
            .distinctUntilChanged()
            .flatMapLatest { countryId ->
                countryRepository.getStream(countryId, Accept())
            }
            .onStart { emit(State.Initial) }
            .distinctUntilChanged()
    }

    private fun getAddress(
        brewer: Flow<State<Brewer>>,
        country: Flow<State<Country>>
    ): Flow<State<Address>> {
        return brewer
            .combine(country) { b, c -> mergeAddress(b, c) }
            .onStart { emit(State.Initial) }
            .distinctUntilChanged()
    }

    private fun mergeAddress(brewer: State<Brewer>, country: State<Country>): State<Address> {
        return if (brewer is State.Success && country is State.Success) {
            State.Success(Address.from(brewer.value, country.value))
        } else {
            State.Loading()
        }
    }

    private fun getRating(beerId: Int, user: Flow<State<User>>): Flow<RatingState<Rating>> {
        return user
            .combine(userRatingRepository.getStream(NoFetch())) { userState, ratingsState ->
                val userValue = userState.valueOrNull()
                val ratings = ratingsState.valueOrNull()
                val rating = ratings
                    ?.filter { it.beerId == beerId }
                    ?.maxByOrNull(Rating::isDraft)
                when {
                    userValue != null && rating != null -> RatingState.ShowRating(rating)
                    userValue != null -> RatingState.ShowAction
                    else -> RatingState.Hide
                }
            }
            .distinctUntilChanged()
    }

    private fun getTick(beer: Flow<State<Beer>>, user: Flow<State<User>>): Flow<RatingState<Tick>> {
        return beer
            .combine(user) { beerState, userState ->
                val userValue = userState.valueOrNull()
                val beerValue = beerState.valueOrNull()
                val tick = Tick.create(beerValue)
                when {
                    userValue != null && tick.tick != null -> RatingState.ShowRating(tick)
                    userValue != null -> RatingState.ShowAction
                    else -> RatingState.Hide
                }
            }
            .distinctUntilChanged()
    }

    private fun getStyle(beer: Flow<State<Beer>>): Flow<State<Style>> {
        return beer
            .mapNotNull { it.valueOrNull() }
            .mapNotNull {
                when {
                    it.styleId != null -> Pair(it.styleId, null)
                    it.styleName != null -> Pair(null, it.styleName)
                    else -> null
                }
            }
            .distinctUntilChanged()
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
