#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <errno.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>

static const int BUFFER_SIZE = 512;
int numLines = 10;// default size

int seek_line(int fd, int linenum);
char *read_line(int fd);
char read_char(int fd);
char *buf;
char *line;
int charsLeft = 0;
int charAt = 0;
int fileDescInput;
int fileDescOutput;

/*
The basic logic for main should be as follows:
1. examine the command line arguments to determine whether the -n option was given
or not, and whether a file (path) was given or not;
2. if a file path was given, open the file (and get a FD), else use the FD for standard
input to read from;
3. call seek_line() to move the file offset to the start of the correct line;
4. if the line was found, iteratively call read_line() and print the returned lines, until
the correct number of lines have been printed (or end-of-file/error).
*/

int main(int argc, char* argv[]){
	int firstLine;
	fileDescOutput = fileno(stdout);
	buf = malloc(BUFFER_SIZE);
	line = malloc(BUFFER_SIZE);
	if(argc > 1){
		if (strncmp(argv[1],"-n",2) == 0){
			numLines = atoi(argv[1]+2);
			firstLine = atoi(argv[2]);
			if ((fileDescInput = open(argv[3], O_RDONLY)) == -1) {
				//fprintf(stderr,"middle: Error opening file %s: %s\n",argv[3], strerror(errno));
				fileDescInput = fileno(stdin);
			}
		}
		else{
			firstLine = atoi(argv[1]);
			if ((fileDescInput = open(argv[2], O_RDONLY)) == -1) {
				//fprintf(stderr,"middle: Error opening file %s: %s\n",argv[2], strerror(errno));
				fileDescInput = fileno(stdin);
			}
		}

		if (seek_line(fileDescInput,firstLine) == 1){
			for(int i = 0;i<numLines;i++){
				write(fileDescOutput,read_line(fileDescInput),BUFFER_SIZE);
			}
		}			
	}
}
			
/*
The basic logic for seek_line() should be as follows:
1. iteratively call read_char() until we have read to the end of line_num lines 
 (from thecurrent position in the file) or end-of-file/error occurs;
2. return “true” if the offset was moved ahead line_num lines, else return “false” 
(re-member that we use int’s for Booleans: 1/0 for T/F).
*/
	
//seek_line() must use read_char() to read through the open file.	

int seek_line(int fd, int linenum){
	char ch;
	for(int i = 0;i<linenum;){
		ch = read_char(fd);
		if(ch == EOF) {
			perror("middle: seek_line: EOF or Error reading line");
			return 0;}
		if (ch == '\n') {
			i++;
		}
	}
	return 1;
}
/*
The basic logic for read_line() should be as follows:
1. if memory for the line buffer has not been allocated, allocate some;
2. iteratively call read_char() until the end of the current line is reached, storing each
returned char in the line buffer, expanding the line buffer if necessary before storing
each character;
3. make certain the line buffer contains a valid C string, and return it.
*/
	
//read_line() must use read_char() to read through the open file.

char *read_line(int fd){
	char ch;
	for(int i = 0;;){
		ch = read_char(fd);
		if(ch == '\0') {
			perror("middle: read_line: EOF or Error reading line");
			exit(EXIT_FAILURE);
		}
		if (ch == '\n') {
			line[i] = ch;
			return(line);
		} else if (i > (BUFFER_SIZE-1)) {
			return(line);		//needs to be fixed
		}
		else{
			line[i] = ch;
			i++;
		}
	}
return "ERROR";
}
/*
read_char() must use buffering in order to be efficient reading individual characters from the file. 
 
The basic logic for read_char() should be as follows:
1. check if char’s remain in the file buffer, else fill it by calling read(), checking whether
the file end was reached or an error occurred;
2. unless end-of-file/error, return the next char from the file buffer;
3. return EOF in case of end-of-file or error when calling read().
* */
char read_char(int fd){
	if (fileDescInput == fileno(stdin)){
		read(fileDescInput,buf,1);
			return buf[0];
	}
	if (charsLeft > 0){ // if chars remain in file buffer
		char c = buf[charAt];
		charAt++;
		charsLeft--;
		return c;
	}else{
		charsLeft = read(fd,buf,BUFFER_SIZE);
		charAt = 0;
		if (charsLeft == -1){
			perror("middle: read_char: Error reading line");
			exit(EXIT_FAILURE);
		}else if (charsLeft == 0){
			return EOF;
		}
		char c = buf[charAt];
		charAt++;
		return c;
	}
	return '0';	
}
