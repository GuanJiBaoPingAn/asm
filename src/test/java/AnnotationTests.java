import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.openjdk.jol.vm.VM;
import org.openjdk.jol.vm.VirtualMachine;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public class AnnotationTests {

	static VirtualMachine vm = null;

	@Before
	public void init() {
		vm = VM.current();
	}

	@Test
	public void annotationTests() throws NoSuchMethodException, NoSuchFieldException, IOException {
		Annotation[] annotations = TestClass.class.getAnnotations();
		System.out.println(Arrays.toString(annotations));

		Method method = TestClass.class.getDeclaredMethod("testMethod", long.class, Long.class, String.class);
		Annotation[] methodAnnotations = method.getAnnotations();
		System.out.println(Arrays.toString(methodAnnotations));

		Field field1 = TestClass.class.getDeclaredField("field1");
		Field field2 = TestClass.class.getDeclaredField("field2");

		Annotation[] field1Annotations = field1.getAnnotations();
		Annotation[] field2Annotations = field2.getAnnotations();

		System.out.println(Arrays.toString(field1Annotations));
		System.out.println(Arrays.toString(field2Annotations));

		InputStream classFile = this.getClass().getResourceAsStream("TestClass.class");
		ClassReader classReader = new ClassReader(classFile);
		ClassWriter classWriter = new ClassWriter(classReader, Opcodes.ASM5);
		System.out.println(classWriter);
	}

}
