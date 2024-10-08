/* Copyright (c) 2018 Matthias Bläsing, All Rights Reserved
 *
 * The contents of this file is dual-licensed under 2
 * alternative Open Source/Free licenses: LGPL 2.1 or later and
 * Apache License 2.0. (starting with JNA version 4.0.0).
 *
 * You can freely decide which license you want to apply to
 * the project.
 *
 * You may obtain a copy of the LGPL License at:
 *
 * http://www.gnu.org/licenses/licenses.html
 *
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "LGPL2.1".
 *
 * You may obtain a copy of the Apache License at:
 *
 * http://www.apache.org/licenses/
 *
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "AL2.0".
 */
package com.sun.unijna;

import java.util.Collections;

import com.sun.unijna.FromNativeConverter;
import com.sun.unijna.Library;
import com.sun.unijna.Native;
import com.sun.unijna.NativeLibrary;
import com.sun.unijna.ToNativeConverter;
import com.sun.unijna.TypeMapper;

/**
 * Variant of {@link ArgumentsMarshalNullableTest} for direct mapped interfaces.
 */
public class DirectArgumentsMarshalNullableTest extends ArgumentsMarshalNullableTest {

    public static class DirectTestLibrary implements TestLibrary {

        public native Int16NativeMapped returnInt16Argument(Int16NativeMapped i);

        public native Int16Integer returnInt16Argument(Int16Integer i);

        public native Int16 returnInt16Argument(Int16 i);

        static {
            NativeLibrary library = NativeLibrary.getInstance("testlib", Collections.singletonMap(Library.OPTION_TYPE_MAPPER, new TypeMapper() {
                public FromNativeConverter getFromNativeConverter(Class<?> javaType) {
                    if (javaType == Int16.class) {
                        return Int16.fromNative;
                    } else {
                        return null;
                    }
                }

                public ToNativeConverter getToNativeConverter(Class<?> javaType) {
                    if (javaType == Int16.class) {
                        return Int16.toNative;
                    } else {
                        return null;
                    }
                }
            }));
            Native.register(library);
        }
    }

    /* Override original. */
    @Override
    protected void setUp() {
        lib = new DirectTestLibrary();
    }

    public static void main(java.lang.String[] argList) {
        junit.textui.TestRunner.run(DirectArgumentsMarshalNullableTest.class);
    }

}
