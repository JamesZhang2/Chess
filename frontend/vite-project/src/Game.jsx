import "./Game.css";
import { useState, useEffect } from 'react';
import Board from "./Board.jsx";
import Home from "./Home.jsx";
import axios from "axios";
import MalformedFENError from "./MalformedFENError.js";
import { getRC, sleep } from "./Util.js";

function Game({ gameId, username }) {
    const startPos = [
        "RNBQKBNR".split(""),
        "PPPPPPPP".split(""),
        "........".split(""),
        "........".split(""),
        "........".split(""),
        "........".split(""),
        "pppppppp".split(""),
        "rnbqkbnr".split(""),
    ];
    const [whiteName, setWhiteName] = useState(null);
    const [blackName, setBlackName] = useState(null);
    const [selfIsWhite, setSelfIsWhite] = useState(false);
    // true if we're playing as white, false otherwise.
    // Affects whether we're seeing the board from white or black's perspective.
    // since we need to wait for opponent's move at the very beginning if we're black,
    // setSelfIsWhite has not changed the state yet. Therefore, we initialize it as false by default.

    const [pieces, setPieces] = useState(startPos);
    const [selectedSquare, setSelectedSquare] = useState(null);
    // u: unknown (game in progress), w: white, d: draw, b: black
    const [winner, setWinner] = useState("u");
    const [legalDests, setLegalDests] = useState(new Set());
    const [legalPromotions, setLegalPromotions] = useState(new Set());
    const [showPromotionOverlay, setShowPromotionOverlay] = useState(false);
    const [promotionSquare, setPromotionSquare] = useState(null);
    const [opResigned, setOpResigned] = useState(false);
    const [goHome, setGoHome] = useState(false);

    // useEffect with empty dependency array to run only once
    useEffect(() => {
        console.log("gameId in Game: " + gameId);
        axios.get(`/api/getGameInfo?gameId=${gameId}`)
            .then((response) => {
                if (response.data === null) {
                    throw new Error("Failed to get the FEN for game with gameId " + gameId);
                }
                console.log(response.data);
                setWhiteName(response.data.whiteName);
                setBlackName(response.data.blackName);
                setSelfIsWhite(response.data.whiteName === username);
                setBoardState(response.data.fen);
                if (response.data.whiteName !== username) {
                    // we are black, wait for opponent's response
                    waitForOpponent();
                }
            })
            .catch((error) => {
                console.log(error);
            });
    }, []);

    let board;
    if (winner === "u") {
        // game is ongoing
        board = <Board
            pieces={pieces}
            white={selfIsWhite}
            selectedSquare={selectedSquare}
            legalDests={legalDests}
            handleSquareClick={handleSquareClick}
            showPromotionOverlay={showPromotionOverlay}
            handlePromotionSelection={handlePromotionSelection}
            handlePromotionCancellation={handlePromotionCancellation} />;
    } else {
        // game has ended
        board = <Board
            pieces={pieces}
            white={selfIsWhite}
            selectedSquare={null}
            legalDests={new Set()}
            handleSquareClick={(sqName) => { }}
            showPromotionOverlay={false}
            handlePromotionSelection={(pieceType) => { }}
            handlePromotionCancellation={(event) => { }} />;
    }
    let message = "";
    if (opResigned) {
        message = "Opponent resigned. ";
    }
    if (winner === "u") {
        message += "Let's play!";
    } else if (winner === "w") {
        message += "White won!";
    } else if (winner === "d") {
        message += "Draw!";
    } else {
        message += "Black won!";
    }

    if (goHome) {
        return <Home username={username} />;
    }

    // TODO: Offer Draw & Resign buttons
    return <div className="game-container">
        <header>
            <h1>{message}</h1>
            <button id="back-home-btn" onClick={() => setGoHome(true)}>Back to Home Page</button>
        </header>
        <div className="player-info-banner">{selfIsWhite ? blackName : whiteName}</div>
        {board}
        <div className="player-info-banner">{selfIsWhite ? whiteName : blackName}</div>
    </div>

    /**
     * Set the board state based on the given FEN string.
     * @param {string} fen 
     */
    function setBoardState(fen) {
        console.log("calling setBoardState with fen " + fen);
        setPieces(parsePiecePlacement(fen));
    }

    function handleSquareClick(sqName) {
        console.log("Clicked " + sqName);
        if (!selectedSquare) {
            // startSquare is null
            if ((pieceColorOnSquare(sqName) === "white" && selfIsWhite)
                || (pieceColorOnSquare(sqName) === "black" && !selfIsWhite)) {
                setSelectedSquare(sqName);
                setLegalMoves(sqName);
            }
        } else {
            // startSquare is not null
            if ((pieceColorOnSquare(sqName) === "white" && selfIsWhite)
                || (pieceColorOnSquare(sqName) === "black" && !selfIsWhite)) {
                // selecting a new friendly piece
                setSelectedSquare(sqName);
                setLegalMoves(sqName);
            } else {
                // attempt to make a move
                if (legalPromotions.has(sqName)) {
                    // promotion overlay
                    setShowPromotionOverlay(true);
                    setPromotionSquare(sqName);
                } else {
                    tryMove(sqName);
                }
            }
        }
    }

    /**
     * For the promotion overlay, the piece will have sqName set to "Q", "R", etc.
     * @param {string} pieceType 
     */
    function handlePromotionSelection(pieceType) {
        console.log("Promotion piece: " + pieceType);
        setShowPromotionOverlay(false);
        tryMove(promotionSquare, pieceType);
    }

    /**
     * handles promotion cancellation (clicking anywhere outside the 4 choices)
     * @param {PointerEvent} event
     */
    function handlePromotionCancellation(event) {
        event.stopPropagation();
        setShowPromotionOverlay(false);
        setSelectedSquare(null);
        setPromotionSquare(null);
        setLegalDests(new Set());
        setLegalPromotions(new Set());
    }

    function setLegalMoves(sqName) {
        axios.get(`/api/getCandidates?gameId=${gameId}&square=${sqName}`)
            .then((response) => {
                const res = response.data;
                console.log("legalDests: " + res.legalDests);
                console.log("legalPromotions: " + res.legalPromotions);
                setLegalDests(new Set(res.legalDests));
                setLegalPromotions(new Set(res.legalPromotions));
            })
            .catch((error) => {
                console.log(error);
            });
    }

    /**
     * Try to play a move. If it's legal, wait for the opponent's response.
     * @param {string} destSq destination square
     * @param {string} promotion single letter representing promotion piece, or null if not a promotion
     */
    function tryMove(destSq, promotion = null) {
        axios.post(`/api/tryMove?gameId=${gameId}`, {
            fromSquare: selectedSquare,
            toSquare: destSq,
            promotion: promotion
        })
            .then((response) => {
                const res = response.data;
                const isLegal = res.isLegal;
                const fen = res.fen;
                const winner = res.winner;
                console.log("response from tryMove:");
                console.log(res);
                if (isLegal) {
                    console.log("Legal move");
                    setBoardState(fen);
                    setWinner(winner);
                    if (winner === "u") {
                        waitForOpponent();
                    }
                } else {
                    console.log("Illegal move");
                }
            })
            .catch((error) => {
                console.log(error);
            });
        setSelectedSquare(null);
        setLegalDests(new Set());
        setLegalPromotions(new Set());
    }

    /**
     * Wait for the opponent's next move.
     * Keeps polling until opponent has made a move.
     */
    async function waitForOpponent() {
        let fen, newWinner, isResign;
        do {
            console.log("Waiting for opponent to move");
            await axios.get(`/api/waitForOpponent?gameId=${gameId}`)
                .then((response) => {
                    const res = response.data;
                    console.log(res);
                    fen = res.fen;
                    newWinner = res.winner;
                    isResign = res.isResign;
                })
                .catch((error) => {
                    console.log(error);
                });
            await sleep(200);
        } while (parseIsWhiteToMove(fen) !== selfIsWhite && newWinner === "u");
        setBoardState(fen);
        setWinner(newWinner);
        setOpResigned(isResign);
    }

    /**
     * @param {string} sqName name of square, like a1 or e4
     * @returns the piece of 
     */
    function pieceColorOnSquare(sqName) {
        const [r, c] = getRC(sqName);
        if ("KQRBNP".includes(pieces[r][c])) {
            return "white";
        } else if ("kqrbnp".includes(pieces[r][c])) {
            return "black";
        } else {
            return "empty";
        }
    }
}

/**
 * Get the placement of pieces from the given FEN string.
 * @param {string} fen 
 * @returns a 2D array of characters representing the placement of pieces
 */
function parsePiecePlacement(fen) {
    // console.log("Calling parsePiecePlacement with " + fen);
    const fields = fen.split(" ");
    const placement = fields[0].split("/");
    // console.log("placement: " + placement);
    if (placement.length != 8) {
        throw new MalformedFENError("Number of rows in piece placement field is not 8");
    }
    const pieces = new Array(8);
    for (let row = 0; row < 8; row++) {
        pieces[row] = new Array(8);
    }
    for (let row = 0; row < 8; row++) {
        // Since FEN goes from the top of the board to the bottom,
        // row i for pieces corresponds to index (7 - i) of the placement string
        const rowStr = placement[7 - row];
        // console.log("rowStr: " + rowStr);
        let col = 0;
        for (let i = 0; i < rowStr.length; i++) {
            if (col >= 8) {
                throw new MalformedFENError("Number of pieces and blanks in rank "
                    + (8 - row) + " is greater than 8");
            }
            const curChar = rowStr.charAt(i);
            if ("12345678".includes(curChar)) {
                // curChar represents a series of blanks
                const blanks = parseInt(curChar, 10);
                if (col + blanks > 8) {
                    throw new MalformedFENError("Number of pieces and blanks in rank "
                        + (8 - row) + " is greater than 8");
                }
                for (let j = 0; j < blanks; j++) {
                    pieces[row][col++] = ".";
                }
            } else {
                // curChar may represent a piece
                if ("KQRBNPkqrbnp".includes(curChar)) {
                    pieces[row][col++] = curChar;
                } else {
                    throw new MalformedFENError("Unknown piece name: " + curChar);
                }
            }
        }
        if (col != 8) {
            throw new MalformedFENError("Number of pieces and blanks in rank "
                + (8 - row) + " is less than 8");
        }
    }
    // console.log("pieces: " + pieces.toString());
    return pieces;
}

function parseIsWhiteToMove(fen) {
    if (fen.split(" ")[1] === "w") {
        return true;
    } else if (fen.split(" ")[1] === "b") {
        return false;
    } else {
        throw new MalformedFENError("Malformed active color field");
    }
}

export default Game;