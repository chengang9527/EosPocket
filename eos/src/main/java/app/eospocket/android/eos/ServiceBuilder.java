package app.eospocket.android.eos;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.google.gson.Gson;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class ServiceBuilder {

    private static final int CONNECTION_TIMEOUT_IN_SEC = 15;
    private static final int READ_TIMEOUT_IN_SEC = 15;
    private static final int WRITE_TIMEOUT_IN_SEC = 15;

    private static Retrofit.Builder RETROFIT_BUILDER = new Retrofit.Builder();

    // todo - builder pattern
    // private Gson gson;
    // private boolean useGson;

    // No need to instantiate this class.
    private ServiceBuilder() {}

    public static <T> T createService(Class<T> serviceClass, String baseUrl) {
        return createService(serviceClass, baseUrl, null, null);
    }

    public static <T> T createService(Class<T> serviceClass, String baseUrl, Gson gson) {
        return createService(serviceClass, baseUrl, null, gson);
    }

    public static <T> T createService(Class<T> serviceClass, String baseUrl,
            HttpLoggingInterceptor httpLoggingInterceptor, Gson gson) {
        return createService(serviceClass, baseUrl, httpLoggingInterceptor, false, gson);
    }

    public static <T> T createService(Class<T> serviceClass, String baseUrl,
            HttpLoggingInterceptor httpLoggingInterceptor, boolean userBadSslSocketFactory, Gson gson) {
        return createService(serviceClass, baseUrl, httpLoggingInterceptor, userBadSslSocketFactory,
                CONNECTION_TIMEOUT_IN_SEC, READ_TIMEOUT_IN_SEC, WRITE_TIMEOUT_IN_SEC, gson);
    }

    public static <T> T createService(Class<T> serviceClass, String baseUrl,
            HttpLoggingInterceptor httpLoggingInterceptor, boolean userBadSslSocketFactory,
            int connectTimeoutInSec, int readTimeoutInSec, int writeTimeoutInSec, Gson gson) {
        OkHttpClient okHttpClient = getClient(userBadSslSocketFactory, httpLoggingInterceptor,
                connectTimeoutInSec, readTimeoutInSec, writeTimeoutInSec);

        RETROFIT_BUILDER.client(okHttpClient)
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()));

        if (gson != null) {
            RETROFIT_BUILDER.addConverterFactory(GsonConverterFactory.create(gson));
        } else {
            RETROFIT_BUILDER.addConverterFactory(JacksonConverterFactory.create());
        }

        Retrofit retrofit = RETROFIT_BUILDER.build();
        return retrofit.create(serviceClass);
    }

    private static OkHttpClient getClient(boolean userBadSslSocketFactory, HttpLoggingInterceptor httpLoggingInterceptor,
                                          int connectTimeoutInSec, int readTimeoutInSec, int writeTimeoutInSec) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if (httpLoggingInterceptor != null) {
            builder.addInterceptor(httpLoggingInterceptor);
        }

        builder.connectTimeout(connectTimeoutInSec, TimeUnit.SECONDS);
        builder.readTimeout(readTimeoutInSec, TimeUnit.SECONDS);
        builder.writeTimeout(writeTimeoutInSec, TimeUnit.SECONDS);

        if (userBadSslSocketFactory) {
            builder.sslSocketFactory(createBadSslSocketFactory());
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        }

        return builder.build();
    }

    private static SSLSocketFactory createBadSslSocketFactory() {
        try {
            // Construct SSLSocketFactory that accepts any cert.
            SSLContext context = SSLContext.getInstance("TLS");
            TrustManager permissive = new X509TrustManager() {

                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            };
            context.init(null, new TrustManager[]{permissive}, null);
            return context.getSocketFactory();
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }
}