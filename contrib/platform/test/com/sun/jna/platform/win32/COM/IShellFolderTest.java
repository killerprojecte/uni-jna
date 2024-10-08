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
package com.sun.unijna.platform.win32.COM;

import com.sun.unijna.Pointer;
import com.sun.unijna.platform.win32.Guid;
import com.sun.unijna.platform.win32.Ole32;
import com.sun.unijna.platform.win32.Shell32;
import com.sun.unijna.platform.win32.ShlObj;
import com.sun.unijna.platform.win32.Shlwapi;
import com.sun.unijna.platform.win32.WinNT;
import com.sun.unijna.platform.win32.COM.COMUtils;
import com.sun.unijna.platform.win32.COM.IEnumIDList;
import com.sun.unijna.platform.win32.COM.IShellFolder;
import com.sun.unijna.platform.win32.Guid.REFIID;
import com.sun.unijna.platform.win32.ShTypes.STRRET;
import com.sun.unijna.ptr.IntByReference;
import com.sun.unijna.ptr.PointerByReference;

import junit.framework.TestCase;

public class IShellFolderTest extends TestCase {
    static {
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
    }

    private IShellFolder psfMyComputer;

    public static WinNT.HRESULT BindToCsidl(int csidl, Guid.REFIID riid, PointerByReference ppv) {
        WinNT.HRESULT hr;
        PointerByReference pidl = new PointerByReference();
        hr = Shell32.INSTANCE.SHGetSpecialFolderLocation(null, csidl, pidl);
        assertTrue(COMUtils.SUCCEEDED(hr));
        PointerByReference psfDesktopPTR = new PointerByReference();
        hr = Shell32.INSTANCE.SHGetDesktopFolder(psfDesktopPTR);
        assertTrue(COMUtils.SUCCEEDED(hr));
        IShellFolder psfDesktop = IShellFolder.Converter.PointerToIShellFolder(psfDesktopPTR);
        short cb = pidl.getValue().getShort(0); // See http://blogs.msdn.com/b/oldnewthing/archive/2011/08/30/10202076.aspx for explanation about this bit
        if (cb != 0) {
            hr = psfDesktop.BindToObject(pidl.getValue(), null, riid, ppv);
        } else {
            hr = psfDesktop.QueryInterface(riid, ppv);
        }
        psfDesktop.Release();
        Ole32.INSTANCE.CoTaskMemFree(pidl.getValue());
        return hr;
    }

    public void setUp() throws Exception {
        WinNT.HRESULT hr = Ole32.INSTANCE.CoInitialize(null);
        assertTrue(COMUtils.SUCCEEDED(hr));
        PointerByReference psfMyComputerPTR = new PointerByReference(Pointer.NULL);
        hr = BindToCsidl(ShlObj.CSIDL_DRIVES, new REFIID(IShellFolder.IID_ISHELLFOLDER), psfMyComputerPTR);
        assertTrue(COMUtils.SUCCEEDED(hr));
        psfMyComputer = IShellFolder.Converter.PointerToIShellFolder(psfMyComputerPTR);
    }

    public void tearDown() throws Exception {
        psfMyComputer.Release();
        Ole32.INSTANCE.CoUninitialize();
    }

    public void testEnumObjects() throws Exception {
        PointerByReference peidlPTR = new PointerByReference();
        int SHCONTF_FOLDERS = 0x20;
        int SHCONTF_NONFOLDERS = 0x40;
        boolean sawNames = false;

        WinNT.HRESULT hr = psfMyComputer.EnumObjects(null,
                SHCONTF_FOLDERS | SHCONTF_NONFOLDERS, peidlPTR);
        assertTrue(COMUtils.SUCCEEDED(hr));
        IEnumIDList peidl = IEnumIDList.Converter.PointerToIEnumIDList(peidlPTR);
        PointerByReference pidlItem = new PointerByReference();
        while (peidl.Next(1, pidlItem, null).intValue() == COMUtils.S_OK) {
            STRRET sr = new STRRET();
            hr = psfMyComputer.GetDisplayNameOf(pidlItem.getValue(), 0, sr);
            assertTrue(COMUtils.SUCCEEDED(hr));
            PointerByReference pszName = new PointerByReference();
            hr = Shlwapi.INSTANCE.StrRetToStr(sr, pidlItem.getValue(), pszName);
            assertTrue(COMUtils.SUCCEEDED(hr));
            String wideString = pszName.getValue().getWideString(0);
            if (wideString != null && wideString.length() > 0)
                sawNames = true;
            Ole32.INSTANCE.CoTaskMemFree(pszName.getValue());
            Ole32.INSTANCE.CoTaskMemFree(pidlItem.getValue());
        }
        peidl.Release();
        assertTrue(sawNames); // We should see at least one item with a name
    }

    public void testParseDisplayName() throws Exception {
        String directory = System.getenv("WinDir");

        IntByReference pchEaten = new IntByReference();
        PointerByReference ppidl = new PointerByReference();
        IntByReference pdwAttributes = new IntByReference();
        PointerByReference desktopFolder = new PointerByReference();

        WinNT.HRESULT hResult = Shell32.INSTANCE.SHGetDesktopFolder(desktopFolder);
        assertEquals(COMUtils.S_OK, hResult.intValue());

        IShellFolder shellFolder = IShellFolder.Converter.PointerToIShellFolder(desktopFolder);
        hResult = shellFolder.ParseDisplayName(null, null, directory, pchEaten, ppidl, pdwAttributes);
        assertEquals(COMUtils.S_OK, hResult.intValue());
    }
}