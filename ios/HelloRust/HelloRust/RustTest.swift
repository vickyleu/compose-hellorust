//
//  RustTest.swift
//  HelloRust
//
//  Created by Seiko on 2022/8/24.
//

import Foundation

class RustTest {
    func sayHello() -> String {
        let cString = hello_native()
        return String(cString: cString!) + ", add=" + String(add_native(100, 50))
    }
}
