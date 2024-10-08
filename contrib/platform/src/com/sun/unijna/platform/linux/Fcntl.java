/* Copyright (c) 2020 Daniel Widdis, All Rights Reserved
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

/**
 * POSIX Standard: 6.5 File Control Operations from {@code fcntl.h}
 */
public interface Fcntl {
    /*
     * File access modes for `open' and `fcntl'
     */
    int O_RDONLY = 00; // Open read-only.
    int O_WRONLY = 01; // Open write-only.
    int O_RDWR = 02; // Open read/write.

    /*
     * Bits OR'd into the second argument to open. Note these are defined
     * differently on linux than unix fcntl header
     */
    int O_CREAT     = 000000100; // Create file if it doesn't exist.
    int O_EXCL      = 000000200; // Fail if file already exists.
    int O_TRUNC     = 000001000; // Truncate file to zero length.
    int O_APPEND    = 000002000;
    int O_NONBLOCK  = 000004000;
    int O_DSYNC     = 000010000;
    int O_FASYNC    = 000020000;
    int O_DIRECT    = 000040000;
    int O_LARGEFILE = 000100000;
    int O_DIRECTORY = 000200000;
    int O_NOFOLLOW  = 000400000;
    int O_NOATIME   = 001000000;
    int O_CLOEXEC   = 002000000;
    int __O_SYNC    = 004000000;
    int O_PATH      = 010000000;
    int __O_TMPFILE = 020000000;

    int O_SYNC = (__O_SYNC | O_DSYNC);
    int O_TMPFILE = (__O_TMPFILE | O_DIRECTORY);
    int O_NDELAY = O_NONBLOCK;

    /* Protection bits. */
    int S_IRUSR = 00400; // Read by owner.
    int S_IWUSR = 00200; // Write by owner.
    int S_IXUSR = 00100; // Execute by owner.
    int S_IRWXU = S_IRUSR | S_IWUSR | S_IXUSR;

    int S_IRGRP = 00040; // Read by group.
    int S_IWGRP = 00020; // Write by group.
    int S_IXGRP = 00010; // Execute by group.
    int S_IRWXG = S_IRGRP | S_IWGRP | S_IXGRP;

    int S_IROTH = 00004; // Read by others.
    int S_IWOTH = 00002; // Write by others.
    int S_IXOTH = 00001; // Execute by others.
    int S_IRWXO = S_IROTH | S_IWOTH | S_IXOTH;

    int S_ISUID = 04000; // set-user-ID bit
    int S_ISGID = 02000; // set-group-ID bit (see inode(7)).
    int S_ISVTX = 01000; // sticky bit (see inode(7)).
}
