package com.yonyou.iuap.baseservice.model;

import com.yonyou.iuap.baseservice.entity.annotation.Reference;

import java.lang.reflect.Field;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Field[] fields = TestBean.class.getDeclaredFields();
        for(Field field :fields){
            if(field.isAnnotationPresent(Reference.class)){
                Reference reference = (Reference) field.getAnnotation(Reference.class);
                System.out.println(reference.path());
                String[] srcProperties = reference.srcProperties();
                for (String srcProperty : srcProperties) {
                    System.out.println(srcProperty);
                }
                String[] desProperties = reference.desProperties();
                for (String desProperty : desProperties) {
                    System.out.println(desProperty);
                }
            }
        }
    }
}
