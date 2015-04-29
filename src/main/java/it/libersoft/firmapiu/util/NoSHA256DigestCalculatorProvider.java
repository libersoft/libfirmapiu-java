package it.libersoft.firmapiu.util;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;

/**
 * 
 */

/**
 * Classe custom per generare un digestcalculator provider che in realtà non
 * calcola il digest. Serve per impostare le API di Bouncy Castle in modo da
 * firmare dati di cui è già stato precalcolato il digest
 * 
 * @author dellanna
 *
 */
public final class NoSHA256DigestCalculatorProvider implements
		DigestCalculatorProvider {

	private final DigestCalculator digCalc;

	/**
	 * 
	 */
	public NoSHA256DigestCalculatorProvider() {
		digCalc = new NoSHA256DigestCalculator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bouncycastle.operator.DigestCalculatorProvider#get(org.bouncycastle
	 * .asn1.x509.AlgorithmIdentifier)
	 */
	@Override
	public DigestCalculator get(AlgorithmIdentifier arg0)
			throws OperatorCreationException {
		// restituisce un digestcalculator che semplimente restituisce già un
		// digest pre-calcolato
		System.out.println("NoSHA256DigestCalculatorProvider.get: this:"
				+ this.toString() + " : restituisco il NoSHA256DigestCalculator");
		return digCalc;
	}

}
