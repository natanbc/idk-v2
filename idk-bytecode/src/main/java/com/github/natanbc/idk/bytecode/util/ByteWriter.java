package com.github.natanbc.idk.bytecode.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UTFDataFormatException;

public class ByteWriter {
    private final OverwritableByteArrayOutputStream baos = new OverwritableByteArrayOutputStream();
    private final DataOutputStream dos = new DataOutputStream(baos);
    
    public final DataOutputStream sink() {
        return dos;
    }
    
    public final int size() {
        try {
            dos.flush();
        } catch(IOException e) {
            throw new IllegalStateException(e);
        }
        return baos.position();
    }
    
    public final void bool(boolean value) {
        u8(value ? 1 : 0);
    }
    
    public final void u8(int value) {
        Utils.validateU8(value, "Value is not a valid u8");
        u8((byte)value);
    }
    
    public final void u8(byte value) {
        try {
            dos.writeByte(value & 0xFF);
        } catch(IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public final void u16(int value) {
        Utils.validateU16(value, "Value is not a valid u16");
        u16((short)value);
    }
    
    public final void u16(short value) {
        try {
            dos.writeShort(value & 0xFFFF);
        } catch(IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public final void u32(int value) {
        try {
            dos.writeInt(value);
        } catch(IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public final void u64(long value) {
        try {
            dos.writeLong(value);
        } catch(IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public final void f32(float value) {
        try {
            dos.writeFloat(value);
        } catch(IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public final void f64(double value) {
        try {
            dos.writeDouble(value);
        } catch(IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public final void patchBool(int offset, boolean value) {
        int old = baos.position();
        baos.position(offset);
        try {
            bool(value);
        } finally {
            baos.position(old);
        }
    }
    
    public final void patchU8(int offset, int value) {
        int old = baos.position();
        baos.position(offset);
        try {
            u8(value);
        } finally {
            baos.position(old);
        }
    }
    
    public final void patchU8(int offset, byte value) {
        int old = baos.position();
        baos.position(offset);
        try {
            u8(value);
        } finally {
            baos.position(old);
        }
    }
    
    public final void patchU16(int offset, int value) {
        int old = baos.position();
        baos.position(offset);
        try {
            u16(value);
        } finally {
            baos.position(old);
        }
    }
    
    public final void patchU16(int offset, short value) {
        int old = baos.position();
        baos.position(offset);
        try {
            u16(value);
        } finally {
            baos.position(old);
        }
    }
    
    public final void patchU32(int offset, int value) {
        int old = baos.position();
        baos.position(offset);
        try {
            u32(value);
        } finally {
            baos.position(old);
        }
    }
    
    public final void patchU64(int offset, long value) {
        int old = baos.position();
        baos.position(offset);
        try {
            u64(value);
        } finally {
            baos.position(old);
        }
    }
    
    public final void patchF32(int offset, float value) {
        int old = baos.position();
        baos.position(offset);
        try {
            f32(value);
        } finally {
            baos.position(old);
        }
    }
    
    public final void patchF64(int offset, double value) {
        int old = baos.position();
        baos.position(offset);
        try {
            f64(value);
        } finally {
            baos.position(old);
        }
    }
    
    public final void utf(String s) {
        try {
            dos.writeUTF(s);
        } catch(UTFDataFormatException e) {
            throw new IllegalArgumentException(e);
        } catch(IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public final void write(OutputStream out) throws IOException {
        dos.flush();
        baos.writeTo(out);
    }
    
    public final byte[] write() {
        ByteArrayOutputStream res = new ByteArrayOutputStream();
        try {
            write(res);
        } catch(IOException e) {
            throw new IllegalStateException(e);
        }
        return res.toByteArray();
    }
}
