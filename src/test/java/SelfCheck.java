import io.CacheMemories;
import obj.Memory;
import obj.UserInput;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.Test;
import processing.check.MemoryChecker;
import processing.check.RunnableMemoryChecker;
import processing.load.RunnableMemoryLoader;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SelfCheck
{
    private static final String[] ALL_EXTENSIONS = new String[]{
            "gif", "png", "bmp", "jpg", "jpeg", "heic", "mp4", "mov", "mp4", "mov", "avi", "vlc", "wmv", "raw", "cr2", "tiff", "tif",
            "GIF", "PNG", "BMP", "JPG", "JPEG", "HEIC", "MP4", "MOV", "MP4", "MOV", "AVI", "VLC", "WMV", "RAW", "CR2", "TIFF", "TIF"
    };

    static List<Memory> testMemories;

    @Test()
    private void testMemoriesLoad()
    {
        final UserInput uio = new UserInput();
        uio.setImported(false);
        uio.setStartingFolder(new File("src/test/java/data"));

        testMemories = RunnableMemoryLoader.loadMemories(uio);
        assert testMemories.size() == 9;
    }

    @Test(priority = 1, dependsOnMethods = "testMemoriesLoad")
    private void testMatchLogic()
    {
        MemoryChecker.checkForDuplicateMemories(testMemories, false);
        boolean result = true;
        for(final Memory temp: testMemories)
        {
            //if it isn't matched and it isn't from the 'should get matched' report it
            if(!temp.isMatched() && temp.getPath().contains("duplicate"))
            {
               System.out.println("ISSUE--" + temp.getName() + " was NOT matched!");
               result = false;
            }
            //if this is matched but is in the edited path, it should not have matched!
            else if(temp.isMatched() && temp.getPath().contains("edited"))
            {
                System.out.println("ISSUE--" + temp.getName() + " WAS matched and should not have been!");
                result = false;
            }
            else if(temp.isMatched() && temp.getPath().contains("duplicate"))
            {
                System.out.println("PASS--" + temp.getName() + " WAS matched correctly!");
            }
            else if(!temp.isMatched() && temp.getPath().contains("edited"))
            {
                System.out.println("PASS--" + temp.getName() + " WAS NOT matched correctly!");
            }
        }
        assert result;
    }

    @Test(priority = 2)
    private void checkCacheMemoriesMatched()
    {
        //get the current starting ones
        final UserInput uio = new UserInput();
        uio.setImported(false);
        uio.setStartingFolder(new File("src/test/java/data/orig"));
        testMemories = RunnableMemoryLoader.loadMemories(uio);
        //cache them
        CacheMemories.cacheCurrentMemoriesTest(testMemories);
        //read them back to validate cache works
        testMemories = new ArrayList<>(3);
        final Collection<File> files = FileUtils.listFiles(new File("src/test/java/data/orig"), ALL_EXTENSIONS, true);
        CacheMemories.readCurrentMemories(testMemories, files);

        //get the ones we are test importing
        final UserInput importUio = new UserInput();
        importUio.setStartingFolder(new File("src/test/java/data/duplicate"));
        importUio.setImported(true);
        final List<Memory> stagedMemories = RunnableMemoryLoader.gatherMemories(importUio);

        final ExecutorService pool = Executors.newFixedThreadPool(4);
        for (final Memory stageMemory : stagedMemories)
        {
            final Runnable runnableMemoryChecker = new RunnableMemoryChecker(stageMemory, new ArrayList<>(0), testMemories);
            pool.execute(runnableMemoryChecker);
        }
        try
        {
            pool.shutdown();
            //Wait for threads to all stop
            pool.awaitTermination(2, TimeUnit.MINUTES);
        } catch (final InterruptedException ie)
        {
            ie.printStackTrace();
        }

        boolean result = true;
        for(final Memory temp: stagedMemories)
        {
            if(!temp.isMatched())
            {
                System.out.println("ISSUE--" + temp.getName() + " was NOT matched!");
                result = false;
            }
            else
            {
                System.out.println("PASS--" + temp.getName() + " WAS matched correctly!");
            }
        }
        assert result;
    }

    @Test(priority = 3)
    private void checkCacheMemoriesNotMatched()
    {
        //get the current starting ones
        final UserInput uio = new UserInput();
        uio.setImported(false);
        uio.setStartingFolder(new File("src/test/java/data/orig"));
        testMemories = RunnableMemoryLoader.loadMemories(uio);
        //cache them
        CacheMemories.cacheCurrentMemoriesTest(testMemories);
        //read them back to validate cache works
        testMemories = new ArrayList<>(3);
        final Collection<File> files = FileUtils.listFiles(new File("src/test/java/data/orig"), ALL_EXTENSIONS, true);
        CacheMemories.readCurrentMemories(testMemories, files);

        //get the ones we are test importing
        final UserInput importUio = new UserInput();
        importUio.setStartingFolder(new File("src/test/java/data/edited"));
        importUio.setImported(true);
        final List<Memory> stagedMemories = RunnableMemoryLoader.gatherMemories(importUio);

        final ExecutorService pool = Executors.newFixedThreadPool(4);
        for (final Memory stageMemory : stagedMemories)
        {
            final Runnable runnableMemoryChecker = new RunnableMemoryChecker(stageMemory, new ArrayList<>(0), testMemories);
            pool.execute(runnableMemoryChecker);
        }
        try
        {
            pool.shutdown();
            //Wait for threads to all stop
            pool.awaitTermination(2, TimeUnit.MINUTES);
        } catch (final InterruptedException ie)
        {
            ie.printStackTrace();
        }

        boolean result = true;
        for(final Memory temp: stagedMemories)
        {
            if(temp.isMatched())
            {
                System.out.println("ISSUE--" + temp.getName() + " WAS matched and should not have been!");
                result = false;
            }
            else
            {
                System.out.println("PASS--" + temp.getName() + " WAS NOT matched correctly!");
            }
        }
        assert result;
    }
}