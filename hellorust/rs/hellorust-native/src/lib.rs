use std::ffi::CString;
use std::os::raw::c_char;

use hellorust_core::{add_num, hello_rust};

#[no_mangle]
pub extern "C" fn hello_native() -> *const c_char {
    CString::new(hello_rust()).unwrap().into_raw()
}

#[no_mangle]
pub extern "C" fn add_native(lhs: i32, rhs: i32) -> i32 {
    add_num(lhs, rhs)
}
