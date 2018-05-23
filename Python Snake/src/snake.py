## Snake
## Tyler Law - 2015

from tkinter import *
import random

# Game class holds the game's variables and methods.
class Game():

    # Initializes game's instance variables
    # and calls main game methods.
    def __init__(self):

        # Create top level window.
        self.root = Tk()

        # Create title which will be displayed at the top of the window.
        self.root.title("Snake")

        # Create sizing variables which will be used to determine the size
        # of the window and canvas.
        self.winHeight = 706
        self.winWidth = 606

        # Main variables for the game.
        self.score = 1
        self.ballx = 502
        self.bally = 502
        self.snakex = 303
        self.snakey = 283
        self.left = False
        self.right = False
        self.up = True
        self.down = False
        self.lastAxis = ""
        self.lastDirection = 0
        self.snakeArray = ["seg1"]
        self.snakeCoords = [(self.snakex, self.snakey)]
        self.gameOver = False
        self.pauseGame = False
        self.delay = 500

        # Set window size and prevent window from being resizable.
        self.root.minsize(self.winWidth, self.winHeight)
        self.root.resizable(width=False, height=False)

        # Create canvas widget on which to draw geometry.
        self.page = Canvas(self.root, width = self.winWidth, \
                           height = self.winHeight)

        # Bind arrow key press events to keyExec() method.
        self.root.bind("<Left>", self.keyExec)
        self.root.bind("<Right>", self.keyExec)
        self.root.bind("<Up>", self.keyExec)
        self.root.bind("<Down>", self.keyExec)

        # Create line which will separate game area from score and button area.
        self.page.create_line(0, 610, 706, 610)

        # Draw the line on the canvas.
        self.page.pack()

        # Create label which will display score when game ends.
        self.label = Label(self.root, text = "", width = 30, anchor=CENTER)
        self.label.place(x = 185, y = 620)

        # Create button which will start/pause/restart game.
        self.startButton = Button(self.root, text = "Start", \
                                  width = 8, command=self.pause)
        self.startButton.place(x = 260, y = 660)

        # Call spawnBall() method which will draw a food pellet.
        self.spawnBall()

        # Call main game loop.
        self.root.mainloop()

    # Handles drawing of the snake, the score and delay time.
    def snake(self):

        # Flag for pausing and unpausing game.
        if self.pauseGame == False:

            # When the game is over, set delay time to zero so snake()
            # stops getting called.
            if self.gameOver == True:
                self.delay = 0
            else:

                # snakeCoords array keeps track of location of snake segments.
                self.snakeCoords.append((self.snakex, self.snakey))

                # Delete end segments
                if len(self.snakeCoords) > self.score:
                    del self.snakeCoords[0]

                # Snake will continue moving without keyboard input, and
                # snake cannot move backwards.
                if self.left == True:
                    self.snakex -= 20
                    
                    if self.lastAxis == "snakex" and self.lastDirection == 20:
                        self.snakex += 40
                    else:
                        self.lastDirection = -20
                        self.lastAxis = "snakex"
                    
                if self.right == True:
                    self.snakex += 20
                    
                    if self.lastAxis == "snakex" and self.lastDirection == -20:
                        self.snakex -= 40
                    else:
                        self.lastDirection = 20
                        self.lastAxis = "snakex"
                    
                if self.up == True:
                    self.snakey -= 20
                    
                    if self.lastAxis == "snakey" and self.lastDirection == 20:
                        self.snakey += 40
                    else:
                        self.lastDirection = -20
                        self.lastAxis = "snakey"
                        
                if self.down == True:
                    self.snakey += 20
                    
                    if self.lastAxis == "snakey" and self.lastDirection == -20:
                        self.snakey -= 40
                    else:
                        self.lastDirection = 20
                        self.lastAxis = "snakey"

                # When the snake's head is on top of a food pellet, score
                # and delay time change, another snake segment is added
                # to snakeArray, and another food pellet is spawned.
                if self.ballx-5 == self.snakex and self.bally-5 == self.snakey:
                    if self.delay > 10:
                        self.delay -= 10
                        self.score += 1
                        self.snakeArray.append("seg" + str(len(self.snakeArray)+1))
                        self.spawnBall()

                # Delete the oldest snake segment and move it to the end
                # of snakeArray.
                else:
                    self.page.delete(self.snakeArray[0])
                    self.snakeArray.append(self.snakeArray[0])
                    del self.snakeArray[0]

                # If the snake passes the borders or hits itself, endGame()
                # is called.
                if self.snakex < 0 or self.snakex > 600 or self.snakey < 0 \
                                                       or self.snakey > 600:
                    self.endGame()
                
                for index in range(len(self.snakeCoords)):
                    if self.snakex == self.snakeCoords[index][0] \
                        and self.snakey == self.snakeCoords[index][1] \
                        and len(self.snakeCoords) > 4:
                            self.endGame()

                if self.gameOver == False:
                    self.page.create_rectangle(self.snakex, self.snakey, \
                            21+self.snakex, 21+self.snakey, fill = "red", \
                            tags = self.snakeArray[-1])

                # snake() is called again after a certain delay time,
                # redrawing the snake.
                self.page.after(self.delay, self.snake)

    # Deletes the geometry, displays score and changes the button text.
    def endGame(self):
        for index in range(len(self.snakeArray)-1):
            self.page.delete(self.snakeArray[index])
            
        self.page.delete("ball")
        self.label["text"] = "Game Over! Your score: " + str(self.score)
        self.gameOver = True
        self.startButton["text"] = "Start"

    # Called when the button is pressed, resetting instance
    # variables and/or changing button text depending on the state of the game.
    def pause(self):
        if self.startButton["text"] == "Start":
            self.label["text"] = ""

            if self.gameOver == True:
                self.snakex = 303
                self.snakey = 283
                self.left = False
                self.right = False
                self.up = True
                self.down = False
                self.lastAxis = ""
                self.lastDirection = 0
                self.snakeArray = ["seg1"]
                self.score = 1
                self.delay = 500
                self.label["text"] = ""
                self.snakeCoords = [(self.snakex, self.snakey)]
                self.spawnBall()
                self.gameOver = False

            self.startButton["text"] = "Pause"
            self.pauseGame = False
            self.snake()

        elif self.startButton["text"] == "Pause":
            self.label["text"] = ""
            self.pauseGame = True
            self.startButton["text"] = "Start"

    # Draws a food pellet at a random location, ensuring it isn't
    # drawn on top of the snake.
    def spawnBall(self):
        newx = random.randrange(3, 594, 20)+5
        newy = random.randrange(3, 594, 20)+5
        
        for index in range(len(self.snakeCoords)):
            if newx == self.snakeCoords[index][0] \
                and newy == self.snakeCoords[index][1]:
                    self.spawnBall()

        self.page.delete("ball")
        self.page.create_oval(newx, newy, newx+10, \
                              newy+10, fill = "black", tags = "ball")
        self.ballx = newx
        self.bally = newy

    # Sets flags which snake() uses to determine which key was
    # last pressed, ensuring the snake is always moving regardless of whether
    # user has pressed a key.
    def keyExec(self, event):

        ## Left
        if event.keysym_num == 65361:
            self.left = True
            self.right = False
            self.up = False
            self.down = False

        ## Right
        if event.keysym_num == 65363:
            self.left = False
            self.right = True
            self.up = False
            self.down = False

        ## Up
        if event.keysym_num == 65362:
            self.left = False
            self.right = False
            self.up = True
            self.down = False

        ## Down
        if event.keysym_num == 65364:
            self.left = False
            self.right = False
            self.up = False
            self.down = True

Game()
