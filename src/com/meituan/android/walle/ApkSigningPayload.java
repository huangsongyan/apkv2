package com.meituan.android.walle;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

class ApkSigningPayload {
    private final int id;
    private final ByteBuffer buffer;

    ApkSigningPayload(final int id, final ByteBuffer buffer) {
        super();
        this.id = id;
        if (buffer.order() != ByteOrder.LITTLE_ENDIAN) {
            throw new IllegalArgumentException("ByteBuffer byte order must be little endian");
        }
        this.buffer = buffer;
    }

    public int getId() {
        return id;
    }

    //从缓存区获取数据
    public byte[] getByteBuffer() {
        final byte[] array = buffer.array();
        final int arrayOffset = buffer.arrayOffset();
        System.out.println("array:"+array.length);
        System.out.println("arrayOffset:"+arrayOffset);
        System.out.println("buffer.position():"+buffer.position());
        System.out.println("buffer.limit():"+buffer.limit());
        byte[] bytes = Arrays.copyOfRange(array, arrayOffset + buffer.position(),
                arrayOffset + buffer.limit());
        System.out.println("bytes:"+bytes.length+"\n");
        return bytes;
    }
}
