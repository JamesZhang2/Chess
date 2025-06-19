import "./Game.css";
import { useState, useEffect } from 'react';
import Board from "./Board.jsx";
import axios from "axios";
import MalformedFENError from "./MalformedFENError.js";

function Game() {
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
    // useEffect with empty dependency array to run only once
    useEffect(() => {
        axios.get('http://localhost:8080/initGame',)
            .then((response) => {
                const fen = response.data;
                setBoardState(fen, setPieces, setWhiteToMove);
            })
            .catch((error) => {
                console.log(error);
            })
    }, []);
    return <>
        <h1>Game!</h1>
        <Board pieces={pieces} />
    </>;
}

/**
 * Set the board state based on the given FEN string.
 * @param {string} fen 
 */
function setBoardState(fen, setPieces, setWhiteToMove) {
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

/**
 * Get the placement of pieces from the given FEN string.
 * @param {string} fen 
 * @returns a 2D array of characters representing the placement of pieces
 */
function parsePiecePlacement(fen) {
    console.log("Calling parsePiecePlacement with " + fen);
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
        console.log("rowStr: " + rowStr);
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