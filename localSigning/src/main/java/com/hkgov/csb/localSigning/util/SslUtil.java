package com.hkgov.csb.localSigning.util;

import okhttp3.OkHttpClient;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

public class SslUtil {
	private static X509TrustManager getX509TrustManager() {
		X509TrustManager trustManager = null;
		try {
			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init((KeyStore) null);
			TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
			if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
				throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
			}
			trustManager = (X509TrustManager) trustManagers[0];
		} catch (Exception e) {
			e.printStackTrace();
		}

		return trustManager;
	}

	public static OkHttpClient getUnsafeOkHttpClient() throws KeyManagementException {
		try {
			final TrustManager[] trustAllCerts = new TrustManager[]{
					new X509TrustManager() {
						@Override
						public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

						}

						@Override
						public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

						}

						@Override
						public X509Certificate[] getAcceptedIssuers() {
							return new X509Certificate[]{};
						}
					}
			};


			final SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, trustAllCerts, new SecureRandom());
			final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
			OkHttpClient.Builder builder = new OkHttpClient.Builder();
			builder.sslSocketFactory(sslSocketFactory,getX509TrustManager());


			builder.hostnameVerifier(new HostnameVerifier() {
				@Override
				public boolean verify(String s, SSLSession sslSession) {
					return true;
				}
			});

			return builder.build();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
}
