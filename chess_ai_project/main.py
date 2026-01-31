from board import Board
from minimax_ai import MinimaxAI
from alphabeta_ai import AlphaBetaAI
from utils import parse_move, format_board

def main():
    board = Board()
    print(format_board(board))

    # Oyuncular
    white_player = "human"
    black_player = AlphaBetaAI(depth=3)

    turn = 'white'

    while True:
        print(f"\n{turn.upper()} sırası:")
        player = white_player if turn == 'white' else black_player

        if player == "human":
            move_input = input("Hamle yap (örnek: e2e4): ")
            start, end = parse_move(move_input.strip())
            if start and end:
                success = board.move_piece(start, end)
                if not success:
                    print("Geçersiz hamle, tekrar deneyin.")
                    continue
            else:
                print("Geçersiz giriş formatı.")
                continue
        else:
            print("AI düşünüyor...")
            move = player.select_move(board, turn)
            if move:
                board.move_piece(move[0], move[1])
                print(f"AI hamlesi: {move}")
            else:
                print("AI hamle bulamadı, oyun sona erdi.")
                break

        print(format_board(board))
        turn = 'black' if turn == 'white' else 'white'

if __name__ == "__main__":
    main()
