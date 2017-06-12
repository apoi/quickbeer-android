/**
 * This file is part of QuickBeer.
 * Copyright (C) 2016 Antti Poikela <antti.poikela@iki.fi>
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
package quickbeer.android.features.photoview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.squareup.picasso.Callback.EmptyCallback;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import quickbeer.android.R;
import quickbeer.android.core.activity.InjectingBaseActivity;

import static io.reark.reark.utils.Preconditions.get;

public class PhotoViewActivity extends InjectingBaseActivity {

    @Nullable
    @Inject
    Picasso picasso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);

        getComponent().inject(this);

        setContentView(R.layout.photo_view_activity);

        initImageView(getIntent().getStringExtra("source"));
    }

    @Override
    protected void inject() {
        getComponent().inject(this);
    }

    private void initImageView(@Nullable String source) {
        ImageView imageView = (ImageView) findViewById(R.id.photo_view);
        PhotoViewAttacher attacher = new PhotoViewAttacher(imageView);

        get(picasso)
                .load(get(source))
                .centerInside()
                .fit()
                .into(imageView, new EmptyCallback() {
                    @Override
                    public void onSuccess() {
                        attacher.update();
                    }
                });
    }
}
