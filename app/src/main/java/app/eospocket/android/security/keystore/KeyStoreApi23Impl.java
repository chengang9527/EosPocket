package app.eospocket.android.security.keystore;

import android.content.Context;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class KeyStoreApi23Impl implements KeyStore {

    private static final String CIPHER_ALGORITHM = "AES/GCM/NoPadding";
    private static final String NEW_PADDING = KeyProperties.ENCRYPTION_PADDING_NONE;
    private static final String NEW_BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM;

    private java.security.KeyStore mKeyStore;

    private Context mContext;

    private boolean mUserAuthRequired = true;

    private int mUserAuthValidDurationSec = 300;

    @Inject
    public KeyStoreApi23Impl(Context context) {
        this.mContext = context;
    }

    @Override
    public void init() {
        try {
            mKeyStore = java.security.KeyStore.getInstance(ANDROID_KEY_STORE);
            mKeyStore.load(null);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean createKeys(@NonNull String alias) {
        if (mKeyStore == null) {
            return false;
        }

        try {
            if (!mKeyStore.containsAlias(alias)) {
                KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);
                keyGenerator.init(new KeyGenParameterSpec.Builder(alias,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(NEW_BLOCK_MODE)
                        .setUserAuthenticationRequired(mUserAuthRequired)
                        .setUserAuthenticationValidityDurationSeconds(mUserAuthValidDurationSec)
                        .setRandomizedEncryptionRequired(false)
                        .setEncryptionPaddings(NEW_PADDING)
                        .build());
                keyGenerator.generateKey();
            }

            return true;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Nullable
    @Override
    public String encryptString(@NonNull String text, @NonNull String alias) {
        if (mKeyStore == null) {
            init();
        }

        byte [] values = null;

        try {
            SecretKey secretKey = (SecretKey) mKeyStore.getKey(alias, null);

            Cipher inCipher = Cipher.getInstance(CIPHER_ALGORITHM);
            inCipher.init(Cipher.ENCRYPT_MODE, secretKey);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(
                    outputStream, inCipher);
            cipherOutputStream.write(text.getBytes("UTF-8"));
            cipherOutputStream.close();

            values = outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (values == null) {
            return null;
        }

        return Base64.encodeToString(values, Base64.DEFAULT);
    }

    @Nullable
    @Override
    public String decryptString(@NonNull String text, @NonNull String alias) {
        if (mKeyStore == null) {
            init();
        }

        String results = null;

        try {
            SecretKey secretKey = (SecretKey) mKeyStore.getKey(alias, null);

            Cipher output = Cipher.getInstance(CIPHER_ALGORITHM);
            output.init(Cipher.DECRYPT_MODE, secretKey);
            CipherInputStream cipherInputStream = new CipherInputStream(
                    new ByteArrayInputStream(Base64.decode(text, Base64.DEFAULT)), output);

            ArrayList<Byte> values = new ArrayList<>();

            int nextByte;

            while ((nextByte = cipherInputStream.read()) != -1) {
                values.add((byte)nextByte);
            }

            byte[] bytes = new byte[values.size()];

            for(int i = 0; i < bytes.length; i++) {
                bytes[i] = values.get(i);
            }

            results = new String(bytes, 0, bytes.length, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return  results;
    }
}
