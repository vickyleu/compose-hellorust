#include <stdarg.h>
#include <stdbool.h>
#include <stdint.h>
#include <stdlib.h>

jstring Java_com_seiko_compose_hellorust_HelloRust_hello(JNIEnv env, JClass);

jint Java_com_seiko_compose_hellorust_HelloRust_add(JNIEnv, JClass, jint lhs, jint rhs);
