#include <stdio.h>
#include <stdlib.h>
#include <string.h>

void delete_chars(char** argv);
void translate_chars(char** argv);
int check_for_delete(char** argv,char c);
int check_for_translate(char** argv,char c);
char** interpret_set(const char** input_set);

int main(int argc, char* argv[])
{
	if(argv[1][0] == '-' && argv[1][1] == 'd'){ 
		if (argc > 2){
			delete_chars(argv);
		}else{
			fputs("mytr: -d: correct syntax is: ",stdout);
			fputs("mytr -d [characters to delete]\n",stdout);
		}
	}
	else if(strlen(argv[1]) <= strlen(argv[2]))
		translate_chars(argv);			
	return EXIT_SUCCESS;
}
void translate_chars(char** argv)
{
	char next;
	int index;
	while(!feof(stdin)){
		next = fgetc(stdin);
		index = check_for_translate(argv,next);
		if (!(index == 0)){
			fputc(argv[2][index],stdout);
		}else{
			fputc(next,stdout);
		}
	}
}
void delete_chars(char** argv)
{
	char next;
	int index;
	while(!feof(stdin)){
		next = fgetc(stdin);
		index = check_for_translate(argv,next);
		if (check_for_delete(argv,next) == 0 )
			fputc(next,stdout);
	}
}
int check_for_delete(char** argv,char c){
	for(int j = 0; j < strlen(argv[2]); j++){
		if (c  == argv[2][j]) {
			return 1;
		}
	}	
	return 0;
}
int check_for_translate(char** argv,char c){
	for(int i = 0; i < strlen(argv[1]); i++){
		if (c == argv[1][i]) {
			return i;
		}
	}	
	return 0;
}
char** interpret_set(const char** input_set){
	char** output_set = malloc(strlen(*input_set)+1);
	int out_set_index = 0;
	for(int i = 0; i < strlen(*input_set); i++){
		if (*input_set[i] == '\\') {
			switch(*input_set[++i]){
				case '\\':
					*output_set[out_set_index] = '\\';
					break;
				case 'n':
					*output_set[out_set_index] = '\n';
					break;						
				case 't':
					*output_set[out_set_index] = '\t';
					break;
				case 'r':
					*output_set[out_set_index] = '\r';
					break;
				default:
					break;
			}
			out_set_index ++;
		}
		else {
			*output_set[out_set_index] = *input_set[i];
			out_set_index ++;
		}
	}
	return output_set;
}
