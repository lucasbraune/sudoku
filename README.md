# sudoku
A program that solves Sudoku puzzles

## Building and running

Requires Java and Maven. To build this project, create a local copy of this repository and exectute `mvn package` from its root. To run it, execute
```
java -jar target/sudoku-1.0-SNAPSHOT.jar
```
This will read sudoku puzzles from the standard input and write their solutions to the standard output.

The 50 puzzles from Project Euler's <a href="https://projecteuler.net/problem=96">Problem 96</a> are contained in the file `src/main/resources/puzzles`. To solve them, run 
```
java -jar target/sudoku-1.0-SNAPSHOT.jar < src/main/resources/puzzles
```

## Format for input and output

A sudoku grid is represented by nine consecutive lines, each of which is made of 9 digits. The digit 0 corresponds to an empty cell. This program will ignore any lines from its input that are not part of the representation of a grid.
