/* Copyright (c) 2017 Daniel Widdis, All Rights Reserved
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
package com.sun.unijna.platform.linux;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;

import com.sun.unijna.Native;
import com.sun.unijna.platform.linux.LibC;
import com.sun.unijna.platform.linux.LibC.Statvfs;
import com.sun.unijna.platform.linux.LibC.Sysinfo;
import com.sun.unijna.platform.unix.LibCAPI.size_t;
import com.sun.unijna.platform.unix.LibCAPI.ssize_t;

import junit.framework.TestCase;

/**
 * Exercise the {@link LibC} class.
 */
public class LibCTest extends TestCase {

    @Test
    public void testSizeTypes() {
        long VALUE = 20;
        size_t st = new size_t(VALUE);
        assertEquals("Wrong size_t value", VALUE, st.longValue());
        ssize_t sst = new ssize_t(VALUE);
        assertEquals("Wrong ssize_t value", VALUE, sst.longValue());
    }

    @Test
    public void testSysinfo() {
        Sysinfo info = new Sysinfo();
        assertEquals(0, LibC.INSTANCE.sysinfo(info));

        // Get loadavg for comparison (rounds to nearest hundredth)
        double[] loadavg = new double[3];
        LibC.INSTANCE.getloadavg(loadavg, 3);
        // Sysinfo loadavg longs must be divided by 2^16
        for (int i = 0; i < 3; i++) {
            assertEquals(loadavg[i], info.loads[i].longValue() / (double) (1 << 16), 0.02);
        }
        assertTrue(info.uptime.longValue() > 0);
        assertTrue(info.totalram.longValue() > 0);
        assertTrue(info.freeram.longValue() <= info.totalram.longValue());
        assertTrue(info.freeswap.longValue() <= info.totalswap.longValue());
    }

    @Test
    public void testStatvfs() throws IOException, InterruptedException {
        Statvfs vfs = new Statvfs();

        String testDirectory = "/";

        int ret = LibC.INSTANCE.statvfs(testDirectory, vfs);
        int errno = Native.getLastError();

        assertEquals(String.format("statvfs call failed for: %s, errno: %d", testDirectory, errno), 0, ret);

        FileStore fstore = Files.getFileStore(Paths.get(testDirectory));
        assertEquals(fstore.getTotalSpace(), vfs.f_blocks.longValue() * vfs.f_bsize.longValue());
        assertTrue(vfs.f_bsize.longValue() > 0);
        assertTrue(vfs.f_bfree.longValue() <= vfs.f_blocks.longValue());
        assertTrue(vfs.f_ffree.longValue() <= vfs.f_files.longValue());
        assertTrue(vfs.f_namemax.longValue() > 0);
    }
}
