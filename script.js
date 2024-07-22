const boardElement = document.getElementById('board');
const turnIndicator = document.getElementById('turn-indicator');
let board = Array(8).fill().map(() => Array(8).fill(-1));

board[3][3] = 1;
board[3][4] = 0;
board[4][3] = 0;
board[4][4] = 1;

let currentTurn = 0; 

function drawBoard() {
    boardElement.innerHTML = '';
    const availableMoves = getAvailableMoves(currentTurn);

    for (let i = 0; i < 8; i++) {
        for (let j = 0; j < 8; j++) {
            const cell = document.createElement('div');
            cell.classList.add('cell');

            if (board[i][j] === 0) {
                cell.classList.add('black');
            } else if (board[i][j] === 1) {
                cell.classList.add('white');
            }

            if (availableMoves.some(([x, y]) => x === i && y === j)) {
                cell.classList.add('available-move');
            }

            cell.addEventListener('click', () => handleMove(i, j));
            boardElement.appendChild(cell);
        }
    }
    updateTurnIndicator();
    checkGameOver();
}

function updateTurnIndicator() {
    turnIndicator.textContent = currentTurn === 0 ? "Current Turn: Black" : "Current Turn: White";
}

function isValidMove(x, y, color) {
    if (board[x][y] !== -1) return false;
    let valid = false;
    const directions = [-1, 0, 1];

    directions.forEach(dx => {
        directions.forEach(dy => {
            if (dx === 0 && dy === 0) return;
            let nx = x + dx, ny = y + dy, foundOpponent = false;

            while (nx >= 0 && nx < 8 && ny >= 0 && ny < 8 && board[nx][ny] === (color ^ 1)) {
                foundOpponent = true;
                nx += dx;
                ny += dy;
            }

            if (foundOpponent && nx >= 0 && nx < 8 && ny >= 0 && ny < 8 && board[nx][ny] === color) {
                valid = true;
            }
        });
    });

    return valid;
}

function getAvailableMoves(color) {
    const moves = [];
    for (let i = 0; i < 8; i++) {
        for (let j = 0; j < 8; j++) {
            if (isValidMove(i, j, color)) {
                moves.push([i, j]);
            }
        }
    }
    return moves;
}

function handleMove(x, y) {
    if (!isValidMove(x, y, currentTurn)) return;

    board[x][y] = currentTurn;
    flipTiles(x, y, currentTurn);
    currentTurn ^= 1;
    drawBoard();
}

function flipTiles(x, y, color) {
    const directions = [-1, 0, 1];

    directions.forEach(dx => {
        directions.forEach(dy => {
            if (dx === 0 && dy === 0) return;
            let nx = x + dx, ny = y + dy, tilesToFlip = [];

            while (nx >= 0 && nx < 8 && ny >= 0 && ny < 8 && board[nx][ny] === (color ^ 1)) {
                tilesToFlip.push([nx, ny]);
                nx += dx;
                ny += dy;
            }

            if (nx >= 0 && nx < 8 && ny >= 0 && ny < 8 && board[nx][ny] === color) {
                tilesToFlip.forEach(([fx, fy]) => {
                    board[fx][fy] = color;
                });
            }
        });
    });
}

function boardScore() {
    let blackTiles = 0;
    let whiteTiles = 0;

    for (let i = 0; i < 8; i++) {
        for (let j = 0; j < 8; j++) {
            if (board[i][j] === 0) {
                blackTiles++;
            } else if (board[i][j] === 1) {
                whiteTiles++;
            }
        }
    }

    return (currentTurn === 0) ? (blackTiles - whiteTiles) : (whiteTiles - blackTiles);
}

function checkGameOver() {
    const blackMoves = getAvailableMoves(0).length;
    const whiteMoves = getAvailableMoves(1).length;
    
    if (blackMoves === 0 && whiteMoves === 0) {
        const blackTiles = board.flat().filter(cell => cell === 0).length;
        const whiteTiles = board.flat().filter(cell => cell === 1).length;

        let winnerMessage;
        if (blackTiles > whiteTiles) {
            winnerMessage = "Black wins!";
        } else if (whiteTiles > blackTiles) {
            winnerMessage = "White wins!";
        } else {
            winnerMessage = "It's a draw!";
        }

        setTimeout(() => alert(winnerMessage), 0);
        document.querySelectorAll('.cell').forEach(cell => cell.removeEventListener('click', handleMove));
    }
}

function getBoardCopy() {
    return board.map(row => row.slice());
}

drawBoard();
