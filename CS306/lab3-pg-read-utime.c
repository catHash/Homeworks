// Solution to Lab #3 in CS306 Spring Fall 2013
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
// -- Uses read() (syscall I/O) to read from /proc/<PID>/io;
// -- Puts all children into a separate process group;
// -- Terminates all children on error, via killpg();
// -- Collects killed children by calling wait() in a loop.
//
// Uses gettimeofday() for log entries times, providing microsecond resolution.


#include <unistd.h>
#include <stdlib.h>
#include <stdio.h>
#include <fcntl.h>
#include <string.h>
#include <ctype.h>
#include <errno.h>
#include <sys/wait.h>
#include <time.h>
#include <sys/time.h>

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
char *get_utime_string(void);


//Global variables:
//(Using global variable to pass children's PG to cleanup_and_exit(),
//to avoid having to add arguments to server().)
pid_t child_pgrp;


int main(int argc, char *argv[])
{
  char *log_pathname;
  int pfds[2], i;
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

  for (i=1; i<=NUMBER_CLIENTS; i++) {
    switch (cpid = fork()) {
    case -1:
      //fork error:
      perror("Main: fork of child process failed");
      cleanup_and_exit(EXIT_FAILURE);
    case 0:
      //In child: (set up process group IDs and call client())
      if (i == 1)
        setpgrp();    //Short for setpgid(0,0)
      else
        setpgid(0,child_pgrp);
      client(pfds);
      exit(EXIT_FAILURE);  //Should not get here!
    default:
      //In parent: (set up process group IDs)
      if (i == 1)
        child_pgrp = cpid;
      setpgid(cpid,child_pgrp);
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

  if ((fptr = fopen(filename,"w")) == NULL) {
    perror("Server: opening log file failed");
    cleanup_and_exit(EXIT_FAILURE);}

  //Close unused write end of pipe:
  close(pfds[1]);

  //Go into loop reading messages from clients:
  while ((message = readline(pfds[0])) != NULL) {
    //Get time string to preface on message:
    timestr = get_utime_string();
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
  char io[61];
  char message[100];

  //Close unused read end of inherited pipe:
  close(pfds[0]);

  //Get this process PID:
  pid_t pid = getpid();

  //Seed random() so random number sequence is different in each child:
  srandom(pid);

  //Open /proc/<pid>/io to read stats:
  snprintf(proc_path,20,"/proc/%d/io",pid);
  int procfd;

  if ((procfd = open(proc_path,O_RDONLY)) == -1) {
    fprintf(stderr,"client %d: failed to open proc file %s: %s\n",pid,proc_path,strerror(errno));
    exit(EXIT_FAILURE); }

   for (int i=1; i<=NUMBER_MESSAGES; i++) {
    //Read read/write stats from proc file:
    lseek(procfd,0,SEEK_SET);;  //Make sure read from start.
    if (read(procfd,io,60) <= 0)
      exit(EXIT_FAILURE);
    io[60]='\0';

    //Prepare client message and write to server via pipe:
    //(Break up io string into separate read/written strings first.)
    char *read_end=strchr(io,'\n');
    *read_end='\0';  //Makes start of io a separate string containing read chars info.
    char * written_start=read_end+1;
    char *written_end=strchr(written_start,'\n');
    *written_end='\0';  //Makes written chars part of io a separate string.
    snprintf(message,100,"%d: Message %d: read: %s  written: %s\n",pid,i,io+7,written_start+7);
    if (write(pfds[1],message,strlen(message)) < 0)
      exit(EXIT_FAILURE);

     //Sleep to simulate random run times:
    sleep(random() % MAXIMUM_SLEEP + 1); }

  close(procfd);

  exit(EXIT_SUCCESS);
}


//Function to be called from parent/server, to cleanup children
//and exit with proper exit status.  If called due to error in
//parent/server, will terminate all children too.
void cleanup_and_exit(int exit_status)
{
  int status;

  //See if need to terminate the children, and if so kill children process group:
  if (exit_status == EXIT_FAILURE)
    killpg(child_pgrp,SIGKILL);

  //Collect all children, and determine final exit status:
  //(Note that we must be able to collect an arbitrary number of children,
  //since this function may be called at any point during forking loop, with
  //less than full number of children having been created.  Simply using while
  //loop with wait to collect all children.)
  while (wait(&status) != -1)
    if (!(WIFEXITED(status) && WEXITSTATUS(status) == EXIT_SUCCESS))
      exit_status = EXIT_FAILURE;

  exit(exit_status);
}


char *get_utime_string(void)
{
  static char time_str[100];
  struct timeval tv;
  struct tm *tms;

  gettimeofday(&tv,NULL),
  tms = localtime(&(tv.tv_sec));
  snprintf(time_str,100,"%02d/%02d/%02d %02d:%02d:%02d:%02d",
	   tms->tm_mon+1,tms->tm_mday,tms->tm_year-100,tms->tm_hour,tms->tm_min,tms->tm_sec,(int)tv.tv_usec);

  return time_str;
}


//EOF
