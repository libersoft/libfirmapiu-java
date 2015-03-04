/**
 * 
 */
package it.libersoft.firmapiu.junit;

import org.junit.BeforeClass;

/**
 * @author andy
 *
 */
public class IncardCardSimpleTest extends CRTSmartCardTokenSimpleTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CRTSmartCardTokenSimpleTest.name="Incard";
		CRTSmartCardTokenSimpleTest.pass="12345678".toCharArray();
		CRTSmartCardTokenSimpleTest.setUpBeforeClassProcedure();
	}

}
