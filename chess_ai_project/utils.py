def parse_move(move_str):
    """'e2e4' gibi notasyonu ((6, 4), (4, 4)) gibi koordinatlara çevirir."""
    if len(move_str) != 4:
        return None, None

    files = {'a': 0, 'b': 1, 'c': 2, 'd': 3,
             'e': 4, 'f': 5, 'g': 6, 'h': 7}
    ranks = {'1': 7, '2': 6, '3': 5, '4': 4,
             '5': 3, '6': 2, '7': 1, '8': 0}

    try:
        start_col = files[move_str[0]]
        start_row = ranks[move_str[1]]
        end_col = files[move_str[2]]
        end_row = ranks[move_str[3]]
    except KeyError:
        return None, None

    return (start_row, start_col), (end_row, end_col)


def format_board(board):
    """Tahtayı konsola basılabilir güzel bir formata çevirir."""
    display = "  a b c d e f g h\n"
    for row in range(8):
        display += str(8 - row) + " "
        for col in range(8):
            piece = board.board[row][col]
            if piece:
                symbol = piece.symbol()
                if piece.color == 'white':
                    symbol = symbol.upper()
                else:
                    symbol = symbol.lower()
                display += symbol + " "
            else:
                display += ". "
        display += str(8 - row) + "\n"
    display += "  a b c d e f g h"
    return display


def is_in_check(board_state, color):
    """Verilen renk için şah tehdit altında mı?"""
    king_pos = None
    for x in range(8):
        for y in range(8):
            piece = board_state[x][y]
            if piece and piece.color == color and piece.symbol() == 'K':
                king_pos = (x, y)
                break

    if not king_pos:
        return True  # Şah yoksa oyun bitmiş olabilir

    enemy_color = 'black' if color == 'white' else 'white'
    for x in range(8):
        for y in range(8):
            piece = board_state[x][y]
            if piece and piece.color == enemy_color:
                if king_pos in piece.get_valid_moves((x, y), board_state):
                    return True

    return False


def move_is_safe(board, start_pos, end_pos, color):
    """Hamle yapıldığında şah tehdit altında kalıyor mu?"""
    from copy import deepcopy
    test_board = deepcopy(board)
    sx, sy = start_pos
    ex, ey = end_pos
    piece = test_board[sx][sy]
    test_board[ex][ey] = piece
    test_board[sx][sy] = None
    return not is_in_check(test_board, color)
