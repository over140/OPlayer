/*
 * Copyright (C) 2012 YIXIA.COM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nmbb.oplayer.util;

import java.lang.reflect.Array;

public class ArrayUtils {
	public static <T> T[] concat(T[] A, T[] B) {
		final Class<?> typeofA = A.getClass().getComponentType();
		@SuppressWarnings("unchecked")
		T[] C = (T[]) Array.newInstance(typeofA, A.length + B.length);
		System.arraycopy(A, 0, C, 0, A.length);
		System.arraycopy(B, 0, C, A.length, B.length);

		return C;
	}
	
	public static <T> int indexOf(T[] array, T s) {
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(s)) {
				return i;
			}
		}
		return -1;
	}
}
