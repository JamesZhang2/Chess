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

import java.util.HashMap;
import java.util.Map;

/**
 * The API that connects the frontend and the backend.
 */
@RestController
//@CrossOrigin(origins = "http://localhost")
@CrossOrigin(origins = "*")  // TODO: Only allow localhost but allow any port
public class AppController {
    private Map<String, String> users = new HashMap<>();
    private int guestCounter = 0;
    private GUIGameController gameController;
    // TODO: Map from gameId to GUIGameController

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
    public ResponseEntity<String> initGame(@RequestBody InitGameRequest request) {
        String whitePlayerType = request.whitePlayerType;
        String blackPlayerType = request.blackPlayerType;
        String handicapType = request.handicapType;

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
        // for testing promotions
//        String testFEN = "q4k2/1P6/8/5K2/8/8/2p5/8 w - - 0 1";
//        try {
//            gameController = new GUIGameController(whitePlayer, blackPlayer, testFEN);
//        } catch (MalformedFENException | IllegalBoardException e) {
//            throw new RuntimeException(e);
//        }
        Handicap handicap;
        try {
            handicap = Handicap.valueOf(handicapType);
        } catch (IllegalArgumentException e) {
            System.out.println("Warning: Unknown handicap type " + handicapType + ", defaulting to NONE");
            handicap = Handicap.NONE;
        }
        gameController = new GUIGameController(whitePlayer, blackPlayer, handicap);
        if (!(whitePlayer instanceof HumanGUIPlayer)) {
            gameController.playOneMove();
        }
        return new ResponseEntity<>(gameController.getFEN(), HttpStatus.OK);
    }

    @GetMapping("/getCandidates")
    public ResponseEntity<CandidateMoves> getCandidates(@RequestParam String square) {
        return new ResponseEntity<>(gameController.getCandidateMoves(square), HttpStatus.OK);
    }

    @PostMapping("/tryMove")
    public ResponseEntity<UIMoveResponse> tryMove(@RequestBody UIMove uiMove) {
        System.out.println(uiMove);
        UIMoveResponse response = gameController.tryMove(uiMove);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
