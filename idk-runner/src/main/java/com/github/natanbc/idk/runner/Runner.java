package com.github.natanbc.idk.runner;

import com.github.natanbc.idk.ast.SimplifierVisitor;
import com.github.natanbc.idk.bytecode.BytecodeReader;
import com.github.natanbc.idk.bytecode.convert.BytecodeConverter;
import com.github.natanbc.idk.interpreter.BytecodeInterpreter;
import com.github.natanbc.idk.interpreter.Interpreter;
import com.github.natanbc.idk.ir.convert.IrConverter;
import com.github.natanbc.idk.parser.IdkParser;
import com.github.natanbc.idk.runtime.BooleanValue;
import com.github.natanbc.idk.runtime.ExecutionContext;
import com.github.natanbc.idk.runtime.Function;
import com.github.natanbc.idk.runtime.NilValue;
import com.github.natanbc.idk.runtime.Value;
import com.github.natanbc.idk.stdlib.Stdlib;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Supplier;

public class Runner {
    private static ExecutionContext context;
    private static boolean bytecode;
    private static boolean debug;
    private static boolean quiet;
    private static boolean simplify;
    private static boolean startup = true;
    
    public static void main(String[] args) {
        clearContext();
        for(var s : args) {
            executeCommand(s, "--", "-");
        }
        startup = false;
        log("Type /h for help");
        var sc = new Scanner(System.in);
        while(true) {
            if(!quiet) System.out.print("> ");
            executeCommand(sc.nextLine().strip(), "/");
        }
    }
    
    private static void executeCommand(String command, String... prefixes) {
        debug("Executing command '%s'", command);
        findCommandName(command, prefixes).ifPresentOrElse(line -> {
            if(line.isEmpty()) return;
            var parts = line.split(":", 2);
            if(parts.length == 0) return;
            switch(parts[0]) {
                case "c", "clear-vars" -> clearContext();
                case "h", "help" -> {
                    System.out.println("""
                        """);
                    if(startup) System.exit(0);
                }
                case "p", "print-vars" ->
                        context.getGlobals().forEach((name, value) -> print("%s = %s", name, value));
                case "q", "quit" -> System.exit(0);
                case "r", "run" -> {
                    if(parts.length == 1) {
                        print("No command specified");
                    } else {
                        var p = Path.of(parts[1]);
                        if(!Files.exists(p)) {
                            fatal("File '%s' does not exist", p);
                        } else {
                            try {
                                executeCode(Files.readString(p).strip());
                            } catch(IOException e) {
                                e.printStackTrace();
                                fatal("Error reading file '%s'", p);
                            }
                        }
                    }
                }
                case "b", "bytecode" -> bytecode = parts.length <= 1 || Boolean.parseBoolean(parts[1]);
                case "d", "debug" -> debug = parts.length <= 1 || Boolean.parseBoolean(parts[1]);
                case "s", "silent" -> quiet = parts.length <= 1 || Boolean.parseBoolean(parts[1]);
                case "simplify" -> simplify = parts.length <= 1 || Boolean.parseBoolean(parts[1]);
            }
        }, () -> executeCode(command));
    }
    
    private static void executeCode(String code) {
        try {
            var ast = new IdkParser(code).parse();
            debug("AST (parsed): %s", ast);
            if(simplify) {
                ast = ast.accept(SimplifierVisitor.instance());
                debug("AST (simplified): %s", ast);
            }
            var ir = ast.accept(IrConverter.instance());
            debug("IR: %s", ir);
            if(bytecode) {
                var bc = ir.accept(BytecodeConverter.instance());
                if(debug) {
                    var br = new BytecodeReader(bc);
                    while(true) {
                        var fr = br.readFunction();
                        if(fr == null) break;
                        debug("  id: %d", fr.id() & 0xFFFF);
                        debug("  name: %s%s", fr.name(), fr.id() == br.entrypoint() ? " (entrypoint)" : "");
                        debug("  arg count: %d", fr.argumentCount());
                        debug("  locals count: %d", fr.localsCount());
                        debug("  varargs: %s", fr.varargs());
                        debug("  annotations: %s", fr.annotations());
                        var codeStart = fr.reader().pos();
                        while(true) {
                            var pos = fr.reader().pos() - codeStart;
                            var op = fr.nextInstruction();
                            if(op == null) break;
                            debug("    %s: %s", pos, op);
                        }
                    }
                }
                execute(() -> new BytecodeInterpreter(bc, context.getGlobals()).run());
            } else {
                execute(() -> ir.accept(new Interpreter(context)));
            }
        } catch(Exception e) {
            print("Error: %s", e);
        }
    }
    
    private static void execute(Supplier<Value> fn) {
        try {
            var v = fn.get();
            log("Result: %s", v);
        } catch(Exception e) {
            print("Error running: %s", e);
        }
    }
    
    private static Optional<String> findCommandName(String command, String... prefixes) {
        for(var s : prefixes) {
            if(command.startsWith(s)) {
                return Optional.of(command.substring(s.length()).strip());
            }
        }
        return Optional.empty();
    }
    
    private static void log(String fmt, Object... args) {
        if(!quiet) {
            print(fmt, args);
        }
    }
    
    private static void debug(String fmt, Object... args) {
        if(debug) {
            print(fmt, args);
        }
    }
    
    private static void fatal(String fmt, Object... args) {
        print(fmt, args);
        if(startup) {
            System.exit(1);
        }
    }
    
    private static void print(String fmt, Object... args) {
        System.out.format(fmt, args);
        System.out.println();
    }
    
    private static void clearContext() {
        context = ExecutionContext.newEmptyContext();
        Stdlib.install(context);
        context.setGlobal("print", new Function() {
            @Override
            public Value call(Value[] args) {
                for(var s : args) {
                    System.out.print(s.tostring().getValue());
                }
                System.out.println();
                return NilValue.instance();
            }
        });
    }
}
