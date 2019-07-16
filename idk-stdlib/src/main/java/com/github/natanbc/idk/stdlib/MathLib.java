package com.github.natanbc.idk.stdlib;

import com.github.natanbc.idk.runtime.*;

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleSupplier;
import java.util.function.DoubleUnaryOperator;

public class MathLib {
    private static final ObjectValue LIB = new ObjectValue();
    
    static {
        constant("e", Math.E);
        constant("pi", Math.PI);
        constant("maxDouble", Double.MAX_VALUE);
        constant("maxExponent", Double.MAX_EXPONENT);
        constant("maxLong", Long.MAX_VALUE);
        constant("minDouble", Double.MIN_VALUE);
        constant("minExponent", Double.MIN_EXPONENT);
        constant("minLong", Long.MIN_VALUE);
        constant("NaN", Double.NaN);
        constant("negativeInfinity", Double.NEGATIVE_INFINITY);
        constant("positiveInfinity", Double.POSITIVE_INFINITY);
        function("acos", Math::acos);
        function("asin", Math::asin);
        function("atan", Math::atan);
        function("atan2", Math::atan2);
        function("cbrt", Math::cbrt);
        function("ceil", Math::ceil);
        function("copySign", Math::copySign);
        function("cos", Math::cos);
        function("cosh", Math::cosh);
        function("deg", Math::toDegrees);
        function("exp", Math::exp);
        function("expm1", Math::expm1);
        function("floor", Math::floor);
        function("fma", Math::fma);
        function("hypot", Math::hypot);
        function("IEEEremainder", Math::IEEEremainder);
        function("log", Math::log);
        function("log10", Math::log10);
        function("log1p", Math::log1p);
        function("max", Math::max);
        function("min", Math::min);
        function("nextAfter", Math::nextAfter);
        function("nextDown", Math::nextDown);
        function("nextUp", Math::nextUp);
        function("pow", Math::pow);
        function("rad", Math::toRadians);
        function("random", Math::random);
        function("rint", Math::rint);
        function("round", Math::round);
        function("signum", Math::signum);
        function("sin", Math::sin);
        function("sinh", Math::sinh);
        function("sqrt", Math::sqrt);
        function("tan", Math::tan);
        function("tanh", Math::tanh);
        function("ulp", Math::ulp);
        
        LIB.set(StringValue.of("abs"), new Function("abs") {
            @Override
            public Value call(ExecutionContext context, Value[] args) {
                var v = Stdlib.get(args, 0);
                if(v.isDouble()) {
                    return DoubleValue.of(Math.abs(v.asDouble().getValue()));
                } else if(v.isLong()) {
                    return LongValue.of(Math.abs(v.asLong().getValue()));
                } else {
                    throw new TypeError("Expected argument to be long or double");
                }
            }
        });
        
        LIB.set(StringValue.of("parseLong"), new Function("parseLong") {
            @Override
            public Value call(ExecutionContext context, Value[] args) {
                var s = Stdlib.get(args, 0).asString().getValue();
                var base = 10;
                if(args.length > 1) {
                    var arg2 = args[1].asLong().getValue();
                    if(arg2 < Character.MIN_RADIX || arg2 > Character.MAX_RADIX) {
                        throw new RangeError("Base outside of range [" + Character.MIN_RADIX + ", " + Character.MAX_RADIX + "]");
                    }
                    base = (int)arg2;
                }
                try {
                    return LongValue.of(Long.parseLong(s, base));
                } catch(NumberFormatException e) {
                    return NilValue.instance();
                }
            }
        });
        
        LIB.set(StringValue.of("parseDouble"), new Function("parseDouble") {
            @Override
            public Value call(ExecutionContext context, Value[] args) {
                var s = Stdlib.get(args, 0).asString().getValue();
                try {
                    return DoubleValue.of(Double.parseDouble(s));
                } catch(NumberFormatException e) {
                    return NilValue.instance();
                }
            }
        });
    }
    
    public static ObjectValue load() {
        return new ObjectValue(LIB.getMap());
    }
    
    private static void constant(String name, double value) {
        LIB.set(StringValue.of(name), DoubleValue.of(value));
    }
    
    private static void constant(String name, long value) {
        LIB.set(StringValue.of(name), LongValue.of(value));
    }
    
    private static void function(String name, DoubleSupplier operator) {
        LIB.set(StringValue.of(name), new Function(name) {
            @Override
            public Value call(ExecutionContext context, Value[] args) {
                return DoubleValue.of(operator.getAsDouble());
            }
        });
    }
    
    private static void function(String name, DoubleUnaryOperator operator) {
        LIB.set(StringValue.of(name), new Function(name) {
            @Override
            public Value call(ExecutionContext context, Value[] args) {
                var v = Stdlib.get(args, 0);
                return DoubleValue.of(operator.applyAsDouble(toDouble(v)));
            }
        });
    }
    
    private static void function(String name, DoubleBinaryOperator operator) {
        LIB.set(StringValue.of(name), new Function(name) {
            @Override
            public Value call(ExecutionContext context, Value[] args) {
                var v = Stdlib.get(args, 0);
                var v2 = Stdlib.get(args, 1);
                return DoubleValue.of(operator.applyAsDouble(toDouble(v), toDouble(v2)));
            }
        });
    }
    
    private static void function(String name, DoubleTernaryOperator operator) {
        LIB.set(StringValue.of(name), new Function(name) {
            @Override
            public Value call(ExecutionContext context, Value[] args) {
                var v = Stdlib.get(args, 0);
                var v2 = Stdlib.get(args, 1);
                var v3 = Stdlib.get(args, 2);
                return DoubleValue.of(operator.applyAsDouble(toDouble(v), toDouble(v2), toDouble(v3)));
            }
        });
    }
    
    private interface DoubleTernaryOperator {
        double applyAsDouble(double a, double b, double c);
    }
    
    private static double toDouble(Value v) {
        if(v.isDouble()) {
            return v.asDouble().getValue();
        } else if(v.isLong()) {
            return v.asLong().getValue();
        } else {
            throw new TypeError("Expected argument to be long or double");
        }
    }
}
