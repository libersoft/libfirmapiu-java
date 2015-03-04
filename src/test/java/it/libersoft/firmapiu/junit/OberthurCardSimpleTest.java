/**
 * 
 */
package it.libersoft.firmapiu.junit;

import org.junit.BeforeClass;

/**
 * @author andy
 *
 */
public class OberthurCardSimpleTest extends CRTSmartCardTokenSimpleTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CRTSmartCardTokenSimpleTest.name="Oberthur";
		CRTSmartCardTokenSimpleTest.pass="55732689".toCharArray();
		CRTSmartCardTokenSimpleTest.setUpBeforeClassProcedure();
	}

}
