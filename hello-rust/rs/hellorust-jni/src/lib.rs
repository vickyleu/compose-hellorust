use hellorust_core::{add_num, hello_rust};
use jni::JNIEnv;
use jni::objects::{JClass};
use jni::sys::{jint, jstring};

#[no_mangle]
pub extern "C" fn Java_com_seiko_compose_hellorust_HelloRust_hello(
    env: JNIEnv, _: JClass,
) -> jstring {
    let str = hello_rust();
    env.new_string(str)
        .expect("Unable to new rust string")
        .into_inner()
}

#[no_mangle]
pub extern "C" fn Java_com_seiko_compose_hellorust_HelloRust_add(
    _: JNIEnv, _: JClass, lhs: jint, rhs: jint
) -> jint {
    add_num(lhs, rhs)
}
