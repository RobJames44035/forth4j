//file:noinspection GroovyAssignabilityCheck
package com.rajames.forth

class ForthRepl {

    private Stack<Integer> stack
    private Scanner scanner

    ForthRepl() {
        this.stack = new Stack<>()
        this.scanner = new Scanner(System.in)
    }

    void run() {
        while (true) {
            System.out.print("forth> ")
            String line = this.scanner.nextLine().trim()

            if (line == "exit") {
                System.out.println("Goodbye!")
                break
            }

            parseAndExecute(line)
        }
        this.scanner.close()
    }

    void parseAndExecute(String line) {
        line.split("\\s+").each { part ->
            switch (part) {
                case ".":
                    if (stack.isEmpty()) {
                        println "Error: Stack Underflow."
                        return
                    }
                    println stack.pop()
                    break

                case "+":
                    if (stack.size() < 2) {
                        println "Error: Not enough elements on the stack for operation '+'"
                        return
                    }
                    stack.push(stack.pop() + stack.pop())
                    break

                case "DUP":
                    if (stack.isEmpty()) {
                        println "Error: Stack Underflow."
                        return
                    }
                    stack.push(stack.peek())
                    break

                default:
                    try {
                        stack.push(Integer.parseInt(part))
                    } catch (NumberFormatException nfe) {
                        println "Unrecognized token: $part"
                    }
            }
        }
        println "Stack: $stack"
    }
}
