# 定义变量
ANTLR_JAR = ./lib/antlr-4.9.1-complete.jar
JAVAC = javac
JAVA = java
MAIN_CLASS = Main
SRC = $(wildcard *.java)
CLASS_FILES = $(SRC:.java=.class)
ANTLR_GEN_FILES = $(wildcard Calculator*.java) $(wildcard Calculator*.tokens) $(wildcard Calculator*.interp)

# 默认目标：生成 ANTLR 代码并编译所有 .java 文件
all: generate compile

# 生成 ANTLR 代码
generate:
	java -jar $(ANTLR_JAR) -visitor Calculator.g4

# 编译所有 .java 文件
compile: $(CLASS_FILES)

# 编译规则：将 .java 文件编译为 .class 文件
%.class: %.java
	$(JAVAC) $<

# 运行程序
run: all
	$(JAVA) $(MAIN_CLASS)

# 清理生成的文件
clean:
	rm -f *.class $(ANTLR_GEN_FILES)

# 伪目标声明
.PHONY: all generate compile run clean