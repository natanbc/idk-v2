package com.github.natanbc.idk.stdlib;

import com.github.natanbc.idk.runtime.ArrayValue;
import com.github.natanbc.idk.runtime.BooleanValue;
import com.github.natanbc.idk.runtime.Function;
import com.github.natanbc.idk.runtime.LongValue;
import com.github.natanbc.idk.runtime.NilValue;
import com.github.natanbc.idk.runtime.StringValue;
import com.github.natanbc.idk.runtime.ThrownError;
import com.github.natanbc.idk.runtime.TypeError;
import com.github.natanbc.idk.runtime.Value;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IteratorValue implements Value {
    private static final Map<Value, BiFunction<IteratorValue, Value[], Value>> OPS = Map.ofEntries(
            Map.entry(StringValue.of("count"), (self, args) -> LongValue.of(self.consume().count())),
            Map.entry(StringValue.of("filter"), (self, args) -> {
                var fn = predicate(Stdlib.get(args, 0).asFunction());
                return new IteratorValue(
                        self.consume().filter(fn)
                );
            }),
            Map.entry(StringValue.of("map"), (self, args) -> {
                var fn = function1(Stdlib.get(args, 0).asFunction());
                return new IteratorValue(
                        self.consume().map(fn)
                );
            }),
            Map.entry(StringValue.of("flatMap"), (self, args) -> {
                var fn = function1(Stdlib.get(args, 0).asFunction());
                return new IteratorValue(
                        self.consume().flatMap(v -> {
                            var ret = fn.apply(v);
                            if(ret instanceof IteratorValue) {
                                return ((IteratorValue) ret).consume();
                            }
                            throw new TypeError("not an iterator");
                        })
                );
            }),
            Map.entry(StringValue.of("distinct"), (self, args) -> new IteratorValue(self.consume().distinct())),
            Map.entry(StringValue.of("sorted"), (self, args) -> {
                var fn = args.length > 0 ? function2(args[0].asFunction()) : null;
                return new IteratorValue(
                        self.consume().sorted((va, vb) -> {
                            if(fn == null) {
                                if(va.smaller(vb).asBoolean().getValue()) {
                                    return -1;
                                }
                                if(va.eq(vb).asBoolean().getValue()) {
                                    return 0;
                                }
                                return 1;
                            }
                            return Long.signum(fn.apply(va, vb).asLong().getValue());
                        })
                );
            }),
            Map.entry(StringValue.of("peek"), (self, args) -> {
                var fn = consumer(Stdlib.get(args, 0).asFunction());
                return new IteratorValue(
                        self.consume().peek(fn)
                );
            }),
            Map.entry(StringValue.of("limit"), (self, args) -> {
                var val = Stdlib.get(args, 0).asLong().getValue();
                return new IteratorValue(
                        self.consume().limit(val)
                );
            }),
            Map.entry(StringValue.of("skip"), (self, args) -> {
                var val = Stdlib.get(args, 0).asLong().getValue();
                return new IteratorValue(
                        self.consume().skip(val)
                );
            }),
            Map.entry(StringValue.of("takeWhile"), (self, args) -> {
                var fn = predicate(Stdlib.get(args, 0).asFunction());
                return new IteratorValue(
                        self.consume().takeWhile(fn)
                );
            }),
            Map.entry(StringValue.of("dropWhile"), (self, args) -> {
                var fn = predicate(Stdlib.get(args, 0).asFunction());
                return new IteratorValue(
                        self.consume().dropWhile(fn)
                );
            }),
            Map.entry(StringValue.of("forEach"), (self, args) -> {
                var fn = consumer(Stdlib.get(args, 0).asFunction());
                self.consume().forEach(fn);
                return NilValue.instance();
            }),
            Map.entry(StringValue.of("forEachOrdered"), (self, args) -> {
                var fn = consumer(Stdlib.get(args, 0).asFunction());
                self.consume().forEachOrdered(fn);
                return NilValue.instance();
            }),
            Map.entry(StringValue.of("toArray"),
                    (self, args) -> new ArrayValue(self.consume().toArray(Value[]::new))),
            Map.entry(StringValue.of("anyMatch"), (self, args) -> {
                var fn = predicate(Stdlib.get(args, 0).asFunction());
                return BooleanValue.of(self.consume().anyMatch(fn));
            }),
            Map.entry(StringValue.of("allMatch"), (self, args) -> {
                var fn = predicate(Stdlib.get(args, 0).asFunction());
                return BooleanValue.of(self.consume().allMatch(fn));
            }),
            Map.entry(StringValue.of("noneMatch"), (self, args) -> {
                var fn = predicate(Stdlib.get(args, 0).asFunction());
                return BooleanValue.of(self.consume().noneMatch(fn));
            }),
            Map.entry(StringValue.of("findFirst"), (self, args) -> self.consume().findFirst().orElse(NilValue.instance())),
            Map.entry(StringValue.of("findAny"), (self, args) -> self.consume().findAny().orElse(NilValue.instance())),
            Map.entry(StringValue.of("join"), (self, args) -> {
                var delim = args.length > 0 ? args[0].asString().getValue() : "";
                var prefix = "";
                var suffix = "";
                if(args.length > 1) {
                    prefix = Stdlib.get(args, 1).asString().getValue();
                    suffix = Stdlib.get(args, 2).asString().getValue();
                }
                return StringValue.of(self.consume()
                                              .map(v -> v.tostring().getValue())
                                              .collect(Collectors.joining(delim, prefix, suffix))
                );
            })
    );
    
    private Stream<Value> stream;
    
    public IteratorValue(Stream<Value> stream) {
        this.stream = stream;
    }
    
    public Stream<Value> consume() {
        var v = stream;
        if(v == null) {
            throw new ThrownError(StringValue.of("This iterator has already been consumed"));
        }
        stream = null;
        return v;
    }
    
    @Override
    public String type() {
        return "iterator";
    }
    
    @Override
    public Value get(Value key) {
        var op = OPS.get(key);
        if(op == null) return NilValue.instance();
        return new Function() {
            @Override
            public Value call(Value[] args) {
                return op.apply(IteratorValue.this, args);
            }
        };
    }
    
    @Override
    public ArrayValue keys() {
        return new ArrayValue(OPS.keySet());
    }
    
    private static Predicate<Value> predicate(Function fn) {
        var v = new Value[1];
        return arg -> {
            v[0] = arg;
            return fn.call(v).asBoolean().getValue();
        };
    }
    
    private static Consumer<Value> consumer(Function fn) {
        var v = new Value[1];
        return arg -> {
            v[0] = arg;
            fn.call(v);
        };
    }
    
    private static UnaryOperator<Value> function1(Function fn) {
        var v = new Value[1];
        return arg -> {
            v[0] = arg;
            return fn.call(v);
        };
    }
    
    private static BiFunction<Value, Value, Value> function2(Function fn) {
        var v = new Value[2];
        return (arg1, arg2) -> {
            v[0] = arg1;
            v[1] = arg2;
            return fn.call(v);
        };
    }
}
