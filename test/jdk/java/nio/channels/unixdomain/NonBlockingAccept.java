/*
 * Copyright (c) 2020, 2024, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/**
 * @test
 * @bug 8245194
 * @library /test/lib
 * @run main/othervm NonBlockingAccept
 */

import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;

import jtreg.SkippedException;

public class NonBlockingAccept {

    static void checkSupported() {
        try {
            SocketChannel.open(StandardProtocolFamily.UNIX).close();
        } catch (UnsupportedOperationException e) {
            throw new SkippedException("Unix domain sockets not supported");
        } catch (Exception e) {
            // continue
        }
    }

    public static void main(String[] args) throws Exception {

        checkSupported();
        UnixDomainSocketAddress addr = null;

        try (ServerSocketChannel serverSocketChannel =
                                 ServerSocketChannel.open(StandardProtocolFamily.UNIX)) {
            //non blocking mode
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(null);
            addr = (UnixDomainSocketAddress) serverSocketChannel.getLocalAddress();
            SocketChannel socketChannel = serverSocketChannel.accept();
            System.out.println("The socketChannel is : expected Null " + socketChannel);
            if (socketChannel != null)
                throw new RuntimeException("expected null");
            // or exception could be thrown otherwise
        } finally {
            if (addr != null) {
                Files.deleteIfExists(addr.getPath());
            }
        }
    }
}

