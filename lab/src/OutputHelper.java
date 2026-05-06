import org.antlr.v4.runtime.BaseErrorListener;

import java.util.ArrayList;
import java.util.List;

public class OutputHelper extends BaseErrorListener {
    private static final List<String> errors = new ArrayList<>();

    public static void printSemanticError(ErrorType errorType, int line, String text) {
        if(errorType.equals(ErrorType.UNDEF_VAR)){
            errors.add("Error type 1 at Line " + line + ": Undefined variable: " + text+".");
        }else if(errorType.equals(ErrorType.UNDEF_FUNC)){
            errors.add("Error type 2 at Line " + line + ": Undefined function: " + text+".");
        }else if(errorType.equals(ErrorType.REDEF_VAR)){
            errors.add("Error type 3 at Line " + line + ": Redefined variable: " + text+".");
        }else if(errorType.equals(ErrorType.REDEF_FUNC)){
            errors.add("Error type 4 at Line " + line + ": Redefined function: " + text+".");
        }else if(errorType.equals(ErrorType.INIT_NOT_MATCH)){
            errors.add("Error type 5 at Line " + line + ": type.Type mismatched for assignment.");
        }else if(errorType.equals(ErrorType.EXP_NOT_MATCH)){
            errors.add("Error type 6 at Line " + line + ": type.Type mismatched for operands.");
        }else if(errorType.equals(ErrorType.RET_NOT_MATCH)){
            errors.add("Error type 7 at Line " + line + ": type.Type mismatched for return.");
        }else if(errorType.equals(ErrorType.FUNPARAM)){
            errors.add("Error type 8 at Line " + line + ": Function is not applicable for arguments.");
        }else if(errorType.equals(ErrorType.XB_NOT_MATCH)){
            errors.add("Error type 9 at Line " + line + ": Not an array: "+ text+".");
        }else if(errorType.equals(ErrorType.VAR_FUN)){
            errors.add("Error type 10 at Line " + line + ": Not a function: "+ text+".");
        }else if(errorType.equals(ErrorType.FUN_VAR)){
            errors.add("Error type 11 at Line " + line + ": The left-hand side of an assignment must be a variable.");
        }
    }

    public static boolean hasErrors() {
        return !errors.isEmpty();
    }
    public static void print() {
        int i=0;
        for (String error : errors) {
            i++;
            if (i < errors.size()) {
                System.err.println(error);
            }else {
                System.err.print(error);
            }
        }
    }
}
