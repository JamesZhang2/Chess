# TODO

- UI/UX
  - [ ] Home page
  - [ ] Implement resigning/offer draw in frontend
  - [ ] Update the board after user input while waiting for AI's response so the game doesn't appear frozen
  - [ ] Lock the board orientation so it doesn't flip back and forth when two players are playing on the same webpage (or when playing against AI)
  - [ ] Timer
- Minimax
  - [ ] Alpha-beta pruning
  - [ ] Cache searched positions and their evaluations
  - [ ] Better evaluation function: Piece location, pawn structure, mobility, center control, etc.
  - [ ] Better search function: Look for more forcing moves, promotions, etc.
  - [ ] Simulations to tune hyperparameters
- Opening book
  - [ ] Use Lichess API to build opening book database
- Endgame tablebase
  - [ ] Create endgame tablebase using dynamic programming
    - [ ] implement unmove method
