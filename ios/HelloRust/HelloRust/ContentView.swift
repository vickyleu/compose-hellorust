//
//  ContentView.swift
//  HelloRust
//
//  Created by Seiko on 2022/8/24.
//

import SwiftUI

struct ContentView: View {
    var body: some View {
        Text(RustTest().sayHello())
            .padding()
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
