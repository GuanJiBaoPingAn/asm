import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_PARAMETER, ElementType.TYPE_USE})
@interface RuntimeVisibleTypeAnnotation {
}

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE_PARAMETER, ElementType.TYPE_USE})
@interface RuntimeInvisibleTypeAnnotation {
}

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.FIELD})
@interface RuntimeVisibleAnnotation {
	String color() default "blue";

	String value() default "";
}

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.FIELD})
@interface RuntimeInvisibleAnnotation {
	String color() default "blue";

	String value() default "";
}

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
@interface RuntimeVisibleParameterAnnotation {
	String color() default "blue";

	String value() default "";
}

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.PARAMETER})
@interface RuntimeInvisibleParameterAnnotation {
	String color() default "blue";

	String value() default "";
}

@RuntimeVisibleAnnotation
@RuntimeInvisibleAnnotation
public class TestClass {

	@RuntimeVisibleAnnotation
	@RuntimeInvisibleAnnotation
	public String field1;

	@RuntimeVisibleTypeAnnotation
	@RuntimeInvisibleTypeAnnotation
	public String field2;

	public TestClass() {

	}

	public TestClass(int i, Long l, String s) {

	}

	@RuntimeVisibleAnnotation
	@RuntimeInvisibleAnnotation
	public Integer testMethod(@RuntimeVisibleParameterAnnotation long ll, @RuntimeInvisibleParameterAnnotation Long l, String s) {
		return null;
	}

	public void testMethod2() {

	}

	public int[] testMethod3(int[] i1, Object o) {
		return null;
	}

	public Integer[] testMethod4(Integer[] i1, int i2) {
		return null;
	}
}

