class AlphaBetaAI:
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

    def alphabeta(self, board, depth, alpha, beta, maximizing_player):
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
                eval, _ = self.alphabeta(board_copy, depth - 1, alpha, beta, False)
                if eval > max_eval:
                    max_eval = eval
                    best_move = move
                alpha = max(alpha, eval)
                if beta <= alpha:
                    break
            return max_eval, best_move
        else:
            min_eval = float('inf')
            for move in moves:
                board_copy = board.copy()
                board_copy.move_piece(move[0], move[1])
                eval, _ = self.alphabeta(board_copy, depth - 1, alpha, beta, True)
                if eval < min_eval:
                    min_eval = eval
                    best_move = move
                beta = min(beta, eval)
                if beta <= alpha:
                    break
            return min_eval, best_move

    def select_move(self, board, color):
        maximizing = color == 'white'
        _, move = self.alphabeta(board, self.depth, float('-inf'), float('inf'), maximizing)
        return move
