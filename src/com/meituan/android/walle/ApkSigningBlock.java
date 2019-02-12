package com.meituan.android.walle;

import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * https://source.android.com/security/apksigning/v2.html
 * https://en.wikipedia.org/wiki/Zip_(file_format)
 */
class ApkSigningBlock {
    // The format of the APK Signing Block is as follows (all numeric fields are little-endian):

    // .size of block in bytes (excluding this field) (uint64)
    // .Sequence of uint64-length-prefixed ID-value pairs:
    //   *ID (uint32)
    //   *value (variable-length: length of the pair - 4 bytes)
    // .size of block in bytes—same as the very first field (uint64)
    // .magic “APK Sig Block 42” (16 bytes)

    // FORMAT:
    // OFFSET       DATA TYPE  DESCRIPTION
    // * @+0  bytes uint64:    size in bytes (excluding this field)
    // * @+8  bytes payload
    // * @-24 bytes uint64:    size in bytes (same as the one above)
    // * @-16 bytes uint128:   magic

    // payload 有 8字节的大小，4字节的ID，还有payload的内容组成

    private final List<ApkSigningPayload> payloads;

    ApkSigningBlock() {
        super();

        payloads = new ArrayList<ApkSigningPayload>();
    }

    public final List<ApkSigningPayload> getPayloads() {
        return payloads;
    }

    public void addPayload(final ApkSigningPayload payload) {
        payloads.add(payload);
    }

    public long writeApkSigningBlock(final DataOutput dataOutput) throws IOException {
        long length = 24; // 24 = 8(size of block in bytes—same as the very first field (uint64)) + 16 (magic “APK Sig Block 42” (16 bytes))
        for (int index = 0; index < payloads.size(); ++index) {
            final ApkSigningPayload payload = payloads.get(index);
            final byte[] bytes = payload.getByteBuffer();
            length += 12 + bytes.length; // 12 = 8(uint64-length-prefixed) + 4 (ID (uint32))
        }

        //size of block
        ByteBuffer byteBuffer = ByteBuffer.allocate(8); // Long.BYTES
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putLong(length);
        byteBuffer.flip();
        dataOutput.write(byteBuffer.array());

        //ID-value，它由一个8字节的长度标示＋4字节的ID＋它的负载组成
        // payloads
        for (int index = 0; index < payloads.size(); ++index) {
            final ApkSigningPayload payload = payloads.get(index);
            //获取id值的字节数组
            final byte[] bytes = payload.getByteBuffer();

            //带 uint64 长度前缀的“ID-值”对序列
            byteBuffer = ByteBuffer.allocate(8); // Long.BYTES 64位 8个字节
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            //写入签名的长度
            byteBuffer.putLong(bytes.length + (8 - 4)); // Long.BYTES - Integer.BYTES 4个字节id
            byteBuffer.flip();
            dataOutput.write(byteBuffer.array());

            byteBuffer = ByteBuffer.allocate(4); // Integer.BYTES 32位 4个字节
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            byteBuffer.putInt(payload.getId());
            byteBuffer.flip();
            //写入id
            dataOutput.write(byteBuffer.array());

            //写入负载值
            dataOutput.write(bytes);
        }

        // size of block
        byteBuffer = ByteBuffer.allocate(8); // Long.BYTES
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putLong(length);
        byteBuffer.flip();
        dataOutput.write(byteBuffer.array());

        // magic 分区
        byteBuffer = ByteBuffer.allocate(8); // Long.BYTES
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putLong(ApkUtil.APK_SIG_BLOCK_MAGIC_LO);
        byteBuffer.flip();
        dataOutput.write(byteBuffer.array());

        byteBuffer = ByteBuffer.allocate(8); // Long.BYTES
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putLong(ApkUtil.APK_SIG_BLOCK_MAGIC_HI);
        byteBuffer.flip();
        dataOutput.write(byteBuffer.array());

        return length;
    }
}
