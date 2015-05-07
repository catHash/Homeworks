#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <errno.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/wait.h>
#include <time.h>
#include "readline.c"		

#define NUMBER_CLIENTS 5
#define MAXIMUM_SLEEP 5
#define NUMBER_MESSAGES 5
#define READ_BUFFER_SIZE 4096

void server(int pfds[], char* filename);
void client(int pfds[]);

/*
	argv[1] is the log file pathname
*/
int main(int argc, char* argv[]){
	pid_t ppid;
	int pipefd[2] = {};
	pipe(pipefd);

	for(int i = 0; i < NUMBER_CLIENTS; i++){
		ppid = fork();
		if (ppid == -1) {
			perror("fork");
			exit(EXIT_FAILURE);
		}else if (ppid == 0){
			client(pipefd);
			//exit(EXIT_FAILURE);
		}
	}
	server(pipefd,argv[1]);	
}		
/*
	server should log "<time>: <child message>"
*/
void server(int pfds[], char* filename){
	close(pfds[1]); 

	FILE* out;	
	char* mode = "w";		
	char* buf;
	time_t mytime;

	out = fopen(filename,mode);
	int out_fd = fileno(out);

	while ((buf = readline(pfds[0])) > 0){
		time(&mytime);
		write(out_fd, ctime(&mytime), 20);	
		write(out_fd, buf, 30);
		write(out_fd, "\n", 1);
	}
	close(pfds[0]);
	fclose(out);
	wait(NULL);                /* Wait for child */
	_exit(EXIT_SUCCESS);
	return;
}
/*
	client should write "<pid>: Message #<i>: <status>"
	//char* procpath = malloc(32);
	//sprintf(procpath,"/proc/%d/io",cpid);	
*/
void client(int pfds[]){
	close(pfds[0]);  

	pid_t cpid;
	char* msg;
	int proc_fd;
	FILE* proc_in;
	char* proc_path = malloc(20);
	char* mode = "r";
	char* proc_read;
	char* proc_written;		
	
	cpid = getpid();
	srand(cpid);
	sprintf(proc_path,"/proc/%i/io",cpid);
	proc_in = fopen(proc_path,mode);	
	proc_fd = fileno(proc_in);
	proc_read = readline(proc_fd);
	//proc_written = readline(proc_fd);
	for(int j=1;j<(NUMBER_MESSAGES+1);j++){
		msg = malloc(128);
		sprintf(msg,"%i: Message #%i: proc fd:%i\n",cpid,j,proc_fd);
		write(pfds[1],msg,strlen(msg));
		//if (j == 1)
		//	write(pfds[1],proc_path,strlen(proc_path));
		sleep(rand()%MAXIMUM_SLEEP);
		free(msg);
	}
	close(pfds[1]);       
	exit(EXIT_SUCCESS);
	return;
}
