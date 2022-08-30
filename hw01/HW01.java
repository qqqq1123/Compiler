package hw01;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/*
 * JAVA 버전: JAVA 11
 * gcc 12.0.5
 * 사용한 운영체제: macOS Big Sur v11.2.3 
 * */

public class HW01 {

	public HW01() {
	}

	public String transFile(String line) throws IOException {
		StringBuilder sentence = new StringBuilder();
		sentence.setLength(0); //StringBuilder 초기화
		line = line.substring(1); //입력받은 명령어에서 '(' 제거한 문자열 저장

		char c = line.charAt(0);
		String tmp = "";

		switch (c) { //각 명령어 구분 

		case 'e': // echo -> printf 
			sentence.append("\tprintf(\"");
			tmp = line.substring(6, line.length() - 2); //"아무 내용" 부분에 해당 
			tmp = tmp + "\\";
			tmp = tmp + "n";
			sentence.append(tmp);
			sentence.append("\");\n");
			break;
		case 'l': // list_dir -> system("ls -al")
			sentence.append("\tsystem(\"ls -al\");\n");
			break;
		case 'd': // del -> rm ./파일명
			sentence.append("\tsystem(\"rm ./");
			tmp = line.substring(5); //파일 이름에 해당
			sentence.append(tmp);
			sentence.append(";\n");
			break;
		case 'm': // mov -> popen
			/* popen()을 통해 파이프스트림을 생성하고, ls -al를 실행시켜 결과를 받아와 이를 파일에 출력 */
			tmp = line.substring(14, line.length() - 2);

			sentence.append("\tchar line[4096];\n"); // ls -al 결과 저장할 배열 
			sentence.append("\tFILE *fp0;\n"); //popen용 포인터
			sentence.append("\tFILE *fp = fopen(\""); //출력 파일용 포인터
			sentence.append(tmp); //파일 이름 
			sentence.append("\",\"w\");\n");
			
			/* popen 및 ls -al 실행 */
			sentence.append("\tif((fp0 = popen(\"ls -al\", \"r\")) == NULL) {\n"); 
			sentence.append("\t\treturn 1;\n");
			sentence.append("\t}\n");
			
			/* ls -al 결과 내용을 출력 파일에 fprintf를 통해 씀 */
			sentence.append("\twhile(fgets(line, 4096, fp0) != NULL) {\n");
			sentence.append("\t\tfprintf(fp, line);\n");
			sentence.append("\t}\n");

			sentence.append("\tpclose(fp0);\n");
			sentence.append("\tfclose(fp);\n");

			break;
		case 's': // show -> system("cat 파일명")
			sentence.append("\tsystem(\"cat ");
			tmp = line.substring(6, line.length() - 2); //파일 이름 
			sentence.append(tmp);
			sentence.append("\");\n");
			break;

		}
		return sentence.toString(); //문자열로 return 

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HW01 hw01 = new HW01();
		StringBuilder sb = new StringBuilder();
		String strF = "#include <stdio.h>\n#include <stdlib.h>\nint main() {\n\n";
		String strL = "\treturn 1;\n}";

		try {
			File file = new File("/Users/mini/Desktop/test.hf"); // 대상 파일 (읽기 전용)
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);

			String line = "";

			String tmp = "";

			while ((line = br.readLine()) != null) { //한줄씩 읽어 #을 기준으로 명령어들을 연결 
				tmp += line;
				tmp += "#";

			}

			/* 각 명령어들을 #(토큰)을 기준으로 분리 */
			StringTokenizer st = new StringTokenizer(tmp, "#");
			String[] strArr = new String[st.countTokens()];

			int i = 0;
			while (st.hasMoreElements()) {
				strArr[i++] = st.nextToken();
			}

			sb.append(strF);
			
			/* hf의 명령어를 한줄씩 c언어로 변환 */
			for (i = 0; i < strArr.length; i++) {
//				System.out.println(strArr[i]);
				sb.append(hw01.transFile(strArr[i]));

			}
			sb.append(strL);
			br.close();

		} catch (Exception e) {
			e.getStackTrace();
		}

		try {
			/* 변환 내용 test.c에 출력 */
			File fileW = new File("/Users/mini/Desktop/test.c"); // 대상 파일 (쓰기 전용)
			BufferedWriter bw = new BufferedWriter(new FileWriter(fileW));

			bw.write(sb.toString());
			bw.flush();
			bw.close();
		} catch (Exception e) {
			e.getStackTrace();
		}

	}

}
