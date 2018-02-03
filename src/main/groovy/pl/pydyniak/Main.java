package pl.pydyniak;

import java.io.File;

/**
 * Created by rafal on 5/26/16.
 */
public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("Podaj sciezke w parametrze");
        }
        String path = args[0];
        System.out.println(path);
        File directoryFile = new File(path);
        if (!directoryFile.exists() || !directoryFile.canRead()) {
            throw new IllegalArgumentException("Katalog nie istnieje lub brak uprawnien do odczytu");
        }
        ImagesRename imagesRename = new ImagesRename();
        imagesRename.renameImagesToCreationDate(path);
    }
}
