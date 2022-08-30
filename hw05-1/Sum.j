.class public Sum

.super java/lang/Object
.method public <init>()V
aload_0
invokenonvirtual java/lang/Object/<init>()V
return
.end method

;sum function
.method public static sum(I)I
.limit stack 32
.limit locals 8
ldc 1
istore 1
iload 1


;write your code
;iload0 = param I(100), iload1 = 1(cnt), iload2 = result
ldc 0
istore 2

LOOP:
iload 1
iload 0
if_icmpgt END

iload 1
iload 2
iadd
istore 2

ldc 1
iload 1
iadd
istore 1


goto LOOP
END:
iload 2
ireturn
.end method

.method public static main([Ljava/lang/String;)V
.limit stack 32
.limit locals 8
ldc 100
istore 0
getstatic java/lang/System/out Ljava/io/PrintStream;
iload 0
invokestatic Sum/sum(I)I
invokevirtual java/io/PrintStream/println(I)V
return
.end method