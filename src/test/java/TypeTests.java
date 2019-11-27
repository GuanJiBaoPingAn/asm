import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.Type;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class TypeTests {
	@Test
	public void primitiveTests() {
		Type byte1 = Type.getType(byte.class);
		Assert.assertEquals(byte1.getDescriptor(), "B");

		Type char1 = Type.getType(char.class);
		Assert.assertEquals(char1.getDescriptor(), "C");

		Type double1 = Type.getType(double.class);
		Assert.assertEquals(double1.getDescriptor(), "D");

		Type float1 = Type.getType(float.class);
		Assert.assertEquals(float1.getDescriptor(), "F");

		Type int1 = Type.getType(int.class);
		Assert.assertEquals(int1.getDescriptor(), "I");

		Type long1 = Type.getType(long.class);
		Assert.assertEquals(long1.getDescriptor(), "J");

		Type short1 = Type.getType(short.class);
		Assert.assertEquals(short1.getDescriptor(), "S");

		Type boolean1 = Type.getType(boolean.class);
		Assert.assertEquals(boolean1.getDescriptor(), "Z");
	}

	@Test
	public void primitiveWrapperTests() {
		Type integer = Type.getType(Integer.class);
		Assert.assertEquals(integer.getDescriptor(), "Ljava/lang/Integer;");

		Type byte1 = Type.getType(Byte.class);
		Assert.assertEquals(byte1.getDescriptor(), "Ljava/lang/Byte;");

		Type char1 = Type.getType(Character.class);
		Assert.assertEquals(char1.getDescriptor(), "Ljava/lang/Character;");

		Type double1 = Type.getType(Double.class);
		Assert.assertEquals(double1.getDescriptor(), "Ljava/lang/Double;");

		Type float1 = Type.getType(Float.class);
		Assert.assertEquals(float1.getDescriptor(), "Ljava/lang/Float;");

		Type long1 = Type.getType(Long.class);
		Assert.assertEquals(long1.getDescriptor(), "Ljava/lang/Long;");

		Type short1 = Type.getType(Short.class);
		Assert.assertEquals(short1.getDescriptor(), "Ljava/lang/Short;");

		Type boolean1 = Type.getType(Boolean.class);
		Assert.assertEquals(boolean1.getDescriptor(), "Ljava/lang/Boolean;");
	}

	@Test
	public void arrayReferenceTests() {
		Type object = Type.getType(Object.class);
		Assert.assertEquals(object.getDescriptor(), "Ljava/lang/Object;");

		Type array = Type.getType(double[].class);
		Assert.assertEquals(array.getDescriptor(), "[D");
	}

	@Test
	public void internalNameTest() {
		String string = Type.getInternalName(String.class);
		Assert.assertEquals(string, "java/lang/String");
	}

	@Test
	public void methodDescriptorTests() throws NoSuchMethodException {
		Method method = TestClass.class.getDeclaredMethod("testMethod", long.class, Long.class, String.class);
		String str = Type.getMethodDescriptor(method);
		Assert.assertEquals(str, "(JLjava/lang/Long;Ljava/lang/String;)Ljava/lang/Integer;");

		Method method2 = TestClass.class.getDeclaredMethod("testMethod2");
		String str2 = Type.getMethodDescriptor(method2);
		Assert.assertEquals(str2, "()V");

		Method method3 = TestClass.class.getDeclaredMethod("testMethod3", int[].class, Object.class);
		String str3 = Type.getMethodDescriptor(method3);
		Assert.assertEquals(str3, "([ILjava/lang/Object;)[I");

		Method method4 = TestClass.class.getDeclaredMethod("testMethod4", Integer[].class, int.class);
		String str4 = Type.getMethodDescriptor(method4);
		Assert.assertEquals(str4, "([Ljava/lang/Integer;I)[Ljava/lang/Integer;");
	}

	@Test
	public void constructorDescriptorTests() {
		Constructor<?>[] constructors = TestClass.class.getDeclaredConstructors();
		String c1 = Type.getConstructorDescriptor(constructors[0]);
		String c2 = Type.getConstructorDescriptor(constructors[1]);
		Assert.assertEquals(c1, "()V");
		Assert.assertEquals(c2, "(ILjava/lang/Long;Ljava/lang/String;)V");
	}
}