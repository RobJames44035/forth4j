package com.rajames.forth.memory

abstract class AbstractStack implements StackInterface {

    protected Stack<Object> stack = new Stack<>()

    @Override
    void push(Object value) {
        stack.push(value)
    }

    @Override
    Object pop() {
        if (stack.empty()) {
            println "Warning: Underflow"
            return 0  // Or however you want to handle underflows
        } else {
            return stack.pop()
        }
    }

    @Override
    void clear() {
        stack.clear()
    }

    @Override
    int size() {
        return stack.size()
    }

    // Additional stack operations as you need...
}

