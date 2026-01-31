class MinimaxAI:
    def __init__(self, depth=3):
        self.depth = depth

    def evaluate(self, board):
        values = {'p': 1, 'n': 3, 'b': 3, 'r': 5, 'q': 9, 'k': 1000}
        score = 0
        for row in board.board:
            for piece in row:
                if piece:
                    value = values.get(piece.symbol().lower(), 0)
                    score += value if piece.color == 'white' else -value
        return score

    def minimax(self, board, depth, maximizing_player):
        if depth == 0:
            return self.evaluate(board), None

        best_move = None
        color = 'white' if maximizing_player else 'black'
        moves = board.get_all_moves(color)

        if maximizing_player:
            max_eval = float('-inf')
            for move in moves:
                board_copy = board.copy()
                board_copy.move_piece(move[0], move[1])
                eval, _ = self.minimax(board_copy, depth - 1, False)
                if eval > max_eval:
                    max_eval = eval
                    best_move = move
            return max_eval, best_move
        else:
            min_eval = float('inf')
            for move in moves:
                board_copy = board.copy()
                board_copy.move_piece(move[0], move[1])
                eval, _ = self.minimax(board_copy, depth - 1, True)
                if eval < min_eval:
                    min_eval = eval
                    best_move = move
            return min_eval, best_move

    def select_move(self, board, color):
        maximizing = color == 'white'
        _, move = self.minimax(board, self.depth, maximizing)
        return move
