package quickbeer.android.injections;

import dagger.Subcomponent;
import quickbeer.android.features.list.fragments.BeersInCountryFragment;
import quickbeer.android.features.list.fragments.BeersInStyleFragment;
import quickbeer.android.features.styledetails.StyleDetailsFragment;

@Subcomponent(modules = IdModule.class)
public interface IdComponent {

    void inject(BeersInCountryFragment beersInCountryFragment);

    void inject(BeersInStyleFragment beersInStyleFragment);

    void inject(StyleDetailsFragment styleDetailsFragment);

}
