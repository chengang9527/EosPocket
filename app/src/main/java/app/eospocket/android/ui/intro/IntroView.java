package app.eospocket.android.ui.intro;

import app.eospocket.android.common.mvp.IView;

public interface IntroView extends IView {

    void startLoginActivity();

    void startCreateWalletActivity();

    void startMainActivity();

    void initPinCode();

    void login();
}
