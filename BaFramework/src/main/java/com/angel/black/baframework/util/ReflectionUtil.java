package com.angel.black.baframework.util;

import com.angel.black.baframework.logger.BaLog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public final class ReflectionUtil {

	public static void _DUMP_METHODS(Class<?> clazz) {
		for (Method method : clazz.getMethods()) {
			BaLog.d(method);
		}
		for (Method method : clazz.getDeclaredMethods()) {
			BaLog.d(method);
		}
	}

	public static Method getMethod(Class<?> clz, String name, Class<?>[] parameterTypes) throws NoSuchMethodException {
		boolean m = false;
		for (Method method : clz.getMethods()) {
			final String method_name = method.getName();
			if (!method_name.equals(name))
				continue;
			m = true;
			final Class<?>[] formalParams = method.getParameterTypes();
			if (checkParams(formalParams, parameterTypes)) {
				return method;
			}
		}
		//debug////////////////////////////////////////////////////////////
		final Method[] _methods = clz.getDeclaredMethods();
		for (int i = 0; i < _methods.length; i++) {
			final Method method = _methods[i];
			final String method_name = method.getName();
			if (!method_name.equals(name))
				continue;

			final Class<?>[] formalParams = method.getParameterTypes();
			if (checkParams(formalParams, parameterTypes)) {
				throw new NoSuchMethodException(name + " is DeclaredMethods method must chagne public method in class[" + clz.getSimpleName() + "]");
			}
		}

		if (m)
			throw new NoSuchMethodException(name + " has but parmas unmatched in class[" + clz.getSimpleName() + "]");
		else
			throw new NoSuchMethodException(BaLog._MESSAGE(clz, name, Arrays.toString(parameterTypes)));
	}
	public static Method getDeclaredMethods(Class<?> clazz, String name, Class<?>[] parameterTypes) throws NoSuchMethodException {
		boolean m = false;
		for (Method method : clazz.getMethods()) {
			final String method_name = method.getName();
			if (!method_name.equals(name))
				continue;
			m = true;
			final Class<?>[] formalParams = method.getParameterTypes();
			if (checkParams(formalParams, parameterTypes)) {
				return method;
			}
		}

		for (Method method : clazz.getDeclaredMethods()) {
			final String method_name = method.getName();
			if (!method_name.equals(name))
				continue;
			m = true;
			final Class<?>[] formalParams = method.getParameterTypes();
			if (checkParams(formalParams, parameterTypes)) {
				return method;
			}
		}

		//debug////////////////////////////////////////////////////////////
		if (!m)
			throw new NoSuchMethodException(BaLog._MESSAGE(clazz, name, Arrays.toString(parameterTypes)));
		else
			throw new NoSuchMethodException(name + " has but parmas unmatched in class[" + clazz.getSimpleName() + "]");

	}
//	public static Class<?>[] getParameterTypes(Object[] args) {
//		if (args == null)
//			return new Class<?>[0];
//
//		Class<?>[] types = new Class<?>[args.length];
//		for (int i = 0; i < args.length; i++) {
//			if (args[i] == null)
//				types[i] = null;
//			else
//				types[i] = args[i].getClass();
//		}
////		BaLog.l(Arrays.toString(types));
//		return types;
//	}
	public static Class<?>[] getParameterTypes(Object... args) {
		if (args == null)
			return new Class<?>[0];

		Class<?>[] types = new Class<?>[args.length];
		for (int i = 0; i < args.length; i++) {
			if (args[i] == null)
				types[i] = null;
			else
				types[i] = args[i].getClass();
		}
		BaLog.d(Arrays.toString(types));
		return types;
	}
	private static boolean checkParams(Class<?>[] formalParams, Class<?>[] actualParams) {
		if (formalParams == null && actualParams == null)
			return true;
		if (formalParams == null && actualParams != null)
			return false;
		if (formalParams != null && actualParams == null)
			return false;
		if (formalParams.length != actualParams.length)
			return false;
		if (formalParams.length == 0)
			return true;

		for (int i = 0; i < formalParams.length; i++) {
			if (!formalParams[i].isPrimitive()) {
				if (actualParams[i] != null && !formalParams[i].isAssignableFrom(actualParams[i]))
					return false;
			} else {
				if (formalParams[i].equals(boolean.class)) {
					if (!Boolean.class.isAssignableFrom(actualParams[i]))
						return false;
				} else {
					if (!Number.class.isAssignableFrom(actualParams[i]))
						return false;
				}
			}
		}
		return true;
	}
	public static Class<?> getReturnType(Object obj, String name, Object... args) throws NoSuchMethodException {
		return getReturnType(obj.getClass(), name, args);
	}
	public static Class<?> getReturnType(Class<?> clazz, String name, Object... args) throws NoSuchMethodException {
		final Class<?>[] parameterTypes = getParameterTypes(args);
		final Method method = getMethod(clazz, name, parameterTypes);
		BaLog.d(method);
		return method.getReturnType();
	}
	public static Class<?> getReturnTypeDeclared(Object obj, String name, Object... args) throws NoSuchMethodException {
		return getReturnType(obj.getClass(), name, args);
	}
	public static Class<?> getReturnTypeDeclared(Class<?> clazz, String name, Object... args) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		final Class<?>[] parameterTypes = getParameterTypes(args);
		final Method method = getDeclaredMethods(clazz, name, parameterTypes);
		method.setAccessible(true);
		BaLog.d(method);
		return method.getReturnType();
	}

	public static Object invoke(Object receiver, String name, Object... args) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		final Class<?> clazz = receiver.getClass();
		final Class<?>[] parameterTypes = getParameterTypes(args);
		final Method method = getMethod(clazz, name, parameterTypes);
		final Object result = method.invoke(receiver, args);

		BaLog.d(result, method);

		final Class<?> returnType = method.getReturnType();
		if (returnType == void.class || returnType == Void.class)
			return void.class;
		else
			return result;
	}
	public static Object invokeDeclared(Object receiver, String name, Object... args) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		final Class<?> clazz = receiver.getClass();
		final Class<?>[] parameterTypes = getParameterTypes(args);
		final Method method = getDeclaredMethods(clazz, name, parameterTypes);
		method.setAccessible(true);
		final Object result = method.invoke(receiver, args);

		BaLog.d(result, method);

		final Class<?> returnType = method.getReturnType();
		if (returnType == void.class || returnType == Void.class)
			return void.class;
		else
			return result;
	}
	public static String methodName() {
		StackTraceElement stackTrace = new Exception().getStackTrace()[1];

		if (stackTrace.isNativeMethod())
			return null;
		if (stackTrace.getClassName().charAt(0) == '<')
			return null;
		if (stackTrace.getMethodName().charAt(0) == '<')
			return null;
		return stackTrace.getMethodName();
	}
	public static boolean isVoid(Object result) {
		return (result == void.class || result == Void.class);
	}

}
