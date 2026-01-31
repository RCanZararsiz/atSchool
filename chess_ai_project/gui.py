import pygame
from board import Board
from alphabeta_ai import AlphaBetaAI

# === Pygame ayarları ===
WIDTH, HEIGHT = 640, 640
SQ_SIZE = WIDTH // 8
FPS = 15

PIECE_IMAGES = {}

def load_images():
    pieces = ['wp', 'bp', 'wr', 'br', 'wn', 'bn', 'wb', 'bb', 'wq', 'bq', 'wk', 'bk']
    for piece in pieces:
        PIECE_IMAGES[piece] = pygame.transform.scale(
            pygame.image.load(f"assets/{piece}.png"), (SQ_SIZE, SQ_SIZE)
        )

def draw_board(screen, board):
    colors = [pygame.Color("white"), pygame.Color("gray")]
    for row in range(8):
        for col in range(8):
            color = colors[(row + col) % 2]
            pygame.draw.rect(screen, color, pygame.Rect(col * SQ_SIZE, row * SQ_SIZE, SQ_SIZE, SQ_SIZE))

            piece = board.board[row][col]
            if piece:
                symbol = piece.symbol().lower() if callable(piece.symbol) else '?'
                image_key = piece.color[0] + symbol
                if image_key in PIECE_IMAGES:
                    screen.blit(PIECE_IMAGES[image_key], pygame.Rect(col * SQ_SIZE, row * SQ_SIZE, SQ_SIZE, SQ_SIZE))

def get_board_pos_from_mouse(pos):
    x, y = pos
    return y // SQ_SIZE, x // SQ_SIZE

def main():
    pygame.init()
    screen = pygame.display.set_mode((WIDTH, HEIGHT))
    pygame.display.set_caption("Satranç - AlphaBeta AI")
    clock = pygame.time.Clock()

    board = Board()
    ai = AlphaBetaAI(depth=3)
    player_color = 'white'
    turn = 'white'
    selected = None
    running = True

    load_images()

    while running:
        draw_board(screen, board)
        pygame.display.flip()

        if turn != player_color:
            print("AI düşünüyor...")
            ai_move = ai.select_move(board, turn)
            if ai_move:
                board.move_piece(ai_move[0], ai_move[1])
                turn = 'black' if turn == 'white' else 'white'
            else:
                print("AI hamle bulamadı.")
                running = False
            continue

        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                running = False

            elif event.type == pygame.MOUSEBUTTONDOWN:
                pos = pygame.mouse.get_pos()
                row, col = get_board_pos_from_mouse(pos)

                if selected:
                    if board.move_piece(selected, (row, col)):
                        turn = 'black' if turn == 'white' else 'white'
                    selected = None
                else:
                    piece = board.board[row][col]
                    if piece and piece.color == player_color:
                        selected = (row, col)

        clock.tick(FPS)

    pygame.quit()

if __name__ == "__main__":
    main()
