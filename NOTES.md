# Notes

## Handling Literal Numbers in Forth-like language

In our discussed approach to handle literal numbers in the Forth-like language, we concluded some steps:

- Modifying the `Word` entity to include an optional `Integer stackValue` to represent literal numbers as special `Word`
  s. It provides an advantage of fitting well within the existing infrastructure.

- Creating a new `LITERAL` word with a generated name whenever a number is encountered during the compilation process.
  It 'compiles' the number into the sequence of words that make up our newly defined word.

- During the runtime, when this generated `LITERAL` word is encountered, its behavior is to push the literal number (
  its `stackValue`) onto the data stack.

- Ensuring that the `LITERAL` word cannot be invoked directly by the user as per Forth's compile-only behavior.

## Support for Double-precision Numbers in the Forth-like Language

Incorporating double-precision number support in our implementation would most likely involve the following changes:

- Enhance the stack: We'd need to update our data stack to be able to handle pairs of stack entries as a single
  double-precision number when appropriate.

- Update existing words: Some existing words would need to be updated or overloaded to handle double-precision numbers,
  such as words for arithmetic operations, comparison, and others.

- Introduce new double-precision-specific words: We need to incorporate the various double-precision words provided by
  Forth, like 2DROP, 2DUP, 2OVER, 2SWAP, D+ (double addition), D- (double subtraction), and others.

- Enhance the parser and compiler: Our parser and compiler need to be enhanced to discern and correctly handle
  double-precision literal numbers.
