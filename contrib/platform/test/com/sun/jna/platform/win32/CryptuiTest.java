/* Copyright (c) 2018 Roshan Muralidharan, All Rights Reserved
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
package com.sun.unijna.platform.win32;

import com.sun.unijna.Native;
import com.sun.unijna.platform.win32.Cryptui;
import com.sun.unijna.platform.win32.WinCrypt.*;
import com.sun.unijna.ptr.PointerByReference;

import junit.framework.TestCase;

/**
 * @author roshan[dot]muralidharan[at]cerner[dot]com
 */
public class CryptuiTest extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(CryptuiTest.class);
    }

    public void testCryptUIDlgSelectCertificateFromStore() {
        CERT_CONTEXT.ByReference context = Cryptui.INSTANCE.CryptUIDlgSelectCertificateFromStore(null, null, "", "", 2, 0, null);

        assertNull("Context should be null as a valid certificate store handle was not provided.", context);
    }
}