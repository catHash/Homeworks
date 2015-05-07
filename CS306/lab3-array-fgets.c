// Solution to Lab #3 in CS306 Fall 2013
// Author: Norman Carver
//
// A simple logging client-server program, parent/server reads log messages
// via a pipe from children/clients, and writes the messages to a log file.
// Chilren/clients messages are bytes read/written info from /proc/<PID>/io.
//
// Compile as executable named lab3
// Usage: lab3 LOG_PATHNAME
//
// This version:
// -- Uses fgets() (library I/O) to read from /proc/<PID>/io;
// -- Keeps track of the PIDs of the children in an array;
// -- Terminates each child one-by-one on error, calling waitpid().


#include <unistd.h>
#include <stdlib.h>
#include <stdio.h>
#include <fcntl.h>
#include <string.h>
#include <ctype.h>
#include <errno.h>
#include <sys/wait.h>
#include <time.h>

// Includes readline function to read lines from pipe:
// (Assuming version with dynamic memory for each line.)
#include "readline.c"


#define NUMBER_CLIENTS 5
#define NUMBER_MESSAGES 5
#define MAXIMUM_SLEEP 10


//Prototypes for functions:
void server(int pfds[], char *filename);
void client(int pfds[]);
void cleanup_and_exit(int exit_status);


//Global variables:
//(Using global variable to pass children PIDs to cleanup_and_exit(),
//to avoid having to add arguments to server().  static is to limit scope.)
static pid_t children_pids[NUMBER_CLIENTS];
static int num_children = 0;  //So know number of children actually forked


int main(int argc, char *argv[])
{
  char *log_pathname;
  int pfds[2];
  pid_t cpid;

  //Check for proper number of arguments:
  if (argc != 2) {
    fprintf(stderr,"Usage: lab3 LOG_PATHNAME\n");
    return EXIT_FAILURE; }

  //Get and store log file pathname from arguments:
  log_pathname = argv[1];

  //Create pipe prior to fork:
  if (pipe(pfds) == -1) {
    perror("Main: Could not create pipe");
    exit(EXIT_FAILURE); }

  for (int child=0; child<NUMBER_CLIENTS; child++) {
    switch (cpid = fork()) {
    case -1:
      //fork error:
      perror("Main: fork of child process failed");
      cleanup_and_exit(EXIT_FAILURE);
    case 0:
      //In child: (call client())
      client(pfds);
      exit(EXIT_FAILURE);  //Should not get here!
    default:
      //In parent:
      //(save child PID and keep track of number of children actually forked)
      children_pids[num_children++] = cpid;
    }
  }

  //In parent after all children created, so call server():
  server(pfds,log_pathname);

  //Should not get here!
  exit(EXIT_FAILURE);
}


//Function to be run in parent/server process.
//Reads messages (separate lines) in pipe, prefixes time,
//then prints to desired log file.
void server(int pfds[], char *filename)
{
  char *message;
  char *timestr;
  FILE *fptr;
  time_t ticks;

  if ((fptr = fopen(filename,"w")) == NULL) {
    perror("Server: opening log file failed");
    cleanup_and_exit(EXIT_FAILURE);}

  //Close unused write end of pipe:
  close(pfds[1]);

  //Go into loop reading messages from clients:
  while ((message = readline(pfds[0])) != NULL) {
    //Get time string to preface on message:
    time(&ticks);
    timestr = ctime(&ticks);
    //Remove newline in time string:
    timestr[24] = '\0';
    //Write out message to log file:
    #ifdef DEBUG
      printf("%s: %s\n",timestr,message);
    #endif
    fprintf(fptr,"%s: %s\n",timestr,message);
    fflush(fptr); 
    free(message);  //Cleanup line memory returned by readline().
  }

  cleanup_and_exit(EXIT_SUCCESS);
}


//Function to be run in each child/client process.
//Write a certain number of log messages into pipe for server,
//waiting a random number of seconds between each message.
void client(int pfds[])
{
  char proc_path[20];
  char message[120];
  char read_info[31], write_info[31];

  //Close unused read end of inherited pipe:
  close(pfds[0]);

  //Get this process PID:
  pid_t pid = getpid();

  //Seed random() so random number sequence is different in each child:
  srandom(pid);

  //Open /proc/<pid>/io to read stats:
  snprintf(proc_path,20,"/proc/%d/io",pid);
  FILE * procfp;

  if ((procfp = fopen(proc_path,"r")) == NULL) {
    fprintf(stderr,"client %d: failed to open proc file %s: %s\n",pid,proc_path,strerror(errno));
    exit(EXIT_FAILURE); }

  //Turn buffering off for proc file, so really re-read file contents when rewind():
  setvbuf(procfp, NULL, _IONBF, 0);

  for (int i=1; i<=NUMBER_MESSAGES; i++) {
    //Read read/write stats from proc file:
    rewind(procfp);  //Make sure read from start.

    if (fgets(read_info,31,procfp) == NULL || fgets(write_info,31,procfp) == NULL) {
      fprintf(stderr,"client %d: fgets() failed: %s\n",pid,strerror(errno));
      exit(EXIT_FAILURE); }

    //Remove newlines from read and written strings:
    read_info[strlen(read_info)-1]='\0';
    write_info[strlen(write_info)-1]='\0';

    #ifdef DEBUG
    printf("fgets rchar: %s    wchar: %s\n",read_info+7,write_info+7);
    #endif

    //Prepare client message and write to server via pipe:
    //(Using pointer arithmetic +7 to get substring w/o text from read/written
    //and using atoi() to convert to integers and stops at newlines so need not remove.)
    snprintf(message,120,"%d: Message %d: read: %s  written: %s\n",pid,i,read_info+7,write_info+7);
    if (write(pfds[1],message,strlen(message)) < 0)
      exit(EXIT_FAILURE); 

    //Sleep to simulate random run times:
    sleep(random() % MAXIMUM_SLEEP + 1); }

  fclose(procfp);

  exit(EXIT_SUCCESS);
}


//Function to be called from parent/server, to cleanup children
//and exit with proper exit status.  If called due to error in
//parent/server, will terminate all children too.
void cleanup_and_exit(int exit_status)
{
  int status;

  //See if need to terminate the children, and if so kill each child:
  if (exit_status == EXIT_FAILURE)
    for (int child=0; child<NUMBER_CLIENTS; child++)
      kill(children_pids[child],SIGTERM);

  //Collect all children, and determine final exit status:
  //(Note that we must be able to collect an arbitrary number of children,
  //since this function may be called at any point during fork loop, with
  //less than full number of children having been created.  Using global
  //variable num_children to pass this information.
  //Here we are specifically collecting each known child by PID.)
  for (int child=0; child<num_children; child++) {
    waitpid(children_pids[child],&status,0);
    if (!WIFEXITED(status) || WEXITSTATUS(status) != EXIT_SUCCESS)
      exit_status = EXIT_FAILURE;
  }

  exit(exit_status);
}


//EOF
