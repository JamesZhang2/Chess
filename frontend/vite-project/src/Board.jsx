import "./Board.css";
import classNames from 'classnames';
import { getRC, getSquareName } from "./Util";

/** sqName is the name of the square (e.g. a1, h8, e4) */
function Square({ bgColor, piece, sqName, isSelected, isLegalDest, handleSquareClick }) {
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
    const squareClass = classNames("square", bgColor, { "contains-piece": containsPiece, "selected": isSelected });
    const centerCircleClass = classNames("center-circle", { "show": isLegalDest })
    // console.log(`sqName=${sqName}, isLegalDest=${isLegalDest}`);
    return (<div
        className={squareClass}
        key={sqName}
        onClick={e => { e.stopPropagation(); handleSquareClick(sqName) }}>
        {svg}
        <div className={centerCircleClass}></div>
    </div>);
}

/**
 * The promotion overlay screen
 * @param {boolean} white 
 */
function PromotionOverlay({ white, handlePromotionSelection, handlePromotionCancellation }) {
    return <div className="promotion-overlay" onClick={e => handlePromotionCancellation(e)}>
        <p>Select the piece that you want to promote to:</p>
        <div className="promotion-selection-container">
            <Square bgColor={"light"} piece={white ? "Q" : "q"} sqName={white ? "Q" : "q"} isSelected={false} isLegalDest={false} handleSquareClick={handlePromotionSelection} />
            <Square bgColor={"light"} piece={white ? "R" : "r"} sqName={white ? "R" : "r"} isSelected={false} isLegalDest={false} handleSquareClick={handlePromotionSelection} />
            <Square bgColor={"light"} piece={white ? "B" : "b"} sqName={white ? "B" : "b"} isSelected={false} isLegalDest={false} handleSquareClick={handlePromotionSelection} />
            <Square bgColor={"light"} piece={white ? "N" : "n"} sqName={white ? "N" : "n"} isSelected={false} isLegalDest={false} handleSquareClick={handlePromotionSelection} />
        </div>
    </div>
}

/**
 * @param {Array<Array<string>>} pieces 2D array of characters representing board state.
 * @param {boolean} white whether to render from white or black's point of view
 * @param {string} selectedSquare the name of the selected square (like a1),
 *                                or null if no squares are selected
 * @param {Set<string>} legalDests set of legal destination squares for the selected square
 * @param {(squareName: string) => void} handleSquareClick handler for clicking a square
 * @param {boolean} showPromotionOverlay whether to show the promotion overlay
 * @param {(squareName: string) => void} handlePromotionSelection handler for promotion selection
 * @param {(e: PointerEvent) => void} handlePromotionCancellation
 *        handler for promotion cancellation (clicking anywhere outside the 4 choices)
 * KQRBNP represent white pieces, kqrbnp represent black pieces, and . represent empty space.
 */
function Board({ pieces, white, selectedSquare, legalDests, handleSquareClick, showPromotionOverlay, handlePromotionSelection, handlePromotionCancellation }) {
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
                isLegalDest={legalDests.has(sqName)}
                handleSquareClick={handleSquareClick} />);
        }
    }
    if (showPromotionOverlay) {
        return (
            <div className="board-container">
                {squares}
                <PromotionOverlay white={white} handlePromotionSelection={handlePromotionSelection} handlePromotionCancellation={handlePromotionCancellation} />
            </div>
        );
    } else {
        return (
            <div className="board-container">
                {squares}
            </div>
        )
    }
}

export default Board;
