from piece import Pawn, Rook, Knight, Bishop, Queen, King
from utils import move_is_safe, is_in_check

class Board:
    def __init__(self):
        self.board = self.create_initial_board()

    def create_initial_board(self):
        board = [[None for _ in range(8)] for _ in range(8)]

        # Siyah taşlar (arka sıra)
        board[0] = [
            Rook('black'), Knight('black'), Bishop('black'), Queen('black'),
            King('black'), Bishop('black'), Knight('black'), Rook('black')
        ]
        board[1] = [Pawn('black') for _ in range(8)]

        # Beyaz taşlar (arka sıra)
        board[6] = [Pawn('white') for _ in range(8)]
        board[7] = [
            Rook('white'), Knight('white'), Bishop('white'), Queen('white'),
            King('white'), Bishop('white'), Knight('white'), Rook('white')
        ]

        return board

    def move_piece(self, start_pos, end_pos):
        sx, sy = start_pos
        ex, ey = end_pos
        piece = self.board[sx][sy]

        if piece and (ex, ey) in piece.get_valid_moves((sx, sy), self.board):
            # Şah hamle sonrası tehdit altında kalıyor mu?
            from copy import deepcopy
            new_board = deepcopy(self.board)
            new_board[ex][ey] = piece
            new_board[sx][sy] = None
            if is_in_check(new_board, piece.color):
                print("Bu hamle sonrası şah tehdit altında kalıyor!")
                return False

            self.board[ex][ey] = piece
            self.board[sx][sy] = None
            return True

        return False

    def copy(self):
        new_board = Board()
        new_board.board = [[piece for piece in row] for row in self.board]
        return new_board

    def get_all_moves(self, color):
        moves = []
        for x in range(8):
            for y in range(8):
                piece = self.board[x][y]
                if piece and piece.color == color:
                    for move in piece.get_valid_moves((x, y), self.board):
                        from copy import deepcopy
                        new_board = deepcopy(self.board)
                        new_board[move[0]][move[1]] = piece
                        new_board[x][y] = None
                        if not is_in_check(new_board, color):
                            moves.append(((x, y), move))
        return moves
