import io.Mover;
import obj.Memory;
import obj.UserInputObj;
import processing.RunnableMemoryChecker;
import processing.RunnableMemoryLoader;
import processing.Shared;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Issue when 2 files named same in staging are put in same pass/flagged folder
 */
class Import
{
    private static void mainProcess()
    {
        final long startTime = System.nanoTime();
        List<Memory> currentMemories = new ArrayList<>(0);

        //move all files from GDrive to staging folder
        Mover.moveToStaging();

        //Then gather up the staged files
        final UserInputObj importUio = new UserInputObj();
        importUio.setStartingFolder(new File("Z:" + File.separator + "Imports" + File.separator + "Stage"));
        importUio.setImported(true);
        final List<Memory> stagedMemories = RunnableMemoryLoader.gatherNewFiles(importUio);

        //If we have any staged files
        if (stagedMemories != null && stagedMemories.isEmpty())
        {
        /*make a thread runner to take 1 stage file, and compare to all of startingFiles
             if passes startingFiles test, see if any files are in the passed on z drive, add those into the check for this thread
            if STILL passes, then add the 1 stage file to the passed on z drive
         */

            //Get all the current files on ShareDrive
            final UserInputObj userInputObj = new UserInputObj();
            userInputObj.setImported(false);
            userInputObj.setStartingFolder(new File("Y:" + File.separator + "SharedFolder" + File.separator + "Pictures and Videos"));
            currentMemories = RunnableMemoryLoader.gatherCurrentFiles(userInputObj);

            //Gather up all in Pass that have not been added to ShareDrive yet
            final UserInputObj passedUio = new UserInputObj();
            passedUio.setStartingFolder(new File("Z:" + File.separator + "Imports" + File.separator + "Pass"));
            passedUio.setImported(false);
            final List<Memory> passedFiles = RunnableMemoryLoader.gatherCurrentFiles(passedUio);
            //add in to are shared ones since these previously passed validations
            currentMemories.addAll(passedFiles);

            final ExecutorService pool = Executors.newFixedThreadPool(3);
            for (final Memory stageMemory : stagedMemories)
            {
                final Runnable runnableMemoryChecker = new RunnableMemoryChecker(stageMemory, stagedMemories, currentMemories);
                pool.execute(runnableMemoryChecker);
            }
            try
            {
                pool.shutdown();
                //Wait for threads to all stop
                pool.awaitTermination(1, TimeUnit.DAYS);

            } catch (final InterruptedException ie)
            {
                ie.printStackTrace();
            }

            //Move memories where they should go
            for (final Memory currentMemory : stagedMemories)
            {
                if (currentMemory.isMatched())
                {
                    Mover.moveImportFileMatched(currentMemory);
                }
                else
                {
                    Mover.movePassed(currentMemory);
                }
            }
        }

        final long endTime = System.nanoTime();
        final long totalTime = endTime - startTime;
        System.out.println("Total  check  between " + currentMemories.size() + " starting memories and " + Objects.requireNonNull(stagedMemories).size() + " staged memories took: " + Shared.printTotalTimeTaken(totalTime));
        currentMemories.clear();
    }

    public static void main(final String[] args)
    {
        mainProcess();
    }
}