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
package com.sun.unijna.platform.win32.COM.util;

import com.sun.unijna.platform.win32.AbstractWin32TestSupport;
import static com.sun.unijna.platform.win32.AbstractWin32TestSupport.checkCOMRegistered;

import com.sun.unijna.Pointer;
import com.sun.unijna.platform.win32.Ole32;
import com.sun.unijna.platform.win32.COM.COMUtils;
import com.sun.unijna.platform.win32.COM.util.AbstractComEventCallbackListener;
import com.sun.unijna.platform.win32.COM.util.IComEventCallbackCookie;
import com.sun.unijna.platform.win32.COM.util.IConnectionPoint;
import com.sun.unijna.platform.win32.COM.util.IDispatch;
import com.sun.unijna.platform.win32.COM.util.IUnknown;
import com.sun.unijna.platform.win32.COM.util.ObjectFactory;
import com.sun.unijna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.unijna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.unijna.platform.win32.COM.util.annotation.ComObject;
import com.sun.unijna.platform.win32.COM.util.annotation.ComProperty;
import com.sun.unijna.platform.win32.OaIdl.DISPID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import org.junit.Assume;

public class IDispatchTest {
    private boolean initialized = false;
    private ObjectFactory factory;

    @Before
    public void before() {
        Assume.assumeTrue("Could not find registration", checkCOMRegistered("{0002DF01-0000-0000-C000-000000000046}"));

        AbstractWin32TestSupport.killProcessByName("iexplore.exe");
        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException ex) {}
        COMUtils.checkRC(Ole32.INSTANCE.CoInitializeEx(Pointer.NULL, Ole32.COINIT_MULTITHREADED));
        initialized = true;
        this.factory = new ObjectFactory();
    }

    @After
    public void after() {
        if(this.factory != null) {
            this.factory.disposeAll();
        }
        if(initialized) {
            Ole32.INSTANCE.CoUninitialize();
            initialized = false;
        }
    }

    @Test
    public void testDispatchBaseOnMethodName() throws InterruptedException {
        ComInternetExplorerMethodname ieApp = factory.createObject(ComInternetExplorerMethodname.class);

        // Test getting property
        assertFalse(ieApp.getVisible());

        // Test setting property
        ieApp.setVisible(Boolean.TRUE);
        assertTrue(ieApp.getVisible());

        // Check navigate function and with that the method invocation
        assertTrue(ieApp.getLocationURL().isEmpty());

        ieApp.Navigate2("https://github.com/java-native-access/");

        // Check max. 2s if Navigation happend
        boolean navigationHappend = false;
        for (int i = 0; i < 10; i++) {
            String url = ieApp.getLocationURL();
            if (!url.isEmpty()) {
                navigationHappend = true;
                break;
            } else {
                Thread.sleep(200);
            }
        }

        ieApp.Quit();

        assertTrue(navigationHappend);
    }

    @ComObject(progId = "Internet.Explorer.1", clsId = "{0002DF01-0000-0000-C000-000000000046}")
    interface ComInternetExplorerMethodname {
        @ComProperty
        String getLocationURL();

        @ComMethod
        void Navigate2(String url);

        @ComProperty
        Boolean getVisible();

        @ComProperty
        void setVisible(Boolean visible);

        @ComMethod
        void Quit();
    }

    @Test
    public void testDispatchBaseOnNamed() throws InterruptedException {
        ComInternetExplorerNamed ieApp = factory.createObject(ComInternetExplorerNamed.class);

        // Test getting property
        assertFalse(ieApp.getVisible_MOD());

        // Test setting property
        ieApp.setVisible_MOD(Boolean.TRUE);
        assertTrue(ieApp.getVisible_MOD());

        // Check navigate function and with that the method invocation
        assertTrue(ieApp.getLocationURL_MOD().isEmpty());

        ieApp.Navigate2_MOD("https://github.com/java-native-access/");

        // Check max. 10s if Navigation happend
        boolean navigationHappend = false;
        for (int i = 0; i < 50; i++) {
            String url = ieApp.getLocationURL_MOD();
            if (!url.isEmpty()) {
                navigationHappend = true;
                break;
            } else {
                Thread.sleep(200);
            }
        }

        ieApp.Quit_MOD();

        assertTrue(navigationHappend);
    }

    @ComObject(progId = "Internet.Explorer.1", clsId = "{0002DF01-0000-0000-C000-000000000046}")
    interface ComInternetExplorerNamed {
        @ComProperty(name="LocationURL")
        String getLocationURL_MOD();

        @ComMethod(name="Navigate2")
        void Navigate2_MOD(String url);

        @ComProperty(name="Visible")
        Boolean getVisible_MOD();

        @ComProperty(name="Visible")
        void setVisible_MOD(Boolean visible);

        @ComMethod(name="Quit")
        void Quit_MOD();
    }

    @Test
    public void testDispatchBaseOnDISPID() throws InterruptedException {
        ComInternetExplorerDISPID ieApp = factory.createObject(ComInternetExplorerDISPID.class);

        // Test getting property
        assertFalse(ieApp.getVisible_MOD());

        // Test setting property
        ieApp.setVisible_MOD(Boolean.TRUE);
        assertTrue(ieApp.getVisible_MOD());

        // Check navigate function and with that the method invocation
        assertTrue(ieApp.getLocationURL_MOD().isEmpty());

        ieApp.Navigate2_MOD("https://github.com/java-native-access/");

        // Check max. 2s if Navigation happend
        boolean navigationHappend = false;
        for (int i = 0; i < 50; i++) {
            String url = ieApp.getLocationURL_MOD();
            if (!url.isEmpty()) {
                navigationHappend = true;
                break;
            } else {
                Thread.sleep(200);
            }
        }

        ieApp.Quit_MOD();

        assertTrue(navigationHappend);
    }

    @ComObject(progId = "Internet.Explorer.1", clsId = "{0002DF01-0000-0000-C000-000000000046}")
    interface ComInternetExplorerDISPID {
        @ComProperty(dispId = 0x000000d3)
        String getLocationURL_MOD();

        @ComMethod(dispId = 0x000001f4)
        void Navigate2_MOD(String url);

        @ComProperty(dispId = 0x00000192)
        Boolean getVisible_MOD();

        @ComProperty(dispId = 0x00000192)
        void setVisible_MOD(Boolean visible);

        @ComMethod(dispId = 0x0000012c)
        void Quit_MOD();
    }

    @Test
    public void testIDispatchName() throws InterruptedException {
        ComInternetExplorerIDispatch ieApp = factory.createObject(ComInternetExplorerIDispatch.class);

        // Test getting property
        assertFalse(ieApp.getProperty(Boolean.class, "Visible"));

        // Test setting property
        ieApp.setProperty("Visible", Boolean.TRUE);
        assertTrue(ieApp.getProperty(Boolean.class, "Visible"));

        // Check navigate function and with that the method invocation
        assertTrue(ieApp.getProperty(String.class, "LocationURL").isEmpty());

        ieApp.invokeMethod(Void.class, "Navigate2", "https://github.com/java-native-access/");

        // Check max. 10s if Navigation happend
        boolean navigationHappend = false;
        for (int i = 0; i < 50; i++) {
            String url = ieApp.getProperty(String.class, "LocationURL");
            if (!url.isEmpty()) {
                navigationHappend = true;
                break;
            } else {
                Thread.sleep(200);
            }
        }

        ieApp.invokeMethod(Void.class, "Quit");

        assertTrue(navigationHappend);
    }

    @Test
    public void testIDispatchDISPID() throws InterruptedException {
        DISPID locationURL = new DISPID(0x000000d3);
        DISPID visible = new DISPID(0x00000192);
        DISPID quit = new DISPID(0x0000012c);
        DISPID navigate2 = new DISPID(0x000001f4);

        ComInternetExplorerIDispatch ieApp = factory.createObject(ComInternetExplorerIDispatch.class);

        // Test getting property
        assertFalse(ieApp.getProperty(Boolean.class, visible));

        // Test setting property
        ieApp.setProperty(visible, Boolean.TRUE);
        assertTrue(ieApp.getProperty(Boolean.class, visible));

        // Check navigate function and with that the method invocation
        assertTrue(ieApp.getProperty(String.class, locationURL).isEmpty());

        ieApp.invokeMethod(Void.class, navigate2, "https://github.com/java-native-access/");

        // Check max. 10s if Navigation happend
        boolean navigationHappend = false;
        for (int i = 0; i < 50; i++) {
            String url = ieApp.getProperty(String.class, locationURL);
            if (!url.isEmpty()) {
                navigationHappend = true;
                break;
            } else {
                Thread.sleep(200);
            }
        }

        ieApp.invokeMethod(Void.class, quit);

        assertTrue(navigationHappend);
    }

    @ComObject(progId = "Internet.Explorer.1", clsId = "{0002DF01-0000-0000-C000-000000000046}")
    interface ComInternetExplorerIDispatch extends IDispatch {
    }

    @Test
    public void testCallbackAll() throws InterruptedException {
        ComInternetExplorerEventTest ieApp = factory.createObject(ComInternetExplorerEventTest.class);
        ieApp.setVisible(false);

        DWebBrowserEvents2_Listener listener1 = new DWebBrowserEvents2_Listener();
        DWebBrowserEvents2_Listener listener2 = new DWebBrowserEvents2_Listener();
        DWebBrowserEvents2_Listener listener3 = new DWebBrowserEvents2_Listener();
        DWebBrowserEvents2_Listener listener4 = new DWebBrowserEvents2_Listener();
        DWebBrowserEvents2_Listener listener5 = new DWebBrowserEvents2_Listener();

        IComEventCallbackCookie cookie1 = ieApp.advise(DWebBrowserEvents2EventTestIDispatch.class, listener1);
        IComEventCallbackCookie cookie2 = ieApp.advise(DWebBrowserEvents2EventTestIUnknown.class, listener2);
        IComEventCallbackCookie cookie3 = ieApp.advise(DWebBrowserEvents2EventTestUtilIDispatch.class, listener3);
        IComEventCallbackCookie cookie4 = ieApp.advise(DWebBrowserEvents2EventTestUtilIUnknown.class, listener4);
        IComEventCallbackCookie cookie5 = ieApp.advise(DWebBrowserEvents2EventTestSubclass.class, listener5);

        ieApp.Navigate2("https://github.com/");

        for(int i = 0; i < 50; i++) {
            Thread.sleep(200);
            if(listener1.IDispatch && listener2.IUnknown && listener3.UtilIDispatch && listener4.UtilIUnknown && listener5.Subclass) {
                break;
            }
        }

        ieApp.unadvise(DWebBrowserEvents2EventTestIDispatch.class, cookie1);
        ieApp.unadvise(DWebBrowserEvents2EventTestIUnknown.class, cookie2);
        ieApp.unadvise(DWebBrowserEvents2EventTestUtilIDispatch.class, cookie3);
        ieApp.unadvise(DWebBrowserEvents2EventTestUtilIUnknown.class, cookie4);
        ieApp.unadvise(DWebBrowserEvents2EventTestSubclass.class, cookie5);

        ieApp.Quit();

        assertTrue(listener1.IDispatch);
        assertFalse(listener1.IUnknown);
        assertFalse(listener1.UtilIDispatch);
        assertFalse(listener1.UtilIUnknown);
        assertFalse(listener1.Subclass);

        assertFalse(listener2.IDispatch);
        assertTrue(listener2.IUnknown);
        assertFalse(listener2.UtilIDispatch);
        assertFalse(listener2.UtilIUnknown);
        assertFalse(listener2.Subclass);

        assertFalse(listener3.IDispatch);
        assertFalse(listener3.IUnknown);
        assertTrue(listener3.UtilIDispatch);
        assertFalse(listener3.UtilIUnknown);
        assertFalse(listener3.Subclass);

        assertFalse(listener4.IDispatch);
        assertFalse(listener4.IUnknown);
        assertFalse(listener4.UtilIDispatch);
        assertTrue(listener4.UtilIUnknown);
        assertFalse(listener4.Subclass);

        assertFalse(listener5.IDispatch);
        assertFalse(listener5.IUnknown);
        assertFalse(listener5.UtilIDispatch);
        assertFalse(listener5.UtilIUnknown);
        assertTrue(listener5.Subclass);
    }

    @ComObject(progId = "Internet.Explorer.1", clsId = "{0002DF01-0000-0000-C000-000000000046}")
    interface ComInternetExplorerEventTest extends ComIWebBrowser2EventTest {
    }

    @ComInterface(iid = "{D30C1661-CDAF-11D0-8A3E-00C04FC9E26E}")
    interface ComIWebBrowser2EventTest extends IUnknown, IConnectionPoint {

        @ComProperty
        boolean getVisible();

        @ComProperty
        void setVisible(boolean value);

        @ComMethod
        void Quit();

        @ComMethod
        void Navigate2(String url);
    }

    @ComInterface(iid = "{34A715A0-6587-11D0-924A-0020AFC7AC4D}")
    interface DWebBrowserEvents2EventTestUtilIUnknown {

        @ComMethod(dispId = 0x000000fc)
        void NavigateComplete2UtilIUnknown(IUnknown source, Object url);
    }

    @ComInterface(iid = "{34A715A0-6587-11D0-924A-0020AFC7AC4D}")
    interface DWebBrowserEvents2EventTestUtilIDispatch {

        @ComMethod(dispId = 0x000000fc)
        void NavigateComplete2UtilIDispatch(IDispatch source, Object url);
    }

    @ComInterface(iid = "{34A715A0-6587-11D0-924A-0020AFC7AC4D}")
    interface DWebBrowserEvents2EventTestIUnknown {

        @ComMethod(dispId = 0x000000fc)
        void NavigateComplete2IUnknown(com.sun.unijna.platform.win32.COM.IUnknown source, Object url);
    }

    @ComInterface(iid = "{34A715A0-6587-11D0-924A-0020AFC7AC4D}")
    interface DWebBrowserEvents2EventTestIDispatch {

        @ComMethod(dispId = 0x000000fc)
        void NavigateComplete2IDispatch(com.sun.unijna.platform.win32.COM.IDispatch source, Object url);
    }

    @ComInterface(iid = "{34A715A0-6587-11D0-924A-0020AFC7AC4D}")
    interface DWebBrowserEvents2EventTestSubclass {

        @ComMethod(dispId = 0x000000fc)
        void NavigateComplete2Subclass(ComIWebBrowser2EventTest source, Object url);
    }

    class DWebBrowserEvents2_Listener extends AbstractComEventCallbackListener implements DWebBrowserEvents2EventTestUtilIUnknown, DWebBrowserEvents2EventTestUtilIDispatch, DWebBrowserEvents2EventTestIUnknown, DWebBrowserEvents2EventTestIDispatch, DWebBrowserEvents2EventTestSubclass {

        volatile boolean UtilIUnknown = false;
        volatile boolean UtilIDispatch = false;
        volatile boolean IUnknown = false;
        volatile boolean IDispatch = false;
        volatile boolean Subclass = false;

        @Override
        public void errorReceivingCallbackEvent(String message, Exception exception) {
//            System.err.println(message);
        }

        public void NavigateComplete2UtilIUnknown(IUnknown source, Object url) {
            if (url instanceof String && ((String) url).startsWith("https://github.com/")) {
                UtilIUnknown = true;
            }
        }

        public void NavigateComplete2UtilIDispatch(IDispatch source, Object url) {
            if (url instanceof String && ((String) url).startsWith("https://github.com/")) {
                UtilIDispatch = true;
            }
        }

        public void NavigateComplete2IUnknown(com.sun.unijna.platform.win32.COM.IUnknown source, Object url) {
            if (url instanceof String && ((String) url).startsWith("https://github.com/")) {
                IUnknown = true;
            }
        }

        public void NavigateComplete2IDispatch(com.sun.unijna.platform.win32.COM.IDispatch source, Object url) {
            if (url instanceof String && ((String) url).startsWith("https://github.com/")) {
                IDispatch = true;
            }
        }

        public void NavigateComplete2Subclass(ComIWebBrowser2EventTest source, Object url) {
            if (url instanceof String && ((String) url).startsWith("https://github.com/")) {
                Subclass = true;
            }
        }
    }
}
