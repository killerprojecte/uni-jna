/* Copyright (c) 2011 Timothy Wall, All Rights Reserved
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
package com.sun.unijna.platform.wince;

import com.sun.unijna.Library;
import com.sun.unijna.Native;
import com.sun.unijna.platform.win32.WinNT;
import com.sun.unijna.win32.W32APIOptions;

/** Definition <code>coredll.dll</code>.
    Add other win32 interface mappings as needed.
 */
public interface CoreDLL extends WinNT, Library {

    CoreDLL INSTANCE = Native.load("coredll", CoreDLL.class, W32APIOptions.UNICODE_OPTIONS);

}
