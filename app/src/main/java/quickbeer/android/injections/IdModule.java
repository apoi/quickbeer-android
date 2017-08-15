package quickbeer.android.injections;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class IdModule {

    private final int primaryId;
    private final int secondaryId;

    public IdModule(int id) {
        this.primaryId = id;
        this.secondaryId = -1;
    }

    public IdModule(int primaryId, int secondaryId) {
        this.primaryId = primaryId;
        this.secondaryId = secondaryId;
    }

    @Provides
    @Named("id")
    Integer provideId() {
        return primaryId;
    }

    @Provides
    @Named("secondaryId")
    Integer provideSecondaryId() {
        return secondaryId;
    }

}
