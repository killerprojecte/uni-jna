/*
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

package com.sun.unijna.platform.win32.COM.util.office.word;

import com.sun.unijna.platform.win32.COM.util.IComEnum;

/**
 * <p>uuid({147553BC-4DC5-3681-A445-D1C4BEA414AD})</p>
 */
public enum WdExportOptimizeFor implements IComEnum {

    /**
     * (0)
     */
    wdExportOptimizeForPrint(0),

    /**
     * (1)
     */
    wdExportOptimizeForOnScreen(1),
    ;

    private WdExportOptimizeFor(long value) {
        this.value = value;
    }
    private long value;

    public long getValue() {
        return this.value;
    }
}