package quickbeer.android.injections;

import dagger.Subcomponent;
import quickbeer.android.features.beerdetails.BeerDetailsFragment;
import quickbeer.android.features.beerdetails.BeerReviewsFragment;
import quickbeer.android.features.brewerdetails.BrewerDetailsFragment;
import quickbeer.android.features.countrydetails.CountryDetailsBeersFragment;
import quickbeer.android.features.countrydetails.CountryDetailsFragment;
import quickbeer.android.features.styledetails.StyleDetailsBeersFragment;
import quickbeer.android.features.styledetails.StyleDetailsFragment;

@Subcomponent(modules = IdModule.class)
public interface IdComponent {

    void inject(BeerDetailsFragment beerDetailsFragment);

    void inject(BrewerDetailsFragment brewerDetailsFragment);

    void inject(BeerReviewsFragment beerReviewsFragment);

    void inject(StyleDetailsBeersFragment styleDetailsBeersFragment);

    void inject(StyleDetailsFragment styleDetailsFragment);

    void inject(CountryDetailsBeersFragment styleDetailsFragment);

    void inject(CountryDetailsFragment countryDetailsFragment);

}
