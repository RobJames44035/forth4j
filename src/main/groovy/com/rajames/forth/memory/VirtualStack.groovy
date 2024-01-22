package com.rajames.forth.memory

abstract class VirtualStack implements StackInterface {

    protected Stack<Integer> stack = new Stack<>()

    @Override
    void push(int value) {
        stack.push(value)
    }

    @Override
    int pop() {
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

