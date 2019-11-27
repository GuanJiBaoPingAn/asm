#Descriptors
>FieldDescriptor:  
>>FieldType  
    
>FieldType:
>>BaseType  
>>ObjectType  
>>ArrayType  
    
>BaseType:(one of)
>>B C D F I J S Z
    
>ObjectType:
>>L ClassName ;

>ArrayType:
>>[ ComponentType

>ComponentType:
>>FieldType

##Interpretation of field descriptors
![pic1](./imgs/FIELD_DESCRIPTORs.PNG)

如，int 是 I  
Object 是 Ljava/lang/Object;  
double[][][] 是 [[[D.

##Method Descriptors
>MethodDescriptor:
>>( {ParameterDescriptor} ) ReturnDescriptor  

>ParameterDescriptor:
>>FieldType

>ReturnDescriptor:
>>FieldType  
>>VoidDescriptor

>VoidDescriptor:
>>V

Object m(int i, double d, Thread t) {}  
是  
(IDLjava/lang/Thread;)Ljava/lang/Object;  

