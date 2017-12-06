/*
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
package quickbeer.android.features.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.about_details_fragment.*
import quickbeer.android.BuildConfig
import quickbeer.android.R
import quickbeer.android.analytics.Analytics
import quickbeer.android.analytics.Events.LaunchAction
import quickbeer.android.core.fragment.BaseFragment
import javax.inject.Inject

class AboutDetailsFragment : BaseFragment() {

    @Inject
    internal lateinit var analytics: Analytics

    override fun inject() {
        getComponent().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.about_details_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        about_version.text = String.format(getString(R.string.about_version), BuildConfig.VERSION_NAME)
        about_spice_program.setOnClickListener { openUri(LaunchAction.ABOUT_SPICE_PROGRAM, "https://spiceprogram.org") }
        about_iiro.setOnClickListener { openUri(LaunchAction.ABOUT_IIRO, "http://iiroisotalo.com") }

        about_google_play.setOnClickListener { openUri(LaunchAction.ABOUT_GOOGLE_PLAY, "https://play.google.com/store/apps/details?id=quickbeer.android") }
        about_source_row.setOnClickListener { openUri(LaunchAction.ABOUT_SOURCE, "https://github.com/apoi/quickbeer-next") }
        about_application_license_row.setOnClickListener { openUri(LaunchAction.ABOUT_LICENSE, "https://ztesch.fi/quickbeer/license") }
        about_library_licenses_row.setOnClickListener { openUri(LaunchAction.ABOUT_OPEN_SOURCE, "https://ztesch.fi/quickbeer/open-source") }
        about_assets_row.setOnClickListener { openUri(LaunchAction.ABOUT_GRAPHICS_ASSETS, "https://ztesch.fi/quickbeer/graphics-assets") }
        about_privacy_policy_row.setOnClickListener { openUri(LaunchAction.ABOUT_PRIVACY_POLICY, "https://ztesch.fi/quickbeer/privacy-policy") }
    }

    private fun openUri(action: LaunchAction, uri: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        context!!.startActivity(intent)

        analytics.createEvent(action)
    }
}
