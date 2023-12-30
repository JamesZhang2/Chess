import "./Board.css"

function Square({ bgColor, piece }) {
    // console.log(bgColor + " " + piece);
    let svg;
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
    }
    return (<div className={bgColor}>
        {svg}
    </div>);
}

function renderSquares(position) {
    console.log(position);
    let squares = [];
    for (let r = 0; r < 8; r++) {
        squares.push([]);
        for (let c = 0; c < 8; c++) {
            let bgColor = (r + c) % 2 === 0 ? "light" : "dark";
            squares[r].push(<Square bgColor={bgColor} piece={position[r][c]} key={`${r}-${c}`} />);
        }
    }
    return squares;
}

function Board() {
    const startPos = [
        "rnbqkbnr".split(""),
        "pppppppp".split(""),
        "........".split(""),
        "........".split(""),
        "........".split(""),
        "........".split(""),
        "PPPPPPPP".split(""),
        "RNBQKBNR".split(""),
    ]
    console.log(startPos);

    return (
        <div className="container">
            {renderSquares(startPos)}
        </div>
    );
}

export default Board