package com.github.natanbc.idk.bytecode.util;

import java.io.ByteArrayOutputStream;

class OverwritableByteArrayOutputStream extends ByteArrayOutputStream {
    int position() {
        return count;
    }
    
    void position(int i) {
        count = i;
    }
}
