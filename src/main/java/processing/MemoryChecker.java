package processing;

import io.Reporter;
import obj.Memory;

import java.util.ArrayList;
import java.util.List;

public class MemoryChecker
{
    public static void checkForDuplicateMemories(final List<Memory> currentMemories)
    {
        try
        {
            final long startTime = System.nanoTime();
            final ArrayList<Memory> matches = new ArrayList<>();
            int counter = 0;
            //if we have to fully scan the pic, save it so we don't do it each time
            byte[] tempByte = null;
            Memory innerMemory;
            for (final Memory checkMemory : currentMemories)
            {
                counter++;

                if (checkMemory.isMatched())
                {
                    continue;
                }
                matches.clear();
                for (int iCount = counter + 1; iCount < currentMemories.size(); iCount++)
                {
                    innerMemory = currentMemories.get(iCount);

                    //if the memory  is already matched or is the same memory, skip it
                    if (innerMemory.isMatched() || checkMemory.equals(innerMemory))
                    {
                        continue;
                    }
                    try
                    {
                        if (tempByte == null && checkMemory.getMetadata() == null)
                        {
                            tempByte = Shared.returnPixelVal(checkMemory.getFile());
                        }

                        if (checkMemory.equals(innerMemory, tempByte))
                        {
                            DetermineMatch.setMatchedItems(matches, checkMemory, innerMemory);
                        }

                       /* if (DetermineMatch.isProbablePictureMatch(checkMemory, currentMemory))
                        {
                            if (tempByte == null && checkMemory.getMetadata() == null)
                            {
                                tempByte = Shared.returnPixelVal(checkMemory.getFile());
                            }

                            if (checkMemory.getMetadata() == null && currentMemory.getMetadata() == null && DetermineMatch.isDuplicatePictureMatch(tempByte, currentMemory.getFile()))
                            {
                                DetermineMatch.setMatchedItems(matches, checkMemory, currentMemory);
                            }
                            if (checkMemory.getMetadata() != null && currentMemory.getMetadata() != null && DetermineMatch.isDuplicatePictureMatchRAW(checkMemory, currentMemory))
                            {
                                DetermineMatch.setMatchedItems(matches, checkMemory, currentMemory);
                            }
                        }

                        if (DetermineMatch.isPossibleVideoMatch(checkMemory, currentMemory) &&
                                DetermineMatch.isDuplicateVideo(checkMemory.getFile().toPath(), currentMemory.getFile().toPath()))
                        {
                            DetermineMatch.setMatchedItems(matches, checkMemory, currentMemory);
                        }*/
                    } catch (final Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                if (!matches.isEmpty())
                {
                    Reporter.reportDuplicates(matches);
                }

                if (counter % 10000 == 0)
                {
                    System.out.println("Current memories checked: " + counter);
                }
            }

            final long endTime = System.nanoTime();
            final long totalTime = endTime - startTime;
            System.out.println("Total  check  for  " + currentMemories.size() + " memories took: " + Shared.printTotalTimeTaken(totalTime));
        } catch (final Exception e)
        {
            e.printStackTrace();
        }
    }
}