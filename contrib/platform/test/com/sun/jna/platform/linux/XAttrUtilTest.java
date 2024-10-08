/*
 * Copyright (c) 2018 Václav Haisman, All Rights Reserved
 *
 * The contents of this file is dual-licensed under 2
 * alternative Open Source/Free licenses: LGPL 2.1 or later and
 * Apache License 2.0.
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
package com.sun.unijna.platform.linux;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.junit.Test;

import com.sun.unijna.Memory;
import com.sun.unijna.platform.linux.XAttrUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

public class XAttrUtilTest {
    private static final String TEST_STRING = "Žluťoučký kůň úpěl nebo tak něco.";
    private static final String TEST_STRING_2 = "Příliš žluťoučký kůň úpěl ďábelské ódy.";
    private static final String TEST_EMPTY_STRING = "";
    private static final String TEST_ATTRIBUTE = "user.test";
    private static final String TEST_ATTRIBUTE_FOO = TEST_ATTRIBUTE + ".foo";
    private static final String TEST_ATTRIBUTE_EMPTY = TEST_ATTRIBUTE + ".empty";

    @Test
    public void setXAttr() throws IOException {
        File file = File.createTempFile("xattr", "test");
        file.deleteOnExit();

        XAttrUtil.setXAttr(file.getAbsolutePath(), TEST_ATTRIBUTE, TEST_STRING);
        XAttrUtil.setXAttr(file.getAbsolutePath(), TEST_ATTRIBUTE_FOO, TEST_STRING_2);

        String retrievedValue = XAttrUtil.getXAttr(file.getAbsolutePath(), TEST_ATTRIBUTE);
        assertEquals(TEST_STRING, retrievedValue);

        retrievedValue = XAttrUtil.getXAttr(file.getAbsolutePath(), TEST_ATTRIBUTE_FOO);
        assertEquals(TEST_STRING_2, retrievedValue);

        XAttrUtil.setXAttr(file.getAbsolutePath(), TEST_ATTRIBUTE, TEST_STRING_2);
        retrievedValue = XAttrUtil.lGetXAttr(file.getAbsolutePath(), TEST_ATTRIBUTE);
        assertEquals(TEST_STRING_2, retrievedValue);

        Collection<String> xattrs = XAttrUtil.listXAttr(file.getAbsolutePath());
        assertTrue(xattrs.contains(TEST_ATTRIBUTE));
        assertTrue(xattrs.contains(TEST_ATTRIBUTE_FOO));

        XAttrUtil.removeXAttr(file.getAbsolutePath(), TEST_ATTRIBUTE);
        xattrs = XAttrUtil.lListXAttr(file.getAbsolutePath());
        assertFalse(xattrs.contains(TEST_ATTRIBUTE));
        assertTrue(xattrs.contains(TEST_ATTRIBUTE_FOO));

        file.delete();
    }

    @Test
    public void testGetXAttr() throws IOException {
        File file = File.createTempFile("xattr", "test");
        file.deleteOnExit();

        XAttrUtil.setXAttr(file.getAbsolutePath(), TEST_ATTRIBUTE_EMPTY, TEST_EMPTY_STRING);

        Memory memoryReadMissing = XAttrUtil.getXAttrAsMemory(file.getAbsolutePath(), TEST_ATTRIBUTE_EMPTY);
        byte[] byteReadMissing = XAttrUtil.getXAttrBytes(file.getAbsolutePath(), TEST_ATTRIBUTE_EMPTY);
        String stringReadMissing = XAttrUtil.getXAttr(file.getAbsolutePath(), TEST_ATTRIBUTE_EMPTY);
        assertNull(memoryReadMissing);
        assertNotNull(byteReadMissing);
        assertEquals(0, byteReadMissing.length);
        assertNotNull(stringReadMissing);
        assertTrue(stringReadMissing.isEmpty());

        file.delete();
    }
}