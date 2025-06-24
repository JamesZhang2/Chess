import { useEffect, useState, useRef } from "react";
import "./Home.css";
import Login from "./Login.jsx";
import Game from "./Game.jsx";
import { sleep } from "./Util.js";
import axios from "axios";

/**
 * Renders the home page.
 * 
 * Challenge system documentation:
 * The list of active (PENDING or MATCHED) challenges are polled every second.
 * Each player can have at most one outgoing challenge.
 * If they have an outgoing challenge, challengeIdRef is set to the challengeId of that challenge
 * otherwise, it's set to null.
 * 
 * When a player sends a challenge, createChallenge is called.
 * A new challenge is created with the PENDING status.
 * They can then cancel it with the cancel challenge button.
 * Clicking it calls cancelChallenge and sets the challenge to the CANCELED status.
 * 
 * When a player accepts the challenge from another player, acceptChallenge is called.
 * A new game is created, the status of the challenge is set to MATCHED,
 * and the player accepting the challenge enters the newly-initialized game.
 * When the other player polls the list of active challenges and finds that their challenge is MATCHED,
 * they tell the backend to change the status of the challenge to RESOLVED
 * and then enter the game with the same gameId.
 */
function Home({ username }) {
    const [loggedOut, setLoggedOut] = useState(false);
    const [vsHuman, setVsHuman] = useState(true);  // toggles vs human or vs AI
    const [aiType, setAIType] = useState("RandomAIPlayer");
    const [side, setSide] = useState("random");
    const [gameParams, setGameParams] = useState(null);  // starts the game if gameParams !== null
    const [challenges, setChallenges] = useState([]);  // list of active (pending or matched) challenges
    const challengeIdRef = useRef(null);  // challengeId of my outgoing challenge, or null if there is none

    const handicapRef = useRef(null);

    useEffect(() => {
        if (loggedOut || gameParams !== null) {
            return;
        }
        // poll challenges every second
        const intervalId = setInterval(pollChallenges, 1000);
        return () => clearInterval(intervalId);
    }, [loggedOut, gameParams]);

    // These names must match the Handicap enum type on the backend,
    // except that SELF and OP will be replaced with WHITE and BLACK
    const handicapDescriptions = [
        { name: "NONE", description: "None" },
        { name: "SELF_QUEEN", description: "I play without the queen" },
        { name: "SELF_A_ROOK", description: "I play without the a-rook" },
        { name: "SELF_H_ROOK", description: "I play without the h-rook" },
        { name: "SELF_B_KNIGHT", description: "I play without the b-knight" },
        { name: "SELF_G_KNIGHT", description: "I play without the g-knight" },
        { name: "SELF_C_BISHOP", description: "I play without the c-bishop" },
        { name: "SELF_F_BISHOP", description: "I play without the f-bishop" },
        { name: "SELF_BOTH_ROOKS", description: "I play without both rooks" },
        { name: "SELF_QUEEN_BOTH_ROOKS", description: "I play without both rooks and the queen" },
        { name: "OP_QUEEN", description: "Opponent plays without the queen" },
        { name: "OP_A_ROOK", description: "Opponent plays without the a-rook" },
        { name: "OP_H_ROOK", description: "Opponent plays without the h-rook" },
        { name: "OP_B_KNIGHT", description: "Opponent plays without the b-knight" },
        { name: "OP_G_KNIGHT", description: "Opponent plays without the g-knight" },
        { name: "OP_C_BISHOP", description: "Opponent plays without the c-bishop" },
        { name: "OP_F_BISHOP", description: "Opponent plays without the f-bishop" },
        { name: "OP_BOTH_ROOKS", description: "Opponent plays without both rooks" },
        { name: "OP_QUEEN_BOTH_ROOKS", description: "Opponent plays without both rooks and the queen" }
    ];

    if (loggedOut) {
        return <Login />;
    }
    if (gameParams !== null) {
        return <Game {...gameParams} />
    }

    async function pollChallenges() {
        await axios.get("/api/getActiveChallenges")
            .then((response) => {
                console.log(response.data);
                // console.log("My challengeId: " + challengeIdRef.current);
                setChallenges(response.data);
                for (let c of response.data) {
                    if (c.challengeId === challengeIdRef.current && c.status === "MATCHED") {
                        // my challenge has a match
                        // console.log("Setting challengeId to null in pollChallenges()");
                        challengeIdRef.current = null;
                        axios.post(`/api/resolveChallenge?challengeId=${c.challengeId}}`)
                            .then((response) => {
                                const success = response.data;
                                if (!success) {
                                    throw new Error("Backend didn't accept the acknowledgement");
                                }
                            })
                            .catch((error) => {
                                console.log(error);
                            });
                        enterGame(c.gameId);
                    }
                }
            })
            .catch((error) => {
                console.log(error);
            });
    }

    return (
        <div className="home-body">
            <header>
                <h1>Hello, {username}!</h1>
                <button id="log-out-btn" onClick={() => setLoggedOut(true)}>Log out</button>
            </header>
            <div className="home-container">
                <div className="toggle" id="human-ai-toggle">
                    <button id="vs-human-btn" className={vsHuman ? "pressed" : ""} onClick={() => setVsHuman(true)}>Play vs. Human</button>
                    <button id="vs-ai-btn" className={vsHuman ? "" : "pressed"} onClick={() => setVsHuman(false)}>Play vs. AI</button>
                </div>

                <div className="toggle" id="side-toggle">
                    <p>I play as:</p>
                    <button id="play-white-btn" className={side === "white" ? "pressed" : ""} onClick={() => setSide("white")}>White</button>
                    <button id="play-random-btn" className={side === "random" ? "pressed" : ""} onClick={() => setSide("random")}>Random</button>
                    <button id="play-black-btn" className={side === "black" ? "pressed" : ""} onClick={() => setSide("black")}>Black</button>
                </div>

                <div className="handicap-container">
                    <label htmlFor="handicap-dropdown">Handicap: </label>
                    <select name="handicap-dropdown" id="handicap-dropdown" ref={handicapRef} defaultValue="NONE">
                        {renderHandicapOptions()}
                    </select>
                </div>

                <div id="ai-menu" className={vsHuman ? "hide" : "show"}>
                    <div className="toggle" id="ai-type-toggle">
                        <p>AI type:</p>
                        <button id="random-ai-btn" className={aiType === "RandomAIPlayer" ? "pressed" : ""} onClick={() => setAIType("RandomAIPlayer")}>Random</button>
                        <button id="minimax-1-ai-btn" className={aiType === "MinimaxAIPlayer-1" ? "pressed" : ""} onClick={() => setAIType("MinimaxAIPlayer-1")}>Minimax Depth 1</button>
                        <button id="minimax-3-ai-btn" className={aiType === "MinimaxAIPlayer-3" ? "pressed" : ""} onClick={() => setAIType("MinimaxAIPlayer-3")}>Minimax Depth 3</button>
                    </div>
                </div>

                <button id="play-btn" onClick={handlePlayBtnClick}>{vsHuman ? "Create Challenge" : "Play"}</button>

                <div className="current-challenges">
                    Current challenges:
                    <table className="challenges-table">
                        <tbody>
                            <tr>
                                <th>Username</th>
                                <th>They play as</th>
                                <th>Handicap</th>
                                <th>Action</th>
                            </tr>
                            {renderChallenges()}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );

    function renderHandicapOptions() {
        return handicapDescriptions.map(h => <option value={h.name}>{h.description}</option>);
    }

    function renderChallenges() {
        let tableRows = [];
        for (let c of challenges) {
            // pair of (handicap name, handicap description)
            const handicapPair = (handicapDescriptions.filter(h => h.name === c.relHandicapType))[0];
            if (c.status === "PENDING") {
                if (c.challengeId === challengeIdRef.current) {
                    // your own challenge
                    tableRows.push(<tr>
                        <td>{c.username}</td>
                        <td>{c.side}</td>
                        <td>{handicapPair.description}</td>
                        <td><button className="cancel-challenge-btn" onClick={() => cancelChallenge(c.challengeId)}>Cancel</button></td>
                    </tr>)
                } else {
                    // other users' challenges

                    tableRows.push(<tr>
                        <td>{c.username}</td>
                        <td>{c.side}</td>
                        <td>{handicapPair.description}</td>
                        <td><button className="accept-challenge-btn" onClick={() => acceptChallenge(c.challengeId, c.username, c.side, handicapPair.name)}>Accept</button></td>
                    </tr>)
                }
            }
        }
        return tableRows;
    }

    /**
     * Cancel the challenge if it's sent by the current logged-in user
     * @param {int} cId challengeId 
     */
    function cancelChallenge(cId) {
        axios.post(`/api/cancelChallenge?challengeId=${cId}`)
            .then((response) => {
                const success = response.data;
                if (!success) {
                    throw new Error("Backend didn't accept the cancellation");
                }
            })
            .catch((error) => {
                console.log(error);
            });
        // console.log("Setting challengeId to null in cancelChallenge()");
        challengeIdRef.current = null;
    }

    /**
     * Initialize a new game, accept the challenge, and enter the initialized game.
     * @param {int} cId challengeId 
     * @param {string} challengerName the name of the user who created the challenge
     * @param {string} challengerSide the side of the user who created the challenge
     * @param {string} relHandicapType the relative handicap type from the point of view of the challenger
     */
    async function acceptChallenge(cId, challengerName, challengerSide, relHandicapType) {
        console.log(`cId: ${cId}, challengerName: ${challengerName}, challengerSide: ${challengerSide}, relHandicapType: ${relHandicapType}`);
        let newChallengerSide = challengerSide;
        // Coordinate the color of the two players so that they don't get the same color
        if (challengerSide === "random") {
            newChallengerSide = Math.random() > 0.5 ? "white" : "black";
        }
        let mySide = newChallengerSide == "white" ? "black" : "white";

        // the relHandicapType of updatedChallenge is from the opponent's point of view
        // so we need to flip it for our relative handicap type
        let myRelHandicapType;
        if (relHandicapType.startsWith("SELF")) {
            myRelHandicapType = relHandicapType.replace("SELF", "OP");
        } else if (relHandicapType.startsWith("OP")) {
            myRelHandicapType = relHandicapType.replace("OP", "SELF");
        } else {
            myRelHandicapType = "NONE";
        }

        const gameId = await initGame(challengerName,
            "HumanGUIPlayer",
            mySide,
            myRelHandicapType
        );

        console.log(`cId: ${cId}, opUsername: ${username}, gameId: ${gameId}, side: ${newChallengerSide}`);
        await axios.post(`/api/acceptChallenge?challengeId=${cId}`, {
            "opUsername": username,
            "gameId": gameId,
            "side": newChallengerSide
        })
            .then(async (response) => {
                console.log(response.data);
            })
            .catch((error) => {
                console.log(error);
            });

        enterGame(gameId);
    }

    async function handlePlayBtnClick(e) {
        console.log("vsHuman: " + vsHuman);
        if (vsHuman) {
            axios.post("/api/createChallenge",
                {
                    "username": username,
                    "side": side,
                    "relHandicapType": handicapRef.current.value
                }
            )
                .then((response) => {
                    const id = response.data;
                    if (id === -1) {
                        alert("You can only have one outgoing challenge at a time. Please cancel the current challenge if you'd like to create a new one.");
                    } else {
                        console.log("Challenge created with challengeId " + id);
                        challengeIdRef.current = id;
                    }
                })
                .catch((error) => {
                    console.log(error);
                });
        } else {
            const gameId = await initGame(aiType, aiType, side, handicapRef.current.value);
            enterGame(gameId);
        }
    }

    /**
     * Initialize a new game by calling the initGame endpoint.
     * Does not enter the game by setting gameParams.
     * @param {string} opUsername username of opponent
     * @param {string} opType type of opponent
     * @param {string} mySide side of self
     * @param {string} relHandicapType relative handicap type (with SELF or OP)
     * @returns {int} the gameId of the initialized game
     */
    async function initGame(opUsername, opType, mySide, relHandicapType) {
        let gameId, whiteName, whitePlayerType, blackName, blackPlayerType, handicapType;

        if (mySide === "white" || (mySide === "random" && Math.random() > 0.5)) {
            whiteName = username;
            whitePlayerType = "HumanGUIPlayer";
            blackName = opUsername;
            blackPlayerType = opType;
            handicapType = relHandicapType.replace("SELF", "WHITE").replace("OP", "BLACK");
        } else {
            whiteName = opUsername;
            whitePlayerType = opType;
            blackName = username;
            blackPlayerType = "HumanGUIPlayer";
            handicapType = relHandicapType.replace("SELF", "BLACK").replace("OP", "WHITE");
        }
        await axios.post("/api/initGame",
            {
                "whiteName": whiteName,
                "whitePlayerType": whitePlayerType,
                "blackName": blackName,
                "blackPlayerType": blackPlayerType,
                "handicapType": handicapType
            }
        )
            .then((response) => {
                gameId = response.data;
                // console.log("gameId after initGame: " + gameId);
            })
            .catch((error) => {
                console.log(error);
            });
        return gameId;
    }

    /**
     * Enters the game with the given gameId.
     * Requires: the game with the given gameId has already been initialized.
     */
    function enterGame(gameId) {
        setGameParams({
            "gameId": gameId,
            "username": username
        });
    }
}

export default Home;