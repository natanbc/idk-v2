package com.github.natanbc.idk.stdlib;

import com.github.natanbc.idk.runtime.*;

import java.util.List;

public class Stdlib {
    public static void install(ExecutionContext context) {
        context.setGlobal("math", MathLib.load());
        
        context.setGlobal("type", new Function("type") {
            @Override
            public Value call(ExecutionContext context, Value[] args) {
                return StringValue.of(Stdlib.get(args, 0).type());
            }
        });
        
        context.setGlobal("keys", new Function("keys") {
            @Override
            public Value call(ExecutionContext context, Value[] args) {
                return Stdlib.get(args, 0).keys();
            }
        });
    
        context.setGlobal("size", new Function("size") {
            @Override
            public Value call(ExecutionContext context, Value[] args) {
                return LongValue.of(Stdlib.get(args, 0).size());
            }
        });
        
        context.setGlobal("pcall", new Function("pcall") {
            @Override
            public Value call(ExecutionContext context, Value[] args) {
                var fn = Stdlib.get(args, 0).asFunction();
                var fArgs = new Value[args.length - 1];
                System.arraycopy(args, 1, fArgs, 0, fArgs.length);
                try {
                    return new ArrayValue(List.of(
                            BooleanValue.of(true),
                            fn.call(context, fArgs)
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
    }
    
    static Value get(Value[] args, int index) {
        if(args.length <= index) {
            throw new ThrownError(StringValue.of("Missing argument " + index));
        }
        return args[index];
    }
}
