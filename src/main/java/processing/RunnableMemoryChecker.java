package processing;

import io.Reporter;
import obj.Memory;

import java.util.ArrayList;
import java.util.List;

public class RunnableMemoryChecker implements Runnable
{
    final private Memory newMemory;
    final private List<Memory> currentMemories;
    final private List<Memory> stagedMemories;

    public RunnableMemoryChecker(final Memory newMemory, final List<Memory> stagedMemories, final List<Memory> currentMemories)
    {
        this.newMemory = newMemory;
        this.stagedMemories = stagedMemories;
        this.currentMemories = currentMemories;
    }

    public void run()
    {
        try
        {
            final long startTime = System.nanoTime();
            final ArrayList<Memory> matches = new ArrayList<>();

            //if we have to fully scan the pic, save it so we don't do it each time
            int[] tempByte = null;

            //check that none of the new files match what we already have
            //this makes sure someone doesn't add a file that is already stored
            for (final Memory currentMemory : currentMemories)
            {
                if (currentMemory.isMatched() || newMemory.equals(currentMemory))
                {
                    continue;
                }
                try
                {
                    if (DetermineMatch.isProbablePictureMatch(newMemory, currentMemory))
                    {
                        if (tempByte == null && newMemory.getMetadata() == null)
                        {
                            tempByte = Shared.returnPixelVal(newMemory.getFile());
                        }

                        if (newMemory.getMetadata() == null && currentMemory.getMetadata() == null && DetermineMatch.isDuplicatePictureMatch(tempByte, currentMemory.getFile()))
                        {
                            DetermineMatch.setMatchedItems(matches, newMemory, currentMemory);
                        }
                        if (newMemory.getMetadata() != null && currentMemory.getMetadata() != null && DetermineMatch.isDuplicatePictureMatchRAW(newMemory, currentMemory))
                        {
                            DetermineMatch.setMatchedItems(matches, newMemory, currentMemory);
                        }
                    }

                    if (DetermineMatch.isPossibleVideoMatch(newMemory, currentMemory) &&
                            DetermineMatch.isDuplicateVideo(newMemory.getFile().toPath(), currentMemory.getFile().toPath()))
                    {
                        DetermineMatch.setMatchedItems(matches, newMemory, currentMemory);
                    }
                } catch (final Exception e)
                {
                    e.printStackTrace();
                }
            }

            //check that none of the new files match what is coming in the single staged.
            //this makes sure someone doesn't add the same picture twice during import
            if (matches.isEmpty())
            {
                for (final Memory stagedMemory : stagedMemories)
                {
                    if (stagedMemory.isMatched() || newMemory.equals(stagedMemory))
                    {
                        continue;
                    }

                    try
                    {
                        if (DetermineMatch.isProbablePictureMatch(newMemory, stagedMemory))
                        {
                            if (tempByte == null && newMemory.getMetadata() == null)
                            {
                                tempByte = Shared.returnPixelVal(newMemory.getFile());
                            }

                            if (newMemory.getMetadata() == null && stagedMemory.getMetadata() == null && DetermineMatch.isDuplicatePictureMatch(tempByte, stagedMemory.getFile()))
                            {
                                DetermineMatch.setMatchedItems(matches, stagedMemory, stagedMemory);
                            }
                            if (newMemory.getMetadata() != null && stagedMemory.getMetadata() != null && DetermineMatch.isDuplicatePictureMatchRAW(newMemory, stagedMemory))
                            {
                                DetermineMatch.setMatchedItems(matches, stagedMemory, stagedMemory);
                            }
                        }

                        if (DetermineMatch.isPossibleVideoMatch(newMemory, stagedMemory) &&
                                DetermineMatch.isDuplicateVideo(newMemory.getFile().toPath(), stagedMemory.getFile().toPath()))
                        {
                            DetermineMatch.setMatchedItems(matches, stagedMemory, stagedMemory);
                        }
                    } catch (final Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            if (!matches.isEmpty())
            {
                Reporter.reportDuplicates(newMemory.getName(), matches);
            }

            final long endTime = System.nanoTime();
            final long totalTime = endTime - startTime;
            System.out.println("Total  check  for file " + newMemory.getName() + " took: " + Shared.printTotalTimeTaken(totalTime));
        } catch (final Exception e)
        {
            e.printStackTrace();
        }
    }
}