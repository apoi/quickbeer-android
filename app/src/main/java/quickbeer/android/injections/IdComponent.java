package quickbeer.android.injections;

import dagger.Subcomponent;
import quickbeer.android.features.list.fragments.BeersInCountryFragment;
import quickbeer.android.features.list.fragments.BeersInStyleFragment;

@Subcomponent(modules = IdModule.class)
public interface IdComponent {

    void inject(BeersInCountryFragment beersInCountryFragment);

    void inject(BeersInStyleFragment beersInStyleFragment);

}
