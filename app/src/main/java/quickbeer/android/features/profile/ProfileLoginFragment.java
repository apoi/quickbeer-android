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
package quickbeer.android.features.profile;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.Unbinder;
import polanski.option.AtomicOption;
import quickbeer.android.R;
import quickbeer.android.analytics.Analytics;
import quickbeer.android.analytics.Events;
import quickbeer.android.core.fragment.BindingBaseFragment;
import quickbeer.android.core.viewmodel.DataBinder;
import quickbeer.android.core.viewmodel.SimpleDataBinder;
import quickbeer.android.providers.ToastProvider;
import quickbeer.android.utils.StringUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static butterknife.ButterKnife.bind;
import static io.reark.reark.utils.Preconditions.get;

public class ProfileLoginFragment extends BindingBaseFragment {

    @BindView(R.id.login_username)
    AutoCompleteTextView usernameView;

    @BindView(R.id.login_password)
    EditText passwordView;

    @BindView(R.id.login_progress_layout)
    View progressLayout;

    @BindView(R.id.login_progress)
    View progressView;

    @BindView(R.id.login_form)
    View loginFormView;

    @BindView(R.id.sign_in_button)
    Button signInButton;

    @Nullable
    @Inject
    ProfileLoginViewModel profileLoginViewModel;

    @Nullable
    @Inject
    ToastProvider toastProvider;

    @Nullable
    @Inject
    Analytics analytics;

    @NonNull
    private final AtomicOption<Unbinder> unbinder = new AtomicOption<>();

    @NonNull
    private final DataBinder dataBinder = new SimpleDataBinder() {
        @Override
        public void bind(@NonNull CompositeSubscription subscription) {
            // Progress indicator during login
            subscription.add(viewModel()
                    .isLoginInProgress()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ProfileLoginFragment.this::showProgress, Timber::e));

            // Error toasts
            subscription.add(viewModel()
                    .errorStream()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ProfileLoginFragment.this::showError, Timber::e));
        }
    };

    @Override
    protected void inject() {
        getComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_login_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        unbinder.setIfNone(bind(this, view));

        passwordView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == R.id.login || id == EditorInfo.IME_NULL) {
                attemptLogin();
                return true;
            }
            return false;
        });

        signInButton.setOnClickListener(__ -> attemptLogin());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        get(analytics).createEvent(Events.Screen.PROFILE_LOGIN);
    }

    @Override
    public void onDestroyView() {
        unbinder.getAndClear()
                .ifSome(Unbinder::unbind);
        super.onDestroyView();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        usernameView.setError(null);
        passwordView.setError(null);

        // Store values at the time of the login attempt.
        String username = usernameView.getText().toString();
        String password = passwordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (StringUtils.isEmpty(password)) {
            passwordView.setError(getString(R.string.error_field_required));
            focusView = passwordView;
            cancel = true;
        }

        // Check for a valid username address.
        if (StringUtils.isEmpty(username)) {
            usernameView.setError(getString(R.string.error_field_required));
            focusView = usernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            InputMethodManager inputManager = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow((null == getActivity().getCurrentFocus())
                    ? null
                    : getActivity().getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);

            viewModel().login(username, password);
        }
    }

    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        loginFormView.animate()
                .setDuration(shortAnimTime)
                .alpha(show ? 0 : 1)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (loginFormView != null) {
                            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    }
                });

        progressLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        progressView.animate()
                .setDuration(shortAnimTime)
                .alpha(show ? 1 : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (progressLayout != null) {
                            progressLayout.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    }
                });
    }

    private void showError(@NonNull String error) {
        get(toastProvider).showToast(error, Toast.LENGTH_LONG);
    }

    @NonNull
    @Override
    protected ProfileLoginViewModel viewModel() {
        return get(profileLoginViewModel);
    }

    @NonNull
    @Override
    protected DataBinder dataBinder() {
        return dataBinder;
    }

}
