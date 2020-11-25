package com.github.natanbc.idk.stdlib;

import com.github.natanbc.idk.runtime.*;

import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.LongStream;
import java.util.stream.StreamSupport;

public class Stdlib {
    public static void install(ExecutionContext context) {
        context.setGlobal("math", MathLib.load());
        
        context.setGlobal("type", new Function("type") {
            @Override
            public Value call(Value[] args) {
                return StringValue.of(Stdlib.get(args, 0).type());
            }
        });
        
        context.setGlobal("keys", new Function("keys") {
            @Override
            public Value call(Value[] args) {
                return Stdlib.get(args, 0).keys();
            }
        });
    
        context.setGlobal("size", new Function("size") {
            @Override
            public Value call(Value[] args) {
                return LongValue.of(Stdlib.get(args, 0).size());
            }
        });
        
        context.setGlobal("pcall", new Function("pcall") {
            @Override
            public Value call(Value[] args) {
                var fn = Stdlib.get(args, 0).asFunction();
                var fArgs = new Value[args.length - 1];
                System.arraycopy(args, 1, fArgs, 0, fArgs.length);
                try {
                    return new ArrayValue(List.of(
                            BooleanValue.of(true),
                            fn.call(fArgs)
                    ));
                } catch(ExecutionError e) {
                    Value errorMessage;
                    if(e instanceof ThrownError) {
                        errorMessage = ((ThrownError) e).getValue();
                    } else {
                        var msg = e.getClass().getSimpleName();
                        if(e.getMessage() != null) {
                            msg += ": " + e.getMessage();
                        }
                        errorMessage = StringValue.of(msg);
                    }
                    return new ArrayValue(List.of(
                            BooleanValue.of(false),
                            errorMessage
                    ));
                }
            }
        });
        
        context.setGlobal("iter", new Function() {
            @Override
            public Value call(Value[] args) {
                var target = Stdlib.get(args, 0);
                if(target.isArray()) {
                    var arr = target.asArray().copyOnWrite();
                    return new IteratorValue(
                            StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                                    arr.iterator(),
                                    Spliterator.ORDERED | Spliterator.NONNULL | Spliterator.IMMUTABLE
                            ), false)
                    );
                } else if(target.isObject()) {
                    var obj = target.asObject().copyOnWrite();
                    var type = args.length > 1 ?
                                       args[1].asString().getValue()
                                       : "entries";
                    return new IteratorValue(switch(type) {
                        case "keys" -> obj.getMap().keySet().stream();
                        case "values" -> obj.getMap().values().stream();
                        case "entries" -> obj.getMap().entrySet().stream()
                                                  .map(e -> new ArrayValue(new Value[] { e.getKey(), e.getValue() }));
                        default -> throw new ThrownError(StringValue.of("Invalid iteration type '" + type + "'"));
                    });
                } else if(target.isRange()) {
                    var range = target.asRange();
                    return new IteratorValue(
                            LongStream.rangeClosed(range.getFrom(), range.getTo())
                                .mapToObj(LongValue::of)
                    );
                }
                throw new ThrownError(StringValue.of("Unable to iterate " + target.type()));
            }
        });
    }
    
    static Value get(Value[] args, int index) {
        if(args.length <= index) {
            throw new ThrownError(StringValue.of("Missing argument " + index));
        }
        return args[index];
    }
}
