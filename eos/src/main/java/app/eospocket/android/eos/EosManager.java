package app.eospocket.android.eos;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;

import app.eospocket.android.eos.model.AccountList;
import app.eospocket.android.eos.model.ChainInfo;
import app.eospocket.android.eos.request.KeyAccountsRequest;
import app.eospocket.android.eos.services.ChainService;
import app.eospocket.android.eos.services.HistoryService;
import app.eospocket.android.eos.services.WalletService;
import io.mithrilcoin.eos.crypto.ec.EosPrivateKey;
import io.mithrilcoin.eos.data.wallet.EosWalletManager;
import io.reactivex.Single;

/**
 * https://developers.eos.io/eosio-nodeos/reference
 */
@Singleton
public class EosManager {

    public static final int SUCCESS = 0;
    public static final int INVALID_PASSWORD = -1;

    private EosWalletManager mEosWalletManager;

    private ChainService mChainService;

    private HistoryService mHistoryService;

    private WalletService mWalletService;

    @Inject
    public EosManager(@NonNull EosWalletManager eosWalletManager,
            @NonNull ChainService chainService, @NonNull HistoryService historyService,
            @NonNull WalletService walletService) {
        this.mEosWalletManager = eosWalletManager;
        this.mChainService = chainService;
        this.mHistoryService = historyService;
        this.mWalletService = walletService;
    }

    public Single<Boolean> hasWallet(@NonNull String walletName) {
        return Single.fromCallable(() -> mEosWalletManager.walletExists(walletName));
    }

    public Single<ChainInfo> getChainInfo() {
        return mChainService.getInfo();
    }

    public Single<String> createWallet(@NonNull String walletName) {
        return Single.create(emitter -> {
            try {
                String pw = mEosWalletManager.create(walletName);

                if (!emitter.isDisposed()) {
                    emitter.onSuccess(pw);
                }
            } catch (Exception e) {
                if (!emitter.isDisposed()) {
                    emitter.onError(e);
                }
            }
        });
    }

    public Single<Integer> unlock(@NonNull String walletName, @NonNull String password) {
        return Single.fromCallable(() -> mEosWalletManager.unlock(walletName, password) ? SUCCESS : INVALID_PASSWORD);
    }

    public EosPrivateKey genPrivateKey() {
        return new EosPrivateKey();
    }

    @Nullable
    public Single<AccountList> findAccountByPublicKey(@NonNull KeyAccountsRequest request) {
        return mHistoryService.getAccountsByKey(request);
    }
}
