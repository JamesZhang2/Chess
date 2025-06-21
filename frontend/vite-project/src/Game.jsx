import "./Game.css";
import { useState, useEffect } from 'react';
import Board from "./Board.jsx";
import axios from "axios";
import MalformedFENError from "./MalformedFENError.js";
import { getRC } from "./Util.js";

function Game({ whiteName, whitePlayerType, blackName, blackPlayerType }) {
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
    const [pieces, setPieces] = useState(startPos);
    const [whiteToMove, setWhiteToMove] = useState(true);
    const [selectedSquare, setSelectedSquare] = useState(null);
    // u: unknown (game in progress), w: white, d: draw, b: black
    const [winner, setWinner] = useState("u");
    const [legalDests, setLegalDests] = useState(new Set());
    const [legalPromotions, setLegalPromotions] = useState(new Set());
    const [showPromotionOverlay, setShowPromotionOverlay] = useState(false);
    const [promotionSquare, setPromotionSquare] = useState(null);

    // useEffect with empty dependency array to run only once
    useEffect(() => {
        axios.get(`/api/initGame?whitePlayerType=${whitePlayerType}&blackPlayerType=${blackPlayerType}`)
            .then((response) => {
                const fen = response.data;
                console.log("fen: " + fen);
                setBoardState(fen);
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
            white={whiteToMove}
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
            white={whiteToMove}
            selectedSquare={null}
            legalDests={new Set()}
            handleSquareClick={(sqName) => { }}
            showPromotionOverlay={false}
            handlePromotionSelection={(pieceType) => { }}
            handlePromotionCancellation={(event) => { }} />;
    }
    let message;
    if (winner === "u") {
        message = "Let's play!";
    } else if (winner === "w") {
        message = "White won!";
    } else if (winner === "d") {
        message = "Draw!";
    } else {
        message = "Black won!";
    }
    // TODO: Offer Draw & Resign buttons
    return <div className="game-container">
        <h1>{message}</h1>
        <div className="player-info-banner">{whiteName}</div>
        {board}
        <div className="player-info-banner">{blackName}</div>
    </div>

    /**
     * Set the board state based on the given FEN string.
     * @param {string} fen 
     */
    function setBoardState(fen) {
        setPieces(parsePiecePlacement(fen));
        const sideToMove = fen.split(" ")[1];
        if (sideToMove === "w") {
            setWhiteToMove(true);
        } else if (sideToMove === "b") {
            setWhiteToMove(false);
        } else {
            throw new MalformedFENError("Malformed active color field");
        }
    }

    function handleSquareClick(sqName) {
        console.log("Clicked " + sqName);
        if (!selectedSquare) {
            // startSquare is null
            if ((pieceColorOnSquare(sqName) === "white" && whiteToMove)
                || (pieceColorOnSquare(sqName) === "black" && !whiteToMove)) {
                setSelectedSquare(sqName);
                setLegalMoves(sqName);
            }
        } else {
            // startSquare is not null
            if ((pieceColorOnSquare(sqName) === "white" && whiteToMove)
                || (pieceColorOnSquare(sqName) === "black" && !whiteToMove)) {
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
        axios.get(`/api/getCandidates?square=${sqName}`)
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

    function tryMove(sqName, promotion = null) {
        axios.post('/api/tryMove', {
            fromSquare: selectedSquare,
            toSquare: sqName,
            promotion: promotion
        })
            .then((response) => {
                const res = response.data;
                const isLegal = res.isLegal;
                const fen = res.fen;
                const winner = res.winner;
                console.log(isLegal);
                console.log(fen);
                console.log("Winner: " + winner);
                if (isLegal) {
                    console.log("Legal move");
                    setBoardState(fen);
                    setWinner(winner);
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

export default Game;