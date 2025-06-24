package application;

import controller.GUIGameController;
import model.board.Handicap;
import model.eval.MaterialEvaluator;
import model.player.HumanGUIPlayer;
import model.player.MinimaxAIPlayer;
import model.player.Player;
import model.player.RandomAIPlayer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The API that connects the frontend and the backend.
 */
@RestController
//@CrossOrigin(origins = "http://localhost")
@CrossOrigin(origins = "*")  // TODO: Only allow localhost but allow any port
public class AppController {
    private final Map<String, String> users = new HashMap<>();
    private int guestCounter = 0;
    private final List<GUIGameController> gameControllers = new ArrayList<>();
    private final List<Challenge> challenges = new ArrayList<>();

    // for testing
    @GetMapping("/")
    public ResponseEntity<String> index() {
        System.out.println("Hello world!");
        return new ResponseEntity<>("Hello world!", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        System.out.printf("Login with username=%s and password=%s\n", username, password);
        if (users.containsKey(username) && users.get(username).equals(password)) {
            return new ResponseEntity<>(new LoginResponse(true, ""), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new LoginResponse(false, "Error: Incorrect username or password"), HttpStatus.OK);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        System.out.printf("Register with username=%s and password=%s\n", username, password);
        if (users.containsKey(username)) {
            // user already exists
            return new ResponseEntity<>(new LoginResponse(false, "Error: A player with this username already exists"), HttpStatus.OK);
        } else {
            users.put(username, password);
            return new ResponseEntity<>(new LoginResponse(true, "Success! Please log in with your username and password."), HttpStatus.OK);
        }
    }

    @GetMapping("/playAsGuest")
    public ResponseEntity<LoginResponse> playAsGuest() {
        String username = "_guest" + (guestCounter++);
        System.out.println("Playing as guest: " + username);
        return new ResponseEntity<>(new LoginResponse(true, username), HttpStatus.OK);
    }

    /**
     * Creates a challenge. Each player must only have at most 1 pending challenge at a time.
     * @return if the challenge is successfully created,
     * returns the challengeId for the newly created challenge.
     * Otherwise, return -1.
     */
    @PostMapping("/createChallenge")
    public ResponseEntity<Integer> createChallenge(@RequestBody Challenge challenge) {
        System.out.println("Creating challenge " + challenge);
        for (Challenge c : challenges) {
            if (c.status == Challenge.Status.PENDING && c.username.equals(challenge.username)) {
                System.out.println("Existing pending challenge found for user " + challenge.username);
                return new ResponseEntity<>(-1, HttpStatus.OK);
            }
        }
        int challengeId = challenges.size();
        challenge.challengeId = challengeId;
        challenges.add(challenge);
        System.out.println("Challenge successfully created with challengeId " + challengeId);
        return new ResponseEntity<>(challengeId, HttpStatus.OK);
    }

    /**
     * Accepts the challenge with the given opponent username and gameId.
     * @return If successful, returns the updated challenge.
     * Otherwise, returns null and a BAD_REQUEST status code.
     */
    @PostMapping("/acceptChallenge")
    public ResponseEntity<Challenge> acceptChallenge(@RequestParam int challengeId, @RequestBody Challenge newChallenge) {
        String opUsername = newChallenge.opUsername;
        int gameId = newChallenge.gameId;
        String newSide = newChallenge.side;
        System.out.println("User " + opUsername + " is trying to accept challenge " + challengeId);
        if (challengeId >= challenges.size() || opUsername == null || gameId >= gameControllers.size()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        Challenge challenge = challenges.get(challengeId);
        // use the challenge mutex because another user may cancel it at the same time
        synchronized (challenge) {
            if (challenge.status != Challenge.Status.PENDING) {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            challenge.match(opUsername, gameId, newSide);
            System.out.println("Challenge successfully accepted");
            return new ResponseEntity<>(challenge, HttpStatus.OK);
        }
    }

    /**
     * Cancels a challenge.
     * @return a response entity containing true if challenge is successfully canceled, false otherwise
     */
    @PostMapping("/cancelChallenge")
    public ResponseEntity<Boolean> cancelChallenge(@RequestParam int challengeId) {
        System.out.println("Cancelling challenge " + challengeId);
        if (challengeId >= challenges.size() || challenges.get(challengeId).status != Challenge.Status.PENDING) {
            return new ResponseEntity<>(false, HttpStatus.OK);
        }
        // use the challenge mutex because another user may accept it at the same time
        Challenge challenge = challenges.get(challengeId);
        synchronized (challenge) {
            challenge.cancel();
            System.out.println("Successfully canceled challenge " + challengeId);
            return new ResponseEntity<>(true, HttpStatus.OK);
        }
    }

    /**
     * Resolve a matched challenge.
     * @return a response entity containing true if challenge is successfully resolved, false otherwise
     */
    @PostMapping("/resolveChallenge")
    public ResponseEntity<Boolean> resolveChallenge(@RequestParam int challengeId) {
        System.out.println("Resolving challenge " + challengeId);
        if (challengeId >= challenges.size() || challenges.get(challengeId).status != Challenge.Status.MATCHED) {
            return new ResponseEntity<>(false, HttpStatus.OK);
        }
        challenges.get(challengeId).resolve();
        System.out.println("Successfully resolved challenge " + challengeId);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    /**
     * @return all challenges whose status is pending or matched
     */
    @GetMapping("/getActiveChallenges")
    public ResponseEntity<List<Challenge>> getChallenges() {
        return new ResponseEntity<>(challenges.stream()
                .filter(c -> c.status == Challenge.Status.PENDING || c.status == Challenge.Status.MATCHED)
                .toList(),
                HttpStatus.OK);
    }

    /**
     * Initializes a game with the given information.
     * @return the gameId of the game
     */
    @PostMapping("/initGame")
    public ResponseEntity<Integer> initGame(@RequestBody InitGameRequest request) {
        String whitePlayerType = request.whitePlayerType();
        String whiteName = request.whiteName();
        String blackPlayerType = request.blackPlayerType();
        String blackName = request.blackName();
        String handicapType = request.handicapType();

        System.out.printf("initGame called with white player name: %s, white player type: %s, black player name: %s, black player type: %s, handicap type: %s\n",
                whiteName, whitePlayerType, blackName, blackPlayerType, handicapType);
        Player whitePlayer, blackPlayer;
        whitePlayer = switch (whitePlayerType) {
            case "HumanGUIPlayer" -> new HumanGUIPlayer(true);
            case "RandomAIPlayer" -> new RandomAIPlayer(true);
            case "MinimaxAIPlayer-1" -> new MinimaxAIPlayer(true, new MaterialEvaluator(), 1);
            case "MinimaxAIPlayer-3" -> new MinimaxAIPlayer(true, new MaterialEvaluator(), 3);
            default -> throw new IllegalArgumentException("Unknown white player: " + whitePlayerType);
        };
        blackPlayer = switch (blackPlayerType) {
            case "HumanGUIPlayer" -> new HumanGUIPlayer(false);
            case "RandomAIPlayer" -> new RandomAIPlayer(false);
            case "MinimaxAIPlayer-1" -> new MinimaxAIPlayer(false, new MaterialEvaluator(), 1);
            case "MinimaxAIPlayer-3" -> new MinimaxAIPlayer(false, new MaterialEvaluator(), 3);
            default -> throw new IllegalArgumentException("Unknown black player: " + blackPlayerType);
        };
        Handicap handicap;
        try {
            handicap = Handicap.valueOf(handicapType);
        } catch (IllegalArgumentException e) {
            System.out.println("Warning: Unknown handicap type " + handicapType + ", defaulting to NONE");
            handicap = Handicap.NONE;
        }
        GUIGameController gameController = new GUIGameController(whiteName, whitePlayer, blackName, blackPlayer, handicap);
        if (!(whitePlayer instanceof HumanGUIPlayer)) {
            gameController.playOneMove();
        }
        int gameId = gameControllers.size();
        gameControllers.add(gameController);
        return new ResponseEntity<>(gameId, HttpStatus.OK);
    }

    /**
     * @return the info for the game with the given gameId, or null if gameId is invalid
     */
    @GetMapping("/getGameInfo")
    public ResponseEntity<GameInfo> getGameInfo(@RequestParam int gameId) {
        System.out.println("Getting the info for game " + gameId);
        if (gameId >= gameControllers.size()) {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        GUIGameController gameController = gameControllers.get(gameId);
        System.out.println("Returning the info for game " + gameId);
        return new ResponseEntity<>(new GameInfo(gameController.getFEN(),
                gameController.getWhiteName(),
                gameController.getBlackName()), HttpStatus.OK);
    }

    @GetMapping("/getCandidates")
    public ResponseEntity<CandidateMoves> getCandidates(@RequestParam int gameId, @RequestParam String square) {
        if (gameId >= gameControllers.size()) {
            throw new IllegalArgumentException("Unknown gameId: " + gameId);
        }
        return new ResponseEntity<>(gameControllers.get(gameId).getCandidateMoves(square), HttpStatus.OK);
    }

    /**
     * Tries to play the given move.
     * If the move is illegal, the isLegal field in the response will be false,
     * the board state will be unchanged, and the fen and winner will be for the current board.
     * If the move is legal, the isLegal field in the response will be true,
     * the move will be made, and the fen and winner will be for the updated move.
     * The frontend should call /getOpponentMove periodically to poll the next move of the opponent.
     */
    @PostMapping("/tryMove")
    public ResponseEntity<TryMoveResponse> tryMove(@RequestParam int gameId, @RequestBody UIMove uiMove) {
        if (gameId >= gameControllers.size()) {
            throw new IllegalArgumentException("Unknown gameId: " + gameId);
        }
        System.out.println(uiMove);
        synchronized (gameControllers.get(gameId)) {
            TryMoveResponse response = gameControllers.get(gameId).tryMove(uiMove);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    /**
     * Wait for the opponent to make a move.
     * If the opponent is an AI or a Human CLI player, the game controller will tell the AI to make a move.
     * If the opponent is a Human GUI player, the game controller will do nothing.
     */
    @GetMapping("/waitForOpponent")
    public ResponseEntity<OpponentMoveResponse> waitForOpponent(@RequestParam int gameId) {
        if (gameId >= gameControllers.size()) {
            throw new IllegalArgumentException("Unknown gameId: " + gameId);
        }
        GUIGameController gameController = gameControllers.get(gameId);
        synchronized (gameController) {
            return new ResponseEntity<>(gameController.playOneMove(), HttpStatus.OK);
        }
    }
}
