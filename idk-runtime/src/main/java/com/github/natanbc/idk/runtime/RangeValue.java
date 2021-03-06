package com.github.natanbc.idk.runtime;

import java.util.List;

public class RangeValue implements Value {
    private final long from;
    private final long to;
    
    private RangeValue(long from, long to) {
        this.from = from;
        this.to = to;
    }
    
    public static RangeValue of(long from, long to) {
        return new RangeValue(from, to);
    }
    
    public long getFrom() {
        return from;
    }
    
    public long getTo() {
        return to;
    }
    
    @Override
    public String type() {
        return "range";
    }
    
    @Override
    public boolean isRange() {
        return true;
    }
    
    @Override
    public RangeValue asRange() {
        return this;
    }
    
    @Override
    public Value get(Value key) {
        if(key.isString()) {
            switch(key.asString().getValue()) {
                case "from": return LongValue.of(from);
                case "to": return LongValue.of(to);
            }
        }
        return NilValue.instance();
    }
    
    @Override
    public Value set(Value key, Value value) {
        throw new ThrownError(StringValue.of("Ranges are immutable"));
    }
    
    @Override
    public long size() {
        return Math.abs(to - from);
    }
    
    @Override
    public ArrayValue keys() {
        return new ArrayValue(List.of(
                StringValue.of("from"),
                StringValue.of("to")
        ));
    }
    
    @Override
    public int hashCode() {
        return Long.hashCode(from) ^ Long.hashCode(to);
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof RangeValue)) {
            return false;
        }
        var o = (RangeValue)obj;
        return o.from == from && o.to == to;
    }
    
    @Override
    public String toString() {
        return "Range(" + from + " to " + to + ")";
    }
}
