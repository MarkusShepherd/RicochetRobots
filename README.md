RicochetRobots
==============

A simple implementation of Alex Randolph's board game Ricochet Robots including solver

Run
---

To run, type

```bash
./solve.sh board [maxMoves [maxTimeInSec]]
```

This will place four robots in random positions on the board, pick on of the targets and on of the robots to reach it, try to find a solution (within the move and time contraints specified), and the give a graphical representation of the moves.

E.g., if you want to solve the board 3 in the repository, simply run

```bash
./solve.sh boards/3.txt
```
