package quickbeer.android.injections;

import dagger.Subcomponent;
import quickbeer.android.features.countrydetails.CountryDetailsBeersFragment;
import quickbeer.android.features.countrydetails.CountryDetailsFragment;
import quickbeer.android.features.styledetails.StyleDetailsBeersFragment;
import quickbeer.android.features.styledetails.StyleDetailsFragment;

@Subcomponent(modules = IdModule.class)
public interface IdComponent {

    void inject(StyleDetailsBeersFragment styleDetailsBeersFragment);

    void inject(StyleDetailsFragment styleDetailsFragment);

    void inject(CountryDetailsBeersFragment styleDetailsFragment);

    void inject(CountryDetailsFragment countryDetailsFragment);

}
