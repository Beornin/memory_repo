import io.CacheMemories;
import obj.Memory;
import obj.UserInputObj;
import processing.MemoryChecker;
import processing.RunnableMemoryLoader;
import processing.Shared;

import java.io.File;
import java.util.List;

/**
 * This class was in the old memory_sorter1, and helps establish a repo of memories that contains no duplicates.
 */
class RepoCheck
{
    public static void main(final String[] args)
    {
        final long startTime = System.nanoTime();

        //Get all the current files on repo
        final UserInputObj userInputObj = new UserInputObj();
        userInputObj.setStartingFolder(new File("Y:" + File.separator + "SharedFolder" + File.separator + "Pictures and Videos"));

        System.out.println("Getting current memories...");
        final List<Memory> currentMemories = RunnableMemoryLoader.gatherCurrentFiles(userInputObj, false);

        System.out.println("Checking current memories...");
        MemoryChecker.checkForDuplicateMemories(currentMemories);

        CacheMemories.cacheCurrentMemories(currentMemories);

        final long endTime = System.nanoTime();
        final long totalTime = endTime - startTime;
        System.out.println("Total  process time  for  " + currentMemories.size() + " memories took: " + Shared.printTotalTimeTaken(totalTime));
    }
}
