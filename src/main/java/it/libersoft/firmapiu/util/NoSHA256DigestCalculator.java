package it.libersoft.firmapiu.util;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.DigestCalculator;

/**
 * 
 */

/**
 * Digest calculator, che non calcola il digest. Restituisce semplicemente un
 * digest che gli è stato passato come parametro Il digest precomputato che gli
 * è stato passato come parametro deve essere sha-256
 * 
 * @author dellanna
 *
 */
public final class NoSHA256DigestCalculator implements DigestCalculator {

	// sha-256 algorithm identifier
	private final static String SHA256ID = "2.16.840.1.101.3.4.2.1";

	// stream presso cui salvare il digest precalcolato
	private final ByteArrayOutputStream preCalculatedDigestStream;

	/**
	 * 
	 */
	public NoSHA256DigestCalculator() {
		preCalculatedDigestStream = new ByteArrayOutputStream();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bouncycastle.operator.DigestCalculator#getAlgorithmIdentifier()
	 */
	@Override
	public AlgorithmIdentifier getAlgorithmIdentifier() {
		// restituisce di default un algorithm identifier sha-256
		return new AlgorithmIdentifier(new ASN1ObjectIdentifier(SHA256ID));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bouncycastle.operator.DigestCalculator#getDigest()
	 */
	@Override
	public byte[] getDigest() {
		// restituisce il digest precalcolato
		// TODO attenzione il metodo restituisce semplicemente i dati presenti
		// sullo stream
		// non controlla che sia un digest o meno

		System.out
				.println("---->NoSHA256DigestCalculator.getDigest: restituisco i dati su cui è stato fatto il digest!");
		return preCalculatedDigestStream.toByteArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bouncycastle.operator.DigestCalculator#getOutputStream()
	 */
	@Override
	public OutputStream getOutputStream() {
		// restituisce l'outputstream presso cui salvare il digest precalcolato
		System.out
				.println("---->NoSHA256DigestCalculator.getOutputStream: i dati di cui fare il digest");
		return preCalculatedDigestStream;
	}

}
