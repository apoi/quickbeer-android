package quickbeer.android.domain.user.store

import quickbeer.android.domain.user.User
import quickbeer.android.util.Mapper

object UserEntityMapper : Mapper<User, UserEntity> {

    override fun mapFrom(source: User): UserEntity {
        return UserEntity(
            id = source.id,
            username = source.username,
            password = source.password,
            loggedIn = source.loggedIn,
            countryId = source.countryId,
            rateCount = source.rateCount,
            tickCount = source.tickCount,
            placeCount = source.placeCount
        )
    }

    override fun mapTo(source: UserEntity): User {
        return User(
            id = source.id,
            username = source.username,
            password = source.password,
            loggedIn = source.loggedIn,
            countryId = source.countryId,
            rateCount = source.rateCount,
            tickCount = source.tickCount,
            placeCount = source.placeCount
        )
    }
}
