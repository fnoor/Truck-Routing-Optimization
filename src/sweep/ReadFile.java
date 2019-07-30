package sweep;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class ReadFile {
    private String file;

    ReadFile(String filePath) {
        file = filePath;
    }

    public ArrayList<String> openFile() throws IOException {
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        ArrayList<String> result = new ArrayList<>();
        String input;
        while ((input = br.readLine()) != null) {
            result.add(input);
        }
        br.close();
        return result;
    }
}