package pl.pydyniak;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by rafal on 5/26/16.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter path: ");
        String path = br.readLine();
        File directoryFile = new File(path);
        if (!directoryFile.exists() || !directoryFile.canRead()) {
            throw new IllegalArgumentException("The catalog doesn't exist or can't be read due to lack of permissions");
        }
        ImagesRename imagesRename = new ImagesRename();
        imagesRename.renameImagesToCreationDate(path);
    }
}
