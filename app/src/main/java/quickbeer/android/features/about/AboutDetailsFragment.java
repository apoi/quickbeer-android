/**
 * This file is part of QuickBeer.
 * Copyright (C) 2017 Antti Poikela <antti.poikela@iki.fi>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package quickbeer.android.features.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.Unbinder;
import polanski.option.AtomicOption;
import quickbeer.android.BuildConfig;
import quickbeer.android.R;
import quickbeer.android.analytics.Analytics;
import quickbeer.android.analytics.Events.LaunchAction;
import quickbeer.android.core.fragment.BaseFragment;

import static butterknife.ButterKnife.bind;
import static io.reark.reark.utils.Preconditions.get;

public class AboutDetailsFragment extends BaseFragment {

    @BindView(R.id.about_version)
    TextView aboutVersion;

    @BindView(R.id.about_spice_program)
    View aboutChilicorn;

    @BindView(R.id.about_iiro)
    View aboutIiro;

    @BindView(R.id.about_source_row)
    View sourceRow;

    @BindView(R.id.about_application_license_row)
    View applicationLicenseRow;

    @BindView(R.id.about_library_licenses_row)
    View libraryLicensesRow;

    @BindView(R.id.about_assets_row)
    View assetsRow;

    @BindView(R.id.about_privacy_policy_row)
    View privacyRow;

    @Nullable
    @Inject
    Analytics analytics;

    @NonNull
    private final AtomicOption<Unbinder> unbinder = new AtomicOption<>();

    @Override
    protected void inject() {
        getComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.about_details_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        unbinder.setIfNone(bind(this, view));

        aboutVersion.setText(String.format(getString(R.string.about_version), BuildConfig.VERSION_NAME));

        aboutChilicorn.setOnClickListener(__ -> openUri(LaunchAction.ABOUT_SPICE_PROGRAM, "https://spiceprogram.org"));
        aboutIiro.setOnClickListener(__ -> openUri(LaunchAction.ABOUT_IIRO, "http://iiroisotalo.com"));
        sourceRow.setOnClickListener(__ -> openUri(LaunchAction.ABOUT_SOURCE, "https://github.com/apoi/quickbeer-next"));
        applicationLicenseRow.setOnClickListener(__ -> openUri(LaunchAction.ABOUT_LICENSE, "https://ztesch.fi/quickbeer/license"));
        libraryLicensesRow.setOnClickListener(__ -> openUri(LaunchAction.ABOUT_OPEN_SOURCE, "https://ztesch.fi/quickbeer/open-source"));
        assetsRow.setOnClickListener(__ -> openUri(LaunchAction.ABOUT_GRAPHICS_ASSETS, "https://ztesch.fi/quickbeer/graphics-assets"));
        privacyRow.setOnClickListener(__ -> openUri(LaunchAction.ABOUT_PRIVACY_POLICY, "https://ztesch.fi/quickbeer/privacy-policy"));
    }

    @Override
    public void onDestroyView() {
        unbinder.getAndClear()
                .ifSome(Unbinder::unbind);
        super.onDestroyView();
    }

    private void openUri(@NonNull LaunchAction action, @NonNull String uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        getContext().startActivity(intent);

        get(analytics).createEvent(action);
    }

}
