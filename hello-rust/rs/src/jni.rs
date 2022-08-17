use jni::JNIEnv;
use jni::objects::{JClass};
use jni::sys::jstring;
use crate::hello_rust;

#[no_mangle]
pub extern "C" fn Java_com_seiko_compose_hellorust_HelloRust_hello(
    env: JNIEnv, _: JClass,
) -> jstring {
    let str = hello_rust();
    env.new_string(str)
        .expect("Unable to new rust string")
        .into_inner()
}
