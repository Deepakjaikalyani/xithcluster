package br.edu.univercidade.cc.xithcluster;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import br.edu.univercidade.cc.xithcluster.composition.ImageOrderAndZBufferStrategyTest;
import br.edu.univercidade.cc.xithcluster.composition.PixelBufferTest;
import br.edu.univercidade.cc.xithcluster.messages.ComposerMessageBrokerTest;
import br.edu.univercidade.cc.xithcluster.messages.MasterMessageBrokerTest;
import br.edu.univercidade.cc.xithcluster.messages.RendererMessageBrokerTest;
import br.edu.univercidade.cc.xithcluster.serial.BranchGroupSerializerTest;
import br.edu.univercidade.cc.xithcluster.serial.GroupSerializerTest;
import br.edu.univercidade.cc.xithcluster.serial.SerializationHelperTest;
import br.edu.univercidade.cc.xithcluster.serial.SerializerTester;
import br.edu.univercidade.cc.xithcluster.serial.TransformGroupSerializerTest;
import br.edu.univercidade.cc.xithcluster.utils.BufferUtilsTest;

@RunWith(Suite.class)
@SuiteClasses({
		BufferUtilsTest.class,
		SerializerTester.class,
		TransformGroupSerializerTest.class,
		GroupSerializerTest.class,
		BranchGroupSerializerTest.class,
		SerializationHelperTest.class,
		PixelBufferTest.class,
		ImageOrderAndZBufferStrategyTest.class,
		ComposerMessageBrokerTest.class,
		MasterMessageBrokerTest.class,
		RendererMessageBrokerTest.class
})
public class AllTests {
	
}
