#include <stdio.h>
#include <stdlib.h>
int main() {

	printf("Hello world!\n");
	system("ls -al");
	printf("delete test.txt\n");
	system("cat test.txt");
	char line[4096];
	FILE *fp0;
	FILE *fp = fopen("res","w");
	if((fp0 = popen("ls -al", "r")) == NULL) {
		return 1;
	}
	while(fgets(line, 4096, fp0) != NULL) {
		fprintf(fp, line);
	}
	pclose(fp0);
	fclose(fp);
	printf("*********test.txt********\n");
	system("rm ./test.txt");
	return 1;
}