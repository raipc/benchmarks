package com.github.raipc;

import com.github.raipc.data.MultiMethodClass;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class ReflectionBenchmark {
	private static final String STR1_GETTER = "getStr1";
	private static final String STR1_SETTER = "setStr1";

	static class A {
		private String str1;
		private String str2;

		public String getStr1() {
			return str1;
		}

		public String getStr2() {
			return str2;
		}

		public void setStr1(String str1) {
			this.str1 = str1;
		}

		public void setStr2(String str2) {
			this.str2 = str2;
		}
	}

	static class B extends A {
		@Override
		public String getStr2() {
			return super.getStr2();
		}

		@Override
		public void setStr2(String str2) {
			super.setStr2(str2);
		}
	}

	static class C extends MultiMethodClass {
		@Override
		public String getStr2() {
			return super.getStr2();
		}

		@Override
		public void setStr2(String str2) {
			super.setStr2(str2);
		}
	}

	@Benchmark
	public Method getMethodForStr1FromA() throws NoSuchMethodException {
		return A.class.getMethod(STR1_GETTER);
	}

	@Benchmark
	public Method getMethodForStr1FromB() throws NoSuchMethodException {
		return B.class.getMethod(STR1_GETTER);
	}

	@Benchmark
	public Method getMethodForStr1FromC() throws NoSuchMethodException {
		return C.class.getMethod(STR1_GETTER);
	}

	@Benchmark
	public Method getDeclaredMethodForStr1FromA() throws NoSuchMethodException {
		return A.class.getDeclaredMethod(STR1_GETTER);
	}

	@Benchmark
	public Method getDeclaredMethodForStr1FromB() throws NoSuchMethodException {
		try {
			return B.class.getDeclaredMethod(STR1_GETTER);
		} catch (NoSuchMethodException e) {
			return B.class.getSuperclass().getDeclaredMethod(STR1_GETTER);
		}
	}

	@Benchmark
	public Method getDeclaredMethodForStr1FromC() throws NoSuchMethodException {
		try {
			return C.class.getDeclaredMethod(STR1_GETTER);
		} catch (NoSuchMethodException e) {
			return C.class.getSuperclass().getDeclaredMethod(STR1_GETTER);
		}
	}

	@Benchmark
	public Method[] getMethodsForA() {
		return A.class.getMethods();
	}

	@Benchmark
	public Method[] getMethodsForB() {
		return B.class.getMethods();
	}

	@Benchmark
	public Method[] getMethodsForC() {
		return C.class.getMethods();
	}

	@Benchmark
	public Method[] getDeclaredMethodsForA() {
		return A.class.getDeclaredMethods();
	}

	@Benchmark
	public Method[] getDeclaredMethodsForB() {
		return B.class.getDeclaredMethods();
	}

	@Benchmark
	public Method[] getDeclaredMethodsForC() {
		return C.class.getDeclaredMethods();
	}

	@Benchmark
	public Method[] getDeclaredMethodsForCSuperclass() {
		return C.class.getSuperclass().getDeclaredMethods();
	}

	@Benchmark
	public Method findGetterMethodForAManually() {
		return findGetterMethod(A.class.getMethods(), STR1_GETTER);
	}

	@Benchmark
	public Method findGetterMethodForBManually() {
		return findGetterMethod(B.class.getMethods(), STR1_GETTER);
	}

	@Benchmark
	public Method findGetterMethodForCManually() {
		return findGetterMethod(C.class.getMethods(), STR1_GETTER);
	}

	@Benchmark
	public Method findDeclaredGetterMethodForAManually() {
		Method getterMethod = findGetterMethod(A.class.getDeclaredMethods(), STR1_GETTER);
		if (getterMethod == null) {
			Class<?> clazz = A.class.getSuperclass();
			if (clazz != null) {
				getterMethod = findGetterMethod(clazz.getDeclaredMethods(), STR1_GETTER);
			}
		}
		return getterMethod;
	}

	@Benchmark
	public Method findDeclaredGetterMethodForBManually() {
		Method getterMethod = findGetterMethod(B.class.getDeclaredMethods(), STR1_GETTER);
		if (getterMethod == null) {
			Class<?> clazz = B.class.getSuperclass();
			if (clazz != null) {
				getterMethod = findGetterMethod(clazz.getDeclaredMethods(), STR1_GETTER);
			}
		}
		return getterMethod;
	}

	@Benchmark
	public Method findDeclaredGetterMethodForCManually() {
		Method getterMethod = findGetterMethod(C.class.getDeclaredMethods(), STR1_GETTER);
		if (getterMethod == null) {
			Class<?> clazz = C.class.getSuperclass();
			if (clazz != null) {
				getterMethod = findGetterMethod(clazz.getDeclaredMethods(), STR1_GETTER);
			}
		}
		return getterMethod;
	}

	@Benchmark
	public Method findSetterMethodForAManually() {
		return findSetterMethod(A.class.getMethods(), STR1_SETTER);
	}

	@Benchmark
	public Method findSetterMethodForBManually() {
		return findSetterMethod(B.class.getMethods(), STR1_SETTER);
	}

	@Benchmark
	public Method findSetterMethodForCManually() {
		return findSetterMethod(C.class.getMethods(), STR1_SETTER);
	}

	@Benchmark
	public Method findDeclaredSetterMethodForAManually() {
		Method getterMethod = findSetterMethod(A.class.getDeclaredMethods(), STR1_SETTER);
		if (getterMethod == null) {
			Class<?> clazz = A.class.getSuperclass();
			if (clazz != null) {
				getterMethod = findSetterMethod(clazz.getDeclaredMethods(), STR1_SETTER);
			}
		}
		return getterMethod;
	}

	@Benchmark
	public Method findDeclaredSetterMethodForBManually() {
		Method getterMethod = findSetterMethod(B.class.getDeclaredMethods(), STR1_SETTER);
		if (getterMethod == null) {
			Class<?> clazz = B.class.getSuperclass();
			if (clazz != null) {
				getterMethod = findSetterMethod(clazz.getDeclaredMethods(), STR1_SETTER);
			}
		}
		return getterMethod;
	}

	@Benchmark
	public Method findDeclaredSetterMethodForCManually() {
		Method getterMethod = findSetterMethod(C.class.getDeclaredMethods(), STR1_SETTER);
		if (getterMethod == null) {
			Class<?> clazz = C.class.getSuperclass();
			if (clazz != null) {
				getterMethod = findSetterMethod(clazz.getDeclaredMethods(), STR1_SETTER);
			}
		}
		return getterMethod;
	}

	private static Method findGetterMethod(Method[] methods, String methodName) {
		for (Method method : methods) {
			if (methodName.equals(method.getName()) && method.getParameters().length == 0) {
				return method;
			}
		}
		return null;
	}

	private static Method findSetterMethod(Method[] methods, String methodName) {
		for (Method method : methods) {
			if (methodName.equals(method.getName())) {
				final Parameter[] parameters = method.getParameters();
				if (parameters.length == 1 && String.class.equals(parameters[0].getType())) {
					return method;
				}
			}
		}
		return null;
	}

	@Benchmark
	public Method getMethodSetterForStr1FromA() throws NoSuchMethodException {
		return A.class.getMethod(STR1_SETTER, String.class);
	}

	@Benchmark
	public Method getMethodSetterForStr1FromB() throws NoSuchMethodException {
		return B.class.getMethod(STR1_SETTER, String.class);
	}

	@Benchmark
	public Method getMethodSetterForStr1FromC() throws NoSuchMethodException {
		return C.class.getMethod(STR1_SETTER, String.class);
	}

	@Benchmark
	public Method getDeclaredMethodSetterForStr1FromA() throws NoSuchMethodException {
		return A.class.getDeclaredMethod(STR1_SETTER, String.class);
	}

	@Benchmark
	public Method getDeclaredMethodSetterForStr1FromB() throws NoSuchMethodException {
		try {
			return B.class.getDeclaredMethod(STR1_SETTER, String.class);
		} catch (NoSuchMethodException e) {
			return B.class.getSuperclass().getDeclaredMethod(STR1_SETTER, String.class);
		}
	}

	@Benchmark
	public Method getDeclaredMethodSetterForStr1FromC() throws NoSuchMethodException {
		try {
			return C.class.getDeclaredMethod(STR1_SETTER, String.class);
		} catch (NoSuchMethodException e) {
			return C.class.getSuperclass().getDeclaredMethod(STR1_SETTER, String.class);
		}
	}
}
