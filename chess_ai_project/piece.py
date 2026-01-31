class Piece:
    def __init__(self, color):
        self.color = color

    def symbol(self):
        return '?'

    def get_valid_moves(self, position, board):
        return []


class Pawn(Piece):
    def symbol(self):
        return 'P'

    def get_valid_moves(self, pos, board):
        moves = []
        x, y = pos
        direction = -1 if self.color == 'white' else 1
        start_row = 6 if self.color == 'white' else 1

        if 0 <= x + direction < 8 and board[x + direction][y] is None:
            moves.append((x + direction, y))
            if x == start_row and board[x + 2 * direction][y] is None:
                moves.append((x + 2 * direction, y))

        for dy in [-1, 1]:
            nx, ny = x + direction, y + dy
            if 0 <= nx < 8 and 0 <= ny < 8:
                target = board[nx][ny]
                if target and target.color != self.color:
                    moves.append((nx, ny))

        return moves


class Rook(Piece):
    def symbol(self):
        return 'R'

    def get_valid_moves(self, pos, board):
        return self._linear_moves(pos, board, directions=[(1, 0), (-1, 0), (0, 1), (0, -1)])

    def _linear_moves(self, pos, board, directions):
        moves = []
        x, y = pos
        for dx, dy in directions:
            nx, ny = x + dx, y + dy
            while 0 <= nx < 8 and 0 <= ny < 8:
                if board[nx][ny] is None:
                    moves.append((nx, ny))
                elif board[nx][ny].color != self.color:
                    moves.append((nx, ny))
                    break
                else:
                    break
                nx += dx
                ny += dy
        return moves


class Knight(Piece):
    def symbol(self):
        return 'N'

    def get_valid_moves(self, pos, board):
        moves = []
        x, y = pos
        deltas = [
            (2, 1), (2, -1), (-2, 1), (-2, -1),
            (1, 2), (1, -2), (-1, 2), (-1, -2)
        ]
        for dx, dy in deltas:
            nx, ny = x + dx, y + dy
            if 0 <= nx < 8 and 0 <= ny < 8:
                target = board[nx][ny]
                if target is None or target.color != self.color:
                    moves.append((nx, ny))
        return moves


class Bishop(Piece):
    def symbol(self):
        return 'B'

    def get_valid_moves(self, pos, board):
        return self._linear_moves(pos, board, directions=[(1, 1), (1, -1), (-1, 1), (-1, -1)])

    def _linear_moves(self, pos, board, directions):
        moves = []
        x, y = pos
        for dx, dy in directions:
            nx, ny = x + dx, y + dy
            while 0 <= nx < 8 and 0 <= ny < 8:
                if board[nx][ny] is None:
                    moves.append((nx, ny))
                elif board[nx][ny].color != self.color:
                    moves.append((nx, ny))
                    break
                else:
                    break
                nx += dx
                ny += dy
        return moves


class Queen(Piece):
    def symbol(self):
        return 'Q'

    def get_valid_moves(self, pos, board):
        return self._linear_moves(pos, board, directions=[
            (1, 0), (-1, 0), (0, 1), (0, -1),
            (1, 1), (1, -1), (-1, 1), (-1, -1)
        ])

    def _linear_moves(self, pos, board, directions):
        moves = []
        x, y = pos
        for dx, dy in directions:
            nx, ny = x + dx, y + dy
            while 0 <= nx < 8 and 0 <= ny < 8:
                if board[nx][ny] is None:
                    moves.append((nx, ny))
                elif board[nx][ny].color != self.color:
                    moves.append((nx, ny))
                    break
                else:
                    break
                nx += dx
                ny += dy
        return moves


class King(Piece):
    def symbol(self):
        return 'K'

    def get_valid_moves(self, pos, board):
        moves = []
        x, y = pos
        for dx in [-1, 0, 1]:
            for dy in [-1, 0, 1]:
                if dx == 0 and dy == 0:
                    continue
                nx, ny = x + dx, y + dy
                if 0 <= nx < 8 and 0 <= ny < 8:
                    target = board[nx][ny]
                    if target is None or target.color != self.color:
                        moves.append((nx, ny))
        return moves
