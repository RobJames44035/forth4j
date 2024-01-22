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
        String[] parts = line.split("\\s+")
        for (String part : parts) {
            switch (part) {
                case "+":
                    // Check that there are at least two elements on the stack
                    if (this.stack.size() < 2) {
                        System.out.println("Error: Not enough elements on the stack for operation '+'")
                        return // Early return
                    }
                    // Perform the addition
                    int sum = this.stack.pop() + this.stack.pop()
                    this.stack.push(sum)
                    break
                default:
                    // Assume the part is an integer
                    try {
                        int value = Integer.parseInt(part)
                        this.stack.push(value)
                    } catch (NumberFormatException ignored) {
                        System.out.println("Unrecognized token: " + part)
                        return // Early return
                    }
                    break
            }
        }

        // Print the stack status
        System.out.println("Stack: " + this.stack)
    }
}
