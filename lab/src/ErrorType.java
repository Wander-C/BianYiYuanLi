public enum ErrorType {
    UNDEF_VAR,
    UNDEF_FUNC,
    REDEF_VAR,
    REDEF_FUNC,
    INIT_NOT_MATCH,//5.赋值号两侧
    EXP_NOT_MATCH,//6.运算符
    RET_NOT_MATCH,//7.返回值
    FUNPARAM,//8.函数参数
    XB_NOT_MATCH,//9.对非数组使用下标运算符
    VAR_FUN,//10.对变量使用函数调用
    FUN_VAR//11.对函数进行赋值


}
