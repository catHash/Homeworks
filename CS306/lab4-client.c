#include <arpa/inet.h>
#include <sys/socket.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>

#define REMPS_SERVER_PORT 2343
#define MAX_BUFFER_SIZE 1024

int main(int argc, char *argv[])
{
	if (argc < 2){
		printf("lab4-client: syntax: ./lab4-client 127.0.0.1\n");
		return 0;
	}
	int connection_fd,chars_in,index = 0,limit = MAX_BUFFER_SIZE;
	char msgbuffer[MAX_BUFFER_SIZE+1];
	struct sockaddr_in servaddr;
	connection_fd = socket(AF_INET, SOCK_STREAM, 0);
	memset(&servaddr, 0, sizeof(servaddr));
	servaddr.sin_family = AF_INET;
	servaddr.sin_port = htons(REMPS_SERVER_PORT);
	servaddr.sin_addr.s_addr = inet_addr(argv[1]);
	
	connect(connection_fd,
		(struct sockaddr *)&servaddr, sizeof(servaddr));
		
	if (connection_fd >= 0){
		while((chars_in=read(connection_fd,&msgbuffer[index], limit))
		> 0){
			index += chars_in;
			limit -= chars_in;
			printf("\n%s\n",msgbuffer);
		}
		msgbuffer[index] = 0;
		printf("\n%s\n",msgbuffer);
		int uid = getuid();
		printf("my UID: %d\n",uid);
		close(connection_fd);
	}
	return 0;
}
