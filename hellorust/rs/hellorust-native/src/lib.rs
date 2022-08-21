use std::ffi::CString;
use std::os::raw::c_char;

use hellorust_core::{add_num, hello_rust};

#[no_mangle]
pub extern "C" fn hello_native() -> *mut c_char {
    return unsafe {
        CString::from_vec_unchecked(hello_rust().as_bytes().to_vec()).into_raw()
    };
}

#[no_mangle]
pub extern "C" fn add_native(lhs: i32, rhs: i32) -> i32 {
    add_num(lhs, rhs)
}
