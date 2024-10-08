/* Copyright (c) 2007 Timothy Wall, All Rights Reserved
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

import org.junit.Ignore;
import org.junit.Test;

import com.sun.unijna.Native;
import com.sun.unijna.Pointer;
import com.sun.unijna.platform.win32.Kernel32;
import com.sun.unijna.platform.win32.WinBase;
import com.sun.unijna.platform.win32.WinNT;
import com.sun.unijna.platform.win32.Wincon;
import com.sun.unijna.platform.win32.WinDef.HWND;
import com.sun.unijna.platform.win32.WinNT.HANDLE;
import com.sun.unijna.platform.win32.Wincon.CONSOLE_SCREEN_BUFFER_INFO;
import com.sun.unijna.platform.win32.Wincon.INPUT_RECORD;
import com.sun.unijna.ptr.IntByReference;

import org.junit.Assume;

/**
 * @author lgoldstein
 */
public class Kernel32ConsoleTest extends AbstractWin32TestSupport {

    private static final Wincon INSTANCE = Kernel32.INSTANCE;

    @Test
    public void testGetConsoleDisplayMode() {
        // If there is no console window, it can't be queried
        Assume.assumeNotNull(INSTANCE.GetConsoleWindow());
        IntByReference curMode = new IntByReference();
        assertCallSucceeded("Initial display mode value retrieval", INSTANCE.GetConsoleDisplayMode(curMode));
    }

    @Test
    public void testConsoleCP() {
        int curCP = INSTANCE.GetConsoleCP();
        // NOTE: we use the same code page value just in case the "SetConsoleCP" call fails
        assertCallSucceeded("Restore CP=" + curCP, INSTANCE.SetConsoleCP(curCP));
    }

    @Test
    public void testConsoleOutputCP() {
        int curCP = INSTANCE.GetConsoleOutputCP();
        // NOTE: we use the same code page value just in case the "SetConsoleOutputCP" call fails
        assertCallSucceeded("Restore CP=" + curCP, INSTANCE.SetConsoleOutputCP(curCP));
    }

    @Test
    public void testGetConsoleWindow() {
        // Only the call is done -- the prior test checked for not-null
        // but running this test from netbeans IDE or console
        // always resulted in NULL and this is a valid value
        // (if there is no console attached)
        HWND hwnd = INSTANCE.GetConsoleWindow();
    }

    @Test
    public void testGetNumberOfConsoleMouseButtons() {
        IntByReference numButtons = new IntByReference(0);
        assertCallSucceeded("Initial display mode value retrieval", INSTANCE.GetNumberOfConsoleMouseButtons(numButtons));
    }

    @Test
    public void testGetStdHandle() {
        for (int nHandle : new int[]{Wincon.STD_INPUT_HANDLE, Wincon.STD_OUTPUT_HANDLE, Wincon.STD_ERROR_HANDLE}) {
            HANDLE hndl = INSTANCE.GetStdHandle(nHandle);
            assertNotEquals("Bad handle value for std handle=" + nHandle, WinBase.INVALID_HANDLE_VALUE, hndl);
            // don't really care what the handle value is - just ensure that API can be called

            /*
             * According to the API documentation:
             *
             * If an application does not have associated standard handles,
             * such as a service running on an interactive desktop, and has
             * not redirected them, the return value is NULL.
             */
            Pointer ptr = hndl.getPointer();
            if (ptr == Pointer.NULL) {
                continue;
            } else {
                assertCallSucceeded("SetStdHandle(" + nHandle + ")", INSTANCE.SetStdHandle(nHandle, hndl));
            }
        }
    }

    @Test
    @Ignore("For some reason we get hr=6 - ERROR_INVALID_HANDLE - even though the documentation stipulates that GetStdHandle can be used")
    public void testGetConsoleMode() {
        for (int nHandle : new int[]{Wincon.STD_INPUT_HANDLE, Wincon.STD_OUTPUT_HANDLE, Wincon.STD_ERROR_HANDLE}) {
            HANDLE hndl = INSTANCE.GetStdHandle(nHandle);
            Pointer ptr = hndl.getPointer();
            if (ptr == Pointer.NULL) {
                continue; // can happen for interactive desktop application
            }

            IntByReference lpMode = new IntByReference(0);
            assertCallSucceeded("GetConsoleMode(" + nHandle + ")", INSTANCE.GetConsoleMode(hndl, lpMode));

            int dwMode = lpMode.getValue();
            // don't really care what the mode is just want to make sure API can be called
            assertCallSucceeded("SetConsoleMode(" + nHandle + "," + dwMode + ")", INSTANCE.SetConsoleMode(hndl, dwMode));
        }
    }

    @Test
    public void testGetConsoleTitle() {
        char[] lpConsoleTitle = new char[WinNT.MAX_PATH];
        int len = INSTANCE.GetConsoleTitle(lpConsoleTitle, lpConsoleTitle.length);
        assertCallSucceeded("GetConsoleTitle", (len > 0));

        String title = Native.toString(lpConsoleTitle);
        assertCallSucceeded("SetConsoleTitle", INSTANCE.SetConsoleTitle(title));
    }

    @Test
    public void testGetConsoleOriginalTitle() {
        char[] lpConsoleTitle = new char[WinNT.MAX_PATH];
        int len = INSTANCE.GetConsoleOriginalTitle(lpConsoleTitle, lpConsoleTitle.length);
        if (len <= 0) {
            int hr = Kernel32.INSTANCE.GetLastError();
            if (hr == 0) { // don't fail the test - we just want to see if the API can be called
                fail("Buffer not large enough to hold the title");
            } else {
                fail("Call failed: hr=0x" + Integer.toHexString(hr));
            }
        }
    }

    @Test
    public void testGetConsoleScreenBufferInfo() {
        HANDLE hConsoleOutput = INSTANCE.GetStdHandle(Wincon.STD_OUTPUT_HANDLE);
        CONSOLE_SCREEN_BUFFER_INFO lpConsoleScreenBufferInfo = new CONSOLE_SCREEN_BUFFER_INFO();

        if (System.console() == null) {
            assertFalse(INSTANCE.GetConsoleScreenBufferInfo(hConsoleOutput, lpConsoleScreenBufferInfo));
        } else {
            assertCallSucceeded("GetConsoleScreenBufferInfo", INSTANCE.GetConsoleScreenBufferInfo(hConsoleOutput, lpConsoleScreenBufferInfo));
        }
    }

    @Test
    public void testReadConsoleInput() {
        HANDLE hConsoleInput = INSTANCE.GetStdHandle(Wincon.STD_INPUT_HANDLE);
        INPUT_RECORD[] lpBuffer = new INPUT_RECORD[1];
        IntByReference lpNumberOfEventsRead = new IntByReference();

        if (System.console() == null) {
            assertFalse(INSTANCE.ReadConsoleInput(hConsoleInput, lpBuffer, lpBuffer.length, lpNumberOfEventsRead));
        } else {
            assertCallSucceeded("ReadConsoleInput", INSTANCE.ReadConsoleInput(hConsoleInput, lpBuffer, lpBuffer.length, lpNumberOfEventsRead));
        }
    }

    @Test
    public void testGetNumberOfConsoleInputEvents() {
        HANDLE hConsoleInput = INSTANCE.GetStdHandle(Wincon.STD_INPUT_HANDLE);
        IntByReference lpcNumberOfEvents = new IntByReference();

        if (System.console() == null) {
            assertFalse(INSTANCE.GetNumberOfConsoleInputEvents(hConsoleInput, lpcNumberOfEvents));
        } else {
            assertCallSucceeded("GetNumberOfConsoleInputEvents", INSTANCE.GetNumberOfConsoleInputEvents(hConsoleInput, lpcNumberOfEvents));
        }
    }

    @Test
    public void testWriteConsole() {
        HANDLE hConsoleOutput = INSTANCE.GetStdHandle(Wincon.STD_OUTPUT_HANDLE);
        String lpBuffer = "WriteConsole";

        if (System.console() == null) {
            assertFalse(INSTANCE.WriteConsole(hConsoleOutput, lpBuffer, lpBuffer.length(), null, null));
        } else {
            assertCallSucceeded("WriteConsole", INSTANCE.WriteConsole(hConsoleOutput, lpBuffer, lpBuffer.length(), null, null));
        }
    }
}
