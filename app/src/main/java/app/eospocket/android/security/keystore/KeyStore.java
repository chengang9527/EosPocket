package app.eospocket.android.security.keystore;

import android.security.keystore.UserNotAuthenticatedException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface KeyStore {

    String ANDROID_KEY_STORE = "AndroidKeyStore";
    String KEY_ALGORITHM_RSA = "RSA";
    int KEY_EXPIRED_IN_YEAR = 100;

    void init();

    boolean createKeys(@NonNull String alias);

    @Nullable
    String encryptString(@NonNull String text, @NonNull String alias) throws UserNotAuthenticatedException;

    @Nullable
    String decryptString(@NonNull String text, @NonNull String alias) throws UserNotAuthenticatedException;
}
