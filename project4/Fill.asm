// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/4/Fill.asm

// Runs an infinite loop that listens to the keyboard input. 
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel. When no key is pressed, 
// the screen should be cleared.


(RESET)  
    @8192
    D=A
    @i
    M=D

(LOOP)
   //Decrease i by 1 
   @i
   M=M-1 
   D=M

   //if(i<0) reset i to 8192
   @RESET
   D;JLT

   // READ the keyboard
   @KBD
   D=M 

   //if(D=0) goto WHITELOOP
   @WHITELOOP
   D;JEQ

   //if(D!=0) goto BLACKLOOP
   @BLACKLOOP
   0;JMP
        
 (BLACKLOOP)
    @SCREEN
    //Compute the address SCREEN + i
    D=A
    @i
    A=D+M
    //set the screen to black
    M=-1
    //return to the main LOOP
    @LOOP
    0;JMP 

(WHITELOOP)
    @SCREEN
    //Compute the address SCREEN + i
    D=A
    @i
    A=D+M
    //set the screen to white
    M=0
    //return to the main LOOP
    @LOOP
    0;JMP


    



    



    



