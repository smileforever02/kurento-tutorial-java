package org.kurento.tutorial.groupcall.behappy.utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public class BehappyBeanUtils extends BeanUtils {

  public static void copyNotNullProperties(Object source, Object target)
      throws BeansException {
    copyNotNullProperties(source, target, null, (String[]) null);
  }

  public static void copyNotNullProperties(Object source, Object target,
      Class<?> editable) throws BeansException {
    copyNotNullProperties(source, target, editable, (String[]) null);
  }

  public static void copyNotNullProperties(Object source, Object target,
      String... ignoreProperties) throws BeansException {
    copyNotNullProperties(source, target, null, ignoreProperties);
  }

  private static void copyNotNullProperties(Object source, Object target,
      Class<?> editable, String... ignoreProperties) throws BeansException {

    Assert.notNull(source, "Source must not be null");
    Assert.notNull(target, "Target must not be null");

    Class<?> actualEditable = target.getClass();
    if (editable != null) {
      if (!editable.isInstance(target)) {
        throw new IllegalArgumentException(
            "Target class [" + target.getClass().getName()
                + "] not assignable to Editable class [" + editable.getName()
                + "]");
      }
      actualEditable = editable;
    }
    PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
    List<String> ignoreList = (ignoreProperties != null
        ? Arrays.asList(ignoreProperties) : null);

    for (PropertyDescriptor targetPd : targetPds) {
      Method writeMethod = targetPd.getWriteMethod();
      if (writeMethod != null
          && (ignoreList == null || !ignoreList.contains(targetPd.getName()))) {
        PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(),
            targetPd.getName());
        if (sourcePd != null) {
          Method readMethod = sourcePd.getReadMethod();
          if (readMethod != null && ClassUtils.isAssignable(
              writeMethod.getParameterTypes()[0], readMethod.getReturnType())) {
            try {
              if (!Modifier
                  .isPublic(readMethod.getDeclaringClass().getModifiers())) {
                readMethod.setAccessible(true);
              }
              Object value = readMethod.invoke(source);
              if (value != null) {
                if (!Modifier
                    .isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                  writeMethod.setAccessible(true);
                }
                writeMethod.invoke(target, value);
              }
            } catch (Throwable ex) {
              throw new FatalBeanException("Could not copy property '"
                  + targetPd.getName() + "' from source to target", ex);
            }
          }
        }
      }
    }
  }

}