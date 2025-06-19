import "./Board.css";
import classNames from 'classnames';
import { getRC, getSquareName } from "./Util";

/** sqName is the name of the square (e.g. a1, h8, e4) */
function Square({ bgColor, piece, sqName, isSelected, handleSquareClick }) {
    // console.log(bgColor + " " + piece);
    let svg;
    let containsPiece = true;
    switch (piece) {
        case 'p':
            svg = <img className="piece-svg" src="../svg/black_pawn.svg" />;
            break;
        case 'n':
            svg = <img className="piece-svg" src="../svg/black_knight.svg" />;
            break;
        case 'b':
            svg = <img className="piece-svg" src="../svg/black_bishop.svg" />;
            break;
        case 'r':
            svg = <img className="piece-svg" src="../svg/black_rook.svg" />;
            break;
        case 'q':
            svg = <img className="piece-svg" src="../svg/black_queen.svg" />;
            break;
        case 'k':
            svg = <img className="piece-svg" src="../svg/black_king.svg" />;
            break;
        case 'P':
            svg = <img className="piece-svg" src="../svg/white_pawn.svg" />;
            break;
        case 'N':
            svg = <img className="piece-svg" src="../svg/white_knight.svg" />;
            break;
        case 'B':
            svg = <img className="piece-svg" src="../svg/white_bishop.svg" />;
            break;
        case 'R':
            svg = <img className="piece-svg" src="../svg/white_rook.svg" />;
            break;
        case 'Q':
            svg = <img className="piece-svg" src="../svg/white_queen.svg" />;
            break;
        case 'K':
            svg = <img className="piece-svg" src="../svg/white_king.svg" />;
            break;
        default:
            svg = <></>;
            containsPiece = false;
    }
    const squareClass = classNames(bgColor, { "contains-piece": containsPiece, "selected": isSelected });
    return (<div className={squareClass} key={sqName} onClick={() => handleSquareClick(sqName)}>
        {svg}
    </div>);
}

/**
 * @param {Array.Array.<string>} pieces 
 * @param {boolean} white whether to render from white or black's point of view
 * @param {string} selectedSquare the name of the selected square (like a1),
 *                                or null if no squares are selected
 * @returns 2D array of Square components
 */
function renderSquares(pieces, white, selectedSquare, handleSquareClick) {
    const [selectedR, selectedC] = selectedSquare ? getRC(selectedSquare) : [-1, -1];
    const squares = new Array(8);
    // r and c are the actual row and column.
    // r = 0, c = 0 corresponds to a1
    // visualR and visualC are the squares shown on the screen.
    // visualR = 0, visualC = 0 corresponds to the top left corner.
    for (let visualR = 0; visualR < 8; visualR++) {
        squares[visualR] = new Array(8);
    }
    for (let r = 0; r < 8; r++) {
        for (let c = 0; c < 8; c++) {
            const bgColor = (r + c) % 2 === 0 ? "dark" : "light";  // a1 is a dark square
            const sqName = getSquareName(r, c);
            const visualR = white ? 7 - r : r;
            const visualC = white ? c : 7 - c;
            squares[visualR][visualC] = (<Square
                bgColor={bgColor}
                piece={pieces[r][c]}
                key={sqName}
                sqName={sqName}
                isSelected={r === selectedR && c === selectedC}
                handleSquareClick={handleSquareClick} />);
        }
    }
    return squares;
}

/**
 * @param {Array.Array.<string>} pieces 2D array of characters representing board state.
 * @param {string} selectedSquare the name of the selected square (like a1),
 *                                or null if no squares are selected
 * KQRBNP represent white pieces, kqrbnp represent black pieces, and . represent empty space.
 */
function Board({ pieces, selectedSquare, handleSquareClick }) {
    // console.log(pieces);

    return (
        <div className="container">
            {renderSquares(pieces, true, selectedSquare, handleSquareClick)}
        </div>
    );
}

export default Board;
