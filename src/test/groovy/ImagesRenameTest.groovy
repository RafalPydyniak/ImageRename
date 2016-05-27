import pl.pydyniak.ImagesRename
import org.apache.commons.io.FileUtils
import org.junit.Assert
import org.junit.Test

/**
 * Created by rafal on 5/24/16.
 */
class ImagesRenameTest {

    private final String RESOURCES_TEST_FILES_PATH = "/home/rafal/Projects/images-rename/src/test/resources/test-files"

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfDirectoryDoesntExist() {
        ImagesRename imagesRename = new ImagesRename()
        imagesRename.renameImagesToCreationDate("non-existing-dir")
    }


    @Test (expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionForWrongTimeZone() {
        ImagesRename imagesRename = new ImagesRename()
        imagesRename.renameImagesToCreationDate(RESOURCES_TEST_FILES_PATH, "wrong")

    }

    @Test (expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionForWrongNameFormat() {
        ImagesRename imagesRename = new ImagesRename()
        imagesRename.renameImagesToCreationDate(RESOURCES_TEST_FILES_PATH, "Europe/Warsaw" , "wrong format")
    }

    @Test
    public void shouldRenameFilesCorrectly() {
        File testDirectory = prepareTestDirectory()

        ImagesRename imagesRename = new ImagesRename()
        imagesRename.renameImagesToCreationDate(testDirectory.getAbsolutePath())

        def filesNames = testDirectory.listFiles().collect {
            it.name
        }
        cleanTestDirectory(testDirectory)

        Assert.assertEquals(true, filesNames.contains("20160510093956.jpg"))
        Assert.assertEquals(true, filesNames.contains("20160510173208.JPG"))
        Assert.assertEquals(true, filesNames.contains("someFile3.txt"))
    }

    private void cleanTestDirectory(testDirectory) {
        FileUtils.deleteDirectory(testDirectory)
    }

    private File prepareTestDirectory() {
        File testFilesDirectory = new File(RESOURCES_TEST_FILES_PATH)
        File newTestFilesDirectory = new File(RESOURCES_TEST_FILES_PATH + "-temp");
        newTestFilesDirectory.mkdir()
        FileUtils.copyDirectory(testFilesDirectory, newTestFilesDirectory)
        newTestFilesDirectory
    }

    @Test
    public void shouldWorkProperlyForDifferentTimeZones() {
        File testDirectory = prepareTestDirectory()

        ImagesRename imagesRename = new ImagesRename()
        imagesRename.renameImagesToCreationDate(testDirectory.getAbsolutePath(), "Europe/Minsk")

        def filesNames = testDirectory.listFiles().collect {
            it.name
        }
        cleanTestDirectory(testDirectory)
        Assert.assertEquals(true, filesNames.contains("20160510083956.jpg"))
        Assert.assertEquals(true, filesNames.contains("20160510163208.JPG"))
        Assert.assertEquals(true, filesNames.contains("someFile3.txt"))
    }

    @Test
    public void shouldRenameImagesWithSameTimeStampCorrectly() {
        File testDirectory = prepareTestDirectoryWithMultipleFilesWithSameTimeStamp()
        ImagesRename imagesRename = new ImagesRename()
        imagesRename.renameImagesToCreationDate(testDirectory.getAbsolutePath())

        def filesNames = testDirectory.listFiles().collect {
            it.name
        }
        cleanTestDirectory(testDirectory)
        Assert.assertEquals(true, filesNames.contains("20160510093956.jpg"))
        Assert.assertEquals(true, filesNames.contains("20160510093956-1.jpg"))
        Assert.assertEquals(true, filesNames.contains("20160510093956-2.jpg"))
    }

    File prepareTestDirectoryWithMultipleFilesWithSameTimeStamp() {
        def testDirectory = prepareTestDirectory()
        FileUtils.copyFile(new File(RESOURCES_TEST_FILES_PATH+"/file1.jpg"), new File(testDirectory.getAbsolutePath()+"/file11.jpg"))
        FileUtils.copyFile(new File(RESOURCES_TEST_FILES_PATH+"/file1.jpg"), new File(testDirectory.getAbsolutePath()+"/file1222.jpg"))
        testDirectory
    }

    @Test
    public void shouldNotRenameFileAgainIfItIsAlreadyNamedCorrectly() {
        File testDirectory = prepareTestDirectory()

        ImagesRename imagesRename = new ImagesRename()
        imagesRename.renameImagesToCreationDate(testDirectory.getAbsolutePath())
        imagesRename.renameImagesToCreationDate(testDirectory.getAbsolutePath())

        def filesNames = testDirectory.listFiles().collect {
            it.name
        }
        cleanTestDirectory(testDirectory)

        println filesNames
        Assert.assertEquals(true, filesNames.contains("20160510093956.jpg"))
        Assert.assertEquals(true, filesNames.contains("20160510173208.JPG"))
        Assert.assertEquals(true, filesNames.contains("someFile3.txt"))
    }
}
