// Solution for Lab #2, CS 306 in Fall 2013
// Author: Norman Carver
//
// middle, a utility similar to the Linux head/tail commands.
// Will print a number of lines starting from a specified line
// in a file or standard input.
//
// This version handles multiple file arguments, formatting output
// similar to the way that head/tail do.
// Also attempts to error check numeric arguments for N and FIRST_LINE.
//
// Compile as:  gcc -Wall -o middle lab2.c
// Call as:  middle [-nN] FIRST_LINE [FILE]...

#include <unistd.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>


// Preprocessor constants:
#define FILE_BUFFER_SIZE 512
#define INIT_BUFF_SIZE 50
#define INC_BUFF_SIZE 10


// Prototypes:
long convert_intstr(char *intstr);
int middle_file(int fd, int first_line, int num_lines);
int seek_line(int fd, int linenum);
char *read_line(int fd);
int read_char(int fd);


int main(int argc, char *argv[])
{
  long first_line;            //First line to print, will get from argv.
  long  num_lines = 10;       //Number of lines to print, default is 10 if no -n option.
  int file_index = 2;         //Argv index of first file argument if given (to be determined).
  int fd = 0;                 //Open file FD, default is 0 for standard input (if no files given)
  char *file = NULL;          //Current file path (initialized so can test).
  int status = EXIT_SUCCESS;  //Exit status, needed since error for any file should cause failure return.

  //Check if called with proper number of arguments, else print usage message:
  if (argc < 2) {
    fprintf(stderr,"Usage:  middle [-nN] FIRST_LINE [FILE]...");
    exit(EXIT_FAILURE); }

  //Decode arguments based on number of arguments,
  //determining if -n option and where files argument(s) start.
  //(Does some error checking for invalid numeric arguments.)
  if (argc > 2 && strncmp(argv[1],"-n",2) == 0) {
    //Have -n option:
    num_lines = convert_intstr(argv[1] + 2);
    first_line = convert_intstr(argv[2]);
    file_index = 3; }
  else {
    //No -n option:
    first_line = convert_intstr(argv[1]);
    file_index = 2; }

  //Check if got valid numeric arguments:
  if (errno || num_lines < 0 || first_line < 0)
    return EXIT_FAILURE;

  //See if file argument(s) have been provided, and if so process each:
  if (argc > file_index) {
    //Loop through the provided file argument(s), opening each and processing it:
    int num_files = argc - file_index;
    for (int fi=file_index; fi<argc; fi++) {
      file = argv[fi];
      //Open the file and process it if successful:
      if ((fd = open(file,O_RDONLY)) != -1) {
        //Current file was opened successfully:
        //If multiple file args are being processed, print file pathname first:
        //(This duplicates head/tails's format, including printing a blank line between multiple files.)
        if (num_files > 1) {
          //Print fileargument like head/tail do:
          if (fi > file_index) printf("\n");
          printf("==> %s <==\n",file); }

        //Perform middle operations on open file, check status:
	if (middle_file(fd,first_line,num_lines) != EXIT_SUCCESS)
          status = EXIT_FAILURE;
        //Be sure to close file to avoid having multiple FDs in use:
	close(fd); }
      else {
        //Current file could not be opened, update exit status and print message:
        status = EXIT_FAILURE; 
        fprintf(stderr,"middle: cannot open file \"%s\" for reading: %s\n", file, strerror(errno)); } } }
  else
    //No file arguments given, so read from standard input:
    status = middle_file(0,first_line,num_lines);

  return status;
}



//Function to take a string of digits representing an integer and return it as a long,
//checking if there are non-digits chars or if result is out of range.
long convert_intstr(char *intstr)
{
  char *nondigits;
  long val = strtol(intstr, &nondigits, 10);

  if (errno) {
    fprintf(stderr,"middle: out of range integer string argument: \"%s\"\n", intstr);
    return -1; }

  if (*nondigits != '\0') {
    fprintf(stderr,"middle: invalid integer string argument: \"%s\"\n", intstr);
    return -1; }

  return val;
}



//Function to perform the functionality of middle on an open file.
//Returns exit status for the file.
int middle_file(int fd, int first_line, int num_lines)
{
  //Move to correct position in file, checking if successful:
  if (! seek_line(fd,first_line-1)) {
    //Did not succeed, so terminate, checking if error or just insufficient lines:
    if (errno)
      return EXIT_FAILURE;
    else
      return EXIT_SUCCESS; }

  //Read and print out the specified number of lines:
  //(If insufficient lines in file, simply stop--not an error.)
  char *line;
  int lines_printed = 0;
  while (lines_printed < num_lines && (line = read_line(fd)) != NULL) {
    //Got line so print it:
    //(Need to print newline at end, since no newline in line string.)
    printf("%s\n",line);
    lines_printed++; }

  //Check if loop reading lines termianted due to an error:
  if (errno)
    return EXIT_FAILURE;

  return EXIT_SUCCESS;
}



// Function to advance linenum lines in open file.
// Boolean return: true if advanced successfully, else false.
// (More efficient than repeatedly calling read_line() since
// don't bother storing lines.)
int seek_line(int fd, int linenum)
{
  int next, newline_cnt = 0;

  while (newline_cnt < linenum) {
    if ((next = read_char(fd)) == EOF)
      return 0;  //false
    if (next == '\n')
      newline_cnt++; }

  return 1;  //true
}



// Function to get next line from open file, returned as a C string.
// Dynamic memory is used for returned strings, and is reused for each line.
// Returns string (pointer to line buffer), or else NULL if error or immediate EOF.
// (Check errno upon NULL return to differentiate EOF vs. error.)
// Note that newline terminator is NOT included in returned line string.
char *read_line(int fd)
{
  static char *line_buff = NULL;  //Memory to hold line string, maintained between calls
  static int buff_size = 0;       //Size of line buffer, maintained as well
  char *temp_line_buff = NULL;
  int buff_pos, next_char;

  //Allocate initial memory for line_buff if needed:
  if (buff_size == 0) {
    if ((line_buff = malloc(INIT_BUFF_SIZE)) == NULL) {
      fprintf(stderr,"middle: error allocating memory with malloc: %s\n",strerror(errno));
      return NULL; }
    buff_size = INIT_BUFF_SIZE; }

  //Loop throughs chars until encounter EOL, EOF, or error,
  //placing chars into next position in line_buff:
  buff_pos = 0;
  while ((next_char = read_char(fd)) != '\n' && next_char != EOF) {
    //Must store at least two more chars (next_char + '\0'),
    //so check line_buff size and expand if necessary:
    if ((buff_size - buff_pos) < 2) {
      if ((temp_line_buff = realloc(line_buff,buff_size+INC_BUFF_SIZE)) == NULL) {
        fprintf(stderr,"middle: error allocating memory with realloc: %s\n",strerror(errno));
        return NULL; }
      buff_size = buff_size + INC_BUFF_SIZE; 
      line_buff = temp_line_buff; }
    //Store next_char:
    line_buff[buff_pos++] = next_char; }

  //Make sure that line_buff always contains a valid string:
  line_buff[buff_pos] = '\0';

  //Check if EOF encountered and determine appropriate return value:
  if (next_char == EOF && (buff_pos == 0 || errno))
    //Was at EOF already or there was an error:
    return NULL;
  else
    //Got a line (which might be blank) so return:
    return line_buff;
}



// Function to get next char/byte from open file descriptor and return it.
// Returns EOF on end-of-file or on error (check errno to differentiate).
// Basically equivalent to fgetc() but takes file descriptor argument.
// Note: buffers the file input, so is efficient reading individual chars.
int read_char(int fd)
{
  static char file_buff[FILE_BUFFER_SIZE];  //Buffer to hold file block reads
  static int buff_end = 0;                  //Number of good/read chars in buffer
  static int buff_pos = 0;                  //Next index to read from in buffer
  int nread;

  //Check if buffer needs to be filled and try to fill:
  //(Due to initial values, will call read() first time called.)
  if (buff_pos == buff_end) {
    if ((nread = read(fd,file_buff,FILE_BUFFER_SIZE)) <= 0) {
      //EOF/error, so done readine from file:
      if (nread < 0) fprintf(stderr,"middle: error reading from file: %s\n",strerror(errno));
      return EOF; }
    buff_end = nread;
    buff_pos = 0; }

  //Normal return of next char in buffer:
  return file_buff[buff_pos++];
}

// EOF
