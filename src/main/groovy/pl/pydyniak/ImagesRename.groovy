package pl.pydyniak

import com.drew.imaging.ImageMetadataReader
import com.drew.imaging.ImageProcessingException
import com.drew.metadata.Metadata
import com.drew.metadata.exif.ExifSubIFDDirectory
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils

import java.text.SimpleDateFormat

/**
 * Created by rafal on 5/24/16.
 */
class ImagesRename {

    public void renameImagesToCreationDate(String directoryPath, String timezone = "Europe/Warsaw", String nameFormat = "yyyyMMddHHmmss") {
        File directoryFile = new File(directoryPath)

        if (!directoryFile.exists())
            throw new IllegalArgumentException()

        if (!isTimezoneCorrect(timezone))
            throw new IllegalArgumentException()

        directoryFile.listFiles().each { file ->
            tryToRenameImage(file, nameFormat, timezone)
        }
    }

    private void tryToRenameImage(File file, nameFormat, timezone) {
        try {
            if (!isAlreadyNamedCorrectly(file, nameFormat, timezone))
                renameImage(file, nameFormat, timezone)
        } catch (ImageProcessingException ex) {
            //do nothing
        }
    }

    private boolean isAlreadyNamedCorrectly(file, nameFormat, timezone) {
        def name = parseCreationDate(getCreationDate(file, timezone), nameFormat)
        return file.getName().startsWith(name)
    }

    private def parseCreationDate(Date date, namePattern) {
        new SimpleDateFormat(namePattern).format(date);
    }

    private def getCreationDate(File file, String timezone) {
        Metadata metadata = ImageMetadataReader.readMetadata(file)
        def type = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class)
        def creationDate = type.getDateOriginal(TimeZone.getTimeZone(timezone))
        creationDate
    }

    private void renameImage(file, nameFormat, timezone) {
        def creationDate = getCreationDate(file, timezone)
        def newName = getNewName(creationDate, file, nameFormat)
        def newFile = new File(file.getParent(), newName)
        if (newFile.exists())
            newFile = addUniqueIdToImageName(newFile)

        FileUtils.moveFile(file, newFile)
    }

    private String getNewName(Date creationDate, File file, nameFormat) {
        def fileExtension = getFileExtension(file)
        def fileName = parseCreationDate(creationDate, nameFormat)
        def finalName = new StringBuilder()
        finalName.append(fileName)
        finalName.append(".")
        finalName.append(fileExtension)
        finalName.toString()
    }

    private File addUniqueIdToImageName(File newFile) {
        def name = getFileNameWithoutExtension(newFile)
        def fileExtension = getFileExtension(newFile)
        def id = 1
        while (newFile.exists()) {
            newFile = new File(newFile.getParent(), buildNameWithUniqueId(name, id, fileExtension))
            id++
        }
        newFile
    }

    private String getFileNameWithoutExtension(File file) {
        file.getName().substring(0, file.getName().indexOf(getFileExtension(file)) - 1);
    }

    private String getFileExtension(File file) {
        FilenameUtils.getExtension(file.getAbsolutePath())
    }

    private String buildNameWithUniqueId(String name, int id, String fileExtension) {
        def newName = new StringBuilder()
        newName.append(name)
        newName.append("-")
        newName.append(id)
        newName.append(".")
        newName.append(fileExtension)
        newName.toString()
    }

    private boolean isTimezoneCorrect(String timezone) {
        TimeZone.getAvailableIDs().contains(timezone)
    }
}
