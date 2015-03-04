/**
 * 
 */
package it.libersoft.firmapiu.junit;


import org.junit.BeforeClass;

/**
 * Batteria di test su carta Athena
 * 
 * @author dellanna
 *
 */
public class AthenaCardSimpleTest extends CRTSmartCardTokenSimpleTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CRTSmartCardTokenSimpleTest.name="Athena";
		CRTSmartCardTokenSimpleTest.pass="87654321".toCharArray();
		CRTSmartCardTokenSimpleTest.setUpBeforeClassProcedure();
	}

	
}
