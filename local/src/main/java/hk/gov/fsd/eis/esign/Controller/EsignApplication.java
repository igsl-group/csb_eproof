package hk.gov.fsd.eis.esign.Controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.security.*;
import java.security.cert.*;

@SpringBootApplication
public class EsignApplication {

	public static void main(String[] args) throws UnrecoverableKeyException, CertificateException, KeyStoreException, NoSuchAlgorithmException, IOException {
		SpringApplication.run(EsignApplication.class, args);
	}

}
