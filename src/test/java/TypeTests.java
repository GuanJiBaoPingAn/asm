import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class TypeTests {

    public TypeTests() {
        super();
    }

    @Test
    public void test() {
        Type intType = Type.FLOAT_TYPE;
        Type type = Type.getObjectType("Ijava/lang/Integer");
        System.out.println(type);
    }

}
