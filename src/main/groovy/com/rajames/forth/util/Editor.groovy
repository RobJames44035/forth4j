/*
 * Copyright 2024 Robert A. James
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rajames.forth.util

import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.TextCharacter
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.graphics.TextGraphics
import com.googlecode.lanterna.gui2.Panel
import com.googlecode.lanterna.gui2.WindowBasedTextGUI
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType
import com.googlecode.lanterna.screen.Screen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import com.rajames.forth.memory.storage.Block
import com.rajames.forth.memory.storage.BlockService

class Editor {

    private Screen screen
    private WindowBasedTextGUI textGUI
    private Panel mainPanel
    private Block block
    private DefaultTerminalFactory terminalFactory
    private BlockService blockService

    private int column
    private int row

    int startRow = 4
    int startCol = 5
    int width = 64

    Editor(Block block, BlockService blockService) {
        this.block = block
        this.terminalFactory = new DefaultTerminalFactory()
        this.blockService = blockService
    }

    void editor() {
        try {
            // : populate 1024 0 do 42 i ! loop ;
            screen = terminalFactory.createScreen()
            screen.startScreen()
            initScreen()
            screen.refresh()
            while (true) {
                KeyStroke ks = screen.readInput()
                switch (ks.getKeyType()) {
                    case KeyType.ArrowUp:
                        // Move cursor Up
                        if (row > startRow) {
                            row--
                        }
                        break
                    case KeyType.ArrowDown:
                        // Move cursor Down
                        if (row < 19) {
                            row++
                        }
                        break
                    case KeyType.ArrowLeft:
                        // Move cursor Left
                        if (column > startCol) {
                            column--
                        }
                        break
                    case KeyType.ArrowRight:
                        // Move cursor Right
                        if (column < 68) {
                            column++
                        }
                        break
                    case KeyType.Backspace:
                        // Handle Backspace
                        if (column > startCol) {
                            column--
                            screen.setCharacter(column, row, new TextCharacter(' ' as char, TextColor.ANSI.YELLOW, TextColor.ANSI.BLACK))
                        }
                        break
                    case KeyType.Enter:
                        // Carriage Return / Enter
                        // Move to the next line if it's not the last one
                        if (row < 19) row++

                        // Reset column to the start
                        column = startCol

                        // Iterate over the line to find the end of the content
                        for (; column <= 68; column++) {
                            // Get the character at the new cursor position
                            TextCharacter textCharacter = screen.getFrontCharacter(column, row)

                            // If the character is a space or null, it's the end of the content
                            if (textCharacter.getCharacterString() == ' ' || textCharacter.getCharacterString() == '\u0000') {
                                break;
                            }
                        }
                        break;
                    default:
                        // Handle Character keystrokes (Typing)
                        if (ks.getKeyType() == KeyType.Character) {
                            // Obtain the input character
                            Character inputChar = ks.getCharacter()
                            // Update screen contents
                            screen.setCharacter(column, row, TextCharacter.fromCharacter(inputChar, TextColor.ANSI.YELLOW, TextColor.ANSI.BLACK) as TextCharacter)
                            // Advance cursor position to right after typing
                            if (column < 68) {
                                column++
                            }
                        }
                        // Handle Ctrl + X (Exiting the editor)
                        if (ks.isCtrlDown() && ks.getCharacter() == 'x') {
                            throw new EditorException("Exit editor command received");
                        }                        // Handle Ctrl + W (ctrlWAction)
                        if (ks.isCtrlDown() && ks.getCharacter() == 'w') {
                            byte[] bytes = new byte[1024]
                            int index = 0
                            for (int currentRow = startRow; currentRow < startRow + 16; currentRow++) {
                                for (int currentColumn = startCol; currentColumn < startCol + 64; currentColumn++) {
                                    TextCharacter textCharacter = screen.getFrontCharacter(currentColumn, currentRow)
                                    char chr = textCharacter.getCharacterString() as char
                                    bytes[index] = chr as byte
                                    index++
                                }
                            }
                            block.bytes = bytes
                            blockService.save(this.block)
                            break
                        }
                        screen.setCursorPosition(new TerminalPosition(column, row))
                        screen.refresh()
                }
                screen.setCursorPosition(new TerminalPosition(column, row))
                // Update cursor position after every keystroke
                screen.refresh()
            }
        }
        catch (IOException e) {
            e.printStackTrace()
        }
        finally {
            if (screen != null) {
                try {
                    screen.refresh()
                    screen.stopScreen()
                    print("\033[H\033[2J")
                }
                catch (IOException e) {
                    e.printStackTrace()
                }
            }
        }
    }

    private void initScreen() {
        head()
        foot()
        left()
        fill()
        populate()
        screen.setCursorPosition(new TerminalPosition(startCol, startRow))
        column = startCol
        row = startRow
    }

    private void populate() {
        for (int i = 0, row = startRow, col = startCol; i < this.block.bytes.length && row < 21; i++) {
            char ch = (char) this.block.bytes[i]
            try {
                screen.setCharacter(col, row, TextCharacter.fromCharacter(ch, TextColor.ANSI.YELLOW, TextColor.ANSI.BLACK) as TextCharacter)
            } catch (IllegalArgumentException ignored) {
                screen.setCharacter(col, row, TextCharacter.fromCharacter("." as char, TextColor.ANSI.YELLOW, TextColor.ANSI.BLACK) as TextCharacter)
            }
            col++
            if (col >= startCol + width) {
                col = startCol
                row++
            }
        }
    }

    private void head() {
        String left = "forth4j 1.0.0"
        String center = "Block: ${this.block.blockNumber}"
        String right = "Editor v1.0 "

        int totalSpace = 80 - (left.length() + center.length() + right.length())
        int leftSpace = (int) (totalSpace / 2)
        int rightSpace = totalSpace - leftSpace

        String header = left + " ".repeat(leftSpace) + center + " ".repeat(rightSpace) + right

        TextGraphics textGraphics = screen.newTextGraphics()
        textGraphics.setForegroundColor(TextColor.ANSI.BLACK)
        textGraphics.setBackgroundColor(TextColor.ANSI.YELLOW)
        textGraphics.putString(0, 0, header)
    }

    private void foot() {
        String footer = " ^X Exit      ^W Write Block                                                    "
        TextGraphics textGraphics = screen.newTextGraphics()
        textGraphics.setForegroundColor(TextColor.ANSI.BLACK)
        textGraphics.setBackgroundColor(TextColor.ANSI.YELLOW)
        textGraphics.putString(0, 23, footer)
    }

    private left() {
        for (int i = 4; i < 20; i++) {
            TextGraphics textGraphics = screen.newTextGraphics()
            textGraphics.setForegroundColor(TextColor.ANSI.BLACK)
            textGraphics.setBackgroundColor(TextColor.ANSI.YELLOW)
            String rowNumber = String.format(" %02d: ", i - 3)
            textGraphics.putString(0, i, rowNumber)
        }
    }

    private fill() {
        String fill = "                                                                                "
        String fill2 = "           "
        TextGraphics textGraphics = screen.newTextGraphics()
        textGraphics.setForegroundColor(TextColor.ANSI.BLACK)
        textGraphics.setBackgroundColor(TextColor.ANSI.YELLOW)
        textGraphics.putString(0, 1, fill)
        textGraphics.putString(0, 2, fill)
        textGraphics.putString(0, 3, fill)

        textGraphics.putString(0, 20, fill)
        textGraphics.putString(0, 21, fill)
        textGraphics.putString(0, 22, fill)
        for (int i = 1; i < 23; i++) {
            textGraphics.putString(69, i, fill2)
        }
    }
}