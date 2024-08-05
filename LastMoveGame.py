#Function for printing map. Gets size and two dimensional array inputs
def print_map(size, twoDimArray):
    letters = "ABCDEFG"
    #Print Top Left Margin And Letter Coordinates
    print("     ",end="")
    for letter in range(size):
        print("", letters[letter] ,end="  ")
    #Print Line Break
    print("")
    print("   ", '-' * ((size * 4) + 1) )
    #Print Number Coordinates, Players and Small Stones
    for i in range(size): 
        print (i + 1, end="   ")
        for j in range(size):
            print("|",twoDimArray[i][j] ,end=" ")
        print ("|  ", i + 1)
        print("   ", '-' * ((size * 4) + 1) )
    print("     ",end="")
    for letter in range(size):
        print("", letters[letter] ,end="  ")
    print("")

#Finding player's positions
def find_player_pos(player, twoDimArray):
    for y in range (len(twoDimArray)):
        for x in range (len(twoDimArray)):
            if (twoDimArray[y][x] == player):
                return ([x,y])

#Checking if move is available
def available_move_check(twoDimArray, to_move):
    available = 0
    try:
        if (twoDimArray[to_move[1]][to_move[0]] == " "):
            available = 1
        else:
            available = 0
    except:
        available = 0
    return (available)

#Get new coordinates for player
def move_coords(way, player, twoDimArray):
    available = 0
    player_pos = find_player_pos(player, twoDimArray)
    if way == "N":
        player_pos[1] = player_pos[1] - 1
        available = available_move_check(twoDimArray, player_pos)
    elif way == "S":
        player_pos[1] = player_pos[1] + 1
        available = available_move_check(twoDimArray, player_pos)
    elif way == "W":
        player_pos[0] = player_pos[0] - 1
        available = available_move_check(twoDimArray, player_pos)
    elif way == "E":
        player_pos[0] = player_pos[0] + 1
        available = available_move_check(twoDimArray, player_pos)
    elif way == "NW":
        player_pos[0] = player_pos[0] - 1
        player_pos[1] = player_pos[1] - 1
        available = available_move_check(twoDimArray, player_pos)
    elif way == "NE":
        player_pos[0] = player_pos[0] + 1
        player_pos[1] = player_pos[1] - 1
        available = available_move_check(twoDimArray, player_pos)
    elif way == "SW":
        player_pos[0] = player_pos[0] - 1
        player_pos[1] = player_pos[1] + 1
        available = available_move_check(twoDimArray, player_pos)
    elif way == "SE":
        player_pos[0] = player_pos[0] + 1
        player_pos[1] = player_pos[1] + 1
        available = available_move_check(twoDimArray, player_pos)
    if(available == 1):
        return(player_pos)
    else:
        return([-1, -1])
    
#Allows the player to drop a small stone
def place_stone(player, twoDimArray):
    tmpwords = "ABCDEFG"
    tmpnums = "1234567"
    words = tmpwords[0:len(twoDimArray)]
    nums = tmpnums[0:len(twoDimArray)]
    while (True):
        coord = input(f"Player {player}, please enter the location where you want to place a small stone (like1A): ")
        try:
            wordcheck = coord[1].upper()
            if (len(coord) == 2):
                if (coord[0] in nums and wordcheck in words):
                    if (twoDimArray[nums.index(coord[0])][words.index(wordcheck)] == " "):
                        twoDimArray[nums.index(coord[0])][words.index(wordcheck)] = "O"
                        break
                    else:
                        print_map(len(twoDimArray), twoDimArray)
                        print("Wrong Input")
                else:
                    print_map(len(twoDimArray), twoDimArray)
                    print("Wrong Input")
            else:
                print_map(len(twoDimArray), twoDimArray)
                print("Wrong Input")
        except:
            print("Wrong Input")

#Check game ends
def game_end_check(player, twoDimArray):
    ways = ["N", "S", "E", "W", "NW", "NE", "SW", "SE"]
    for item in ways:
        new_xy = move_coords(item, player, twoDimArray)
        if (new_xy[0] > -1 and new_xy[1] < len(twoDimArray) and new_xy[0] < len(twoDimArray) and new_xy[1] > -1):
            return (0)
    return (1)

#Performs the process of moving the player's stone
def player_move(twoDimArray, player):
    way = input (f"Player {player}, please enter the direction you want to move your own big stone (N, S, \
E, W, NE, NW, SE, SW): ").upper()
    ways = ["N", "S", "E", "W", "NW", "NE", "SW", "SE"]
    if (way not in ways):
        return (1)
    new_xy = move_coords(way, player, twoDimArray)
    old_xy = find_player_pos(player, twoDimArray)
    if (new_xy[0] > -1 and new_xy[1] < len(twoDimArray) and new_xy[0] < len(twoDimArray) and new_xy[1] > -1):
        twoDimArray[old_xy[1]][old_xy[0]] = " "
        twoDimArray[new_xy[1]][new_xy[0]] = player
        return (0)
    else:
        return (1)

#Print game end
def print_game_end(lastmove, player1, player2, twoDimArray):
    if (lastmove == 1):
        if (game_end_check(player1, twoDimArray) == 1):
            print(f"Player {player2} won the game.")
            return (1)
        elif (game_end_check(player2, twoDimArray) == 1):
            print(f"Player {player1} won the game.")
            return (1)
    else:
        if (game_end_check(player2, twoDimArray) == 1):
            print(f"Player {player1} won the game.")
            return (1)
        elif (game_end_check(player1, twoDimArray) == 1):
            print(f"Player {player2} won the game.")
            return (1)
    return (0)
        

#Starts game loop
def game_loop(twoDimArray, player1, player2, size):
    while (True):
        player1_move_status = 1
        player2_move_status = 1
        game_status = print_game_end(2, player1, player2, twoDimArray)
        if (game_status == 1):
            break
        #Player 1 move and place stone
        while (player1_move_status != 0):
            print_map(size, twoDimArray)
            player1_move_status = player_move(twoDimArray, player1)
            if (player1_move_status == 0):
                game_status = print_game_end(1, player1, player2, twoDimArray)
                if (game_status == 1):
                    break
                print_map(size,twoDimArray)
                place_stone(player1, twoDimArray)
            else:
                print_map(size, twoDimArray)
                print("Invalid Input")
        print_map(size, twoDimArray)
        #Check if player1 wins
        game_status = print_game_end(1, player1, player2, twoDimArray)
        if (game_status == 1):
                break
        #Player 2 move and place stone
        while (player2_move_status != 0):
            game_status = print_game_end(1, player1, player2, twoDimArray)
            if (game_status == 1):
                    break
            player2_move_status = player_move(twoDimArray, player2)
            if (player2_move_status == 0):
                print_map(size,twoDimArray)
                game_status = print_game_end(2, player1, player2, twoDimArray)
                if (game_status == 1):
                    break
                print_map(size,twoDimArray)
                place_stone(player2, twoDimArray)
                print_map(size, twoDimArray)
            else:
                print_map(size, twoDimArray)
                print("Invalid Input")
        game_status = print_game_end(2, player1, player2, twoDimArray)
        if (game_status == 1):
            break
        print_map(size, twoDimArray)

#Starts the game
def start_game():
    capital_letters = "ABCDEFGHIJKLMNPQRSTUVWXYZ"
    player1 = input("Enter a capital letter to represent player 1 (except O): ").upper()
    while (player1==0 or player1 not in capital_letters):
        print("Wrong input for players, Please try again")
        player1 = input("Enter a capital letter to represent player 1 (except O): ").upper()
    player2 = input("Enter a capital letter to represent player 2 (except O): ").upper()
    while (player1==0 or player2 not in capital_letters or player2==player1):
        print("Wrong input for players, Please try again")
        player2 = input("Enter a capital letter to represent player 2 (except O): ").upper()   
    #Get Map Size
    size=0
    while (size not in [3,5,7]):
        try:
            size = int(input("Enter the row/column number of the playing field (3, 5, 7): "))

            if size not in [3,5,7]:
                print("Wrong choice")

        except:
            print("Not a number")

    #Create Two Dimension Array
    center = int((size / 2))
    twoDimArray = []
    for i in range(size):
        oneDimArray = []
        for j in range(size):
            #Put Players Center Of Top And Bottom Lines
            if (i == 0 and j == center):
                oneDimArray.append(player1)
            elif (i == size - 1 and j == center):
                oneDimArray.append(player2)
            else:
                oneDimArray.append(" ")
        twoDimArray.append(oneDimArray)
    game_loop(twoDimArray, player1, player2, size)

#Determines whether the game shold be started or not
def main():
    #Get Player Represents
    play = "Y"
    while (True):
        if (play == "Y"):
            start_game()
            play = "C"
        elif (play == "N"):
            break
        else:
            play = input("\nWould you like to play again(Y/N)?:").upper()
            if (play not in ['y','Y','n','N']):
                print("Wrong Input")
        

if __name__ == "__main__":
    main()


