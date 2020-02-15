# Ricochet Robots 🤖

A Python implementation of Alex Randolph's game
[*Ricochet Robots*](https://recommend.games/#/game/51) including solver.

## Compile

Compile the Java code with the command:

```bash
mkdir --parents bin
javac -classpath java/lib/commons-cli-1.3.1.jar \
    -d bin \
    java/src/info/riemannhypothesis/ricochetrobots/*.java
```

## Run

To run, type

```bash
./solve.sh -b <board> -g
```

This will place four robots in random positions on the board, pick one of the
targets and one of the robots to reach it, try to find a solution (within the
move and time contraints specified), and then give a graphical representation
of the moves.

E.g., if you want to solve the board 3 in the repository, simply run

```bash
./solve.sh -b boards/3.txt -g
```

For a full description of all available command line options, simply type

```bash
./solve.sh
```
