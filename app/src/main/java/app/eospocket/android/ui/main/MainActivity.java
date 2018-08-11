package app.eospocket.android.ui.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import org.reactivestreams.Subscription;

import java.util.List;

import javax.inject.Inject;

import app.eospocket.android.R;
import app.eospocket.android.common.CommonActivity;
import app.eospocket.android.ui.importaccount.ImportAccountActivity;
import app.eospocket.android.wallet.db.model.EosAccountModel;
import app.eospocket.android.wallet.repository.EosAccountRepository;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.FlowableSubscriber;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends CommonActivity implements MainView {

    @Inject
    MainPresenter mMainPresenter;

    @Inject
    EosAccountRepository mEosAccountRepository;

    private Disposable mAccountDisposable;

    @BindView(R.id.btn_import_account)
    Button mImportAccountButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        subscribeAccounts();

        mMainPresenter.onCreate();
    }

    @Override
    protected void onDestroy() {
        if (mAccountDisposable != null && !mAccountDisposable.isDisposed()) {
            mAccountDisposable.isDisposed();
            mAccountDisposable = null;
        }

        super.onDestroy();
    }

    private void subscribeAccounts() {
        if (mAccountDisposable != null && !mAccountDisposable.isDisposed()) {
            mAccountDisposable.isDisposed();
            mAccountDisposable = null;
        }

        mEosAccountRepository.getEosAccounts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new FlowableSubscriber<List<EosAccountModel>>() {
                    @Override
                    public void onSubscribe(Subscription s) {

                    }

                    @Override
                    public void onNext(List<EosAccountModel> eosAccountModels) {
                        // todo - ui update
                        if (!eosAccountModels.isEmpty()) {
                            mImportAccountButton.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getAccount() {
        String accountName = "";

        mMainPresenter.getAccount(accountName);
    }

    @OnClick(R.id.btn_import_account)
    public void onImportAccountClick() {
        startActivity(ImportAccountActivity.class);
    }
}
