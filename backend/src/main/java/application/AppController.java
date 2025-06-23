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
        return new ResponseEntity<>(new LoginResponse(true, username), HttpStatus.OK);
    }

    @PostMapping("/initGame")
    public ResponseEntity<InitGameResponse> initGame(@RequestBody InitGameRequest request) {
        String whitePlayerType = request.whitePlayerType();
        String blackPlayerType = request.blackPlayerType();
        String handicapType = request.handicapType();

        System.out.printf("initGame called with white player: %s, black player: %s, handicap type: %s\n", whitePlayerType, blackPlayerType, handicapType);
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
        GUIGameController gameController = new GUIGameController(whitePlayer, blackPlayer, handicap);
        if (!(whitePlayer instanceof HumanGUIPlayer)) {
            gameController.playOneMove();
        }
        int gameId = gameControllers.size();
        gameControllers.add(gameController);
        return new ResponseEntity<>(new InitGameResponse(gameController.getFEN(), gameId), HttpStatus.OK);
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
