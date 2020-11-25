package com.github.natanbc.idk.bytecode.util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.UTFDataFormatException;

public class ByteReader {
    private final ByteReader parent;
    private final DataInputStream dis;
    private final int readLimit;
    private int read;
    
    private ByteReader(ByteReader parent, DataInputStream dis, int limit) {
        this.parent = parent;
        this.dis = dis;
        this.readLimit = limit;
    }
    
    public ByteReader(byte[] array, int offset, int length) {
        this(null, new DataInputStream(new ByteArrayInputStream(array, offset, length)), length);
    }
    
    public ByteReader(byte[] array) {
        this(array, 0, array.length);
    }
    
    public ByteReader subReader(int limit) {
        return new ByteReader(this, dis, limit);
    }
    
    private void read(int n) {
        read += n;
        if(parent != null) parent.read(n);
    }
    
    public final int limit() {
        return readLimit > 0 ? readLimit - read : -1;
    }
    
    public final int pos() {
        return read;
    }
    
    public final boolean canRead() {
        return readLimit < 0 || read < readLimit;
    }
    
    public final boolean bool() {
        return u8() != 0;
    }
    
    public final byte u8() {
        try {
            read(1);
            return dis.readByte();
        } catch(IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public final short u16() {
        try {
            read(2);
            return dis.readShort();
        } catch(IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public final int u32() {
        try {
            read(4);
            return dis.readInt();
        } catch(IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public final long u64() {
        try {
            read(8);
            return dis.readLong();
        } catch(IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public final float f32() {
        try {
            read(4);
            return dis.readFloat();
        } catch(IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public final double f64() {
        try {
            read(8);
            return dis.readDouble();
        } catch(IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public final String utf() {
        try {
            var s = dis.readUTF();
            read(Utils.utfLen(s) + 2);
            return s;
        } catch(UTFDataFormatException e) {
            throw new IllegalArgumentException(e);
        } catch(IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
