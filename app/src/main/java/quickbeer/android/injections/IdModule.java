package quickbeer.android.injections;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class IdModule {

    private final int id;

    public IdModule(int id) {
        this.id = id;
    }

    @Provides
    @Named("id")
    Integer provideId() {
        return id;
    }

}
