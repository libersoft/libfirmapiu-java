package it.libersoft.firmapiu.junit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AthenaCardSimpleTest.class, IncardCardSimpleTest.class,
		OberthurCardSimpleTest.class })
public class SimpleCardAllTests {

}
