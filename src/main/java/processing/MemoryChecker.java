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
            int countPictureProbableRight = 0;
            int countPictureProbableWrong = 0;

            int countVideoProbableRight = 0;
            int countVideoProbableWrong = 0;

            final long startTime = System.nanoTime();
            final ArrayList<Memory> matches = new ArrayList<>();
            int counter = 0;
            //if we have to fully scan the pic, save it so we don't do it each time
            int[] tempByte;
            Memory innerMemory;
            for (final Memory checkMemory : currentMemories)
            {
                tempByte = null;
                counter++;

                if (checkMemory.isMatched())
                {
                    continue;
                }
                matches.clear();
                for (int iCount = counter; iCount < currentMemories.size(); iCount++)
                {
                    innerMemory = currentMemories.get(iCount);

                    //if the memory  is already matched or is the same memory, skip it
                    if (innerMemory.isMatched() || checkMemory.equals(innerMemory))
                    {
                        continue;
                    }
                    try
                    {
                        if (DetermineMatch.isProbablePictureMatch(checkMemory, innerMemory))
                        {
                            if (tempByte == null && checkMemory.getMetadata() == null)
                            {
                                tempByte = Shared.returnPixelVal(checkMemory.getFile());
                            }

                            if (checkMemory.getMetadata() == null && innerMemory.getMetadata() == null && DetermineMatch.isDuplicatePictureMatch(tempByte, innerMemory.getFile()))
                            {
                                DetermineMatch.setMatchedItems(matches, checkMemory, innerMemory);
                                countPictureProbableRight++;
                            }
                            else if (checkMemory.getMetadata() != null && innerMemory.getMetadata() != null && DetermineMatch.isDuplicatePictureMatchRAW(checkMemory, innerMemory))
                            {
                                DetermineMatch.setMatchedItems(matches, checkMemory, innerMemory);
                                countPictureProbableRight++;
                            }
                            else
                            {
                                countPictureProbableWrong++;
                            }
                        }

                        if (DetermineMatch.isPossibleVideoMatch(checkMemory, innerMemory))
                        {
                            if (DetermineMatch.isDuplicateVideo(checkMemory.getFile().toPath(), innerMemory.getFile().toPath()))
                            {
                                DetermineMatch.setMatchedItems(matches, checkMemory, innerMemory);
                                countVideoProbableRight++;
                            }
                            else
                            {
                                countVideoProbableWrong++;
                            }
                        }
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

            System.out.println("Total  Picture Possible RIGHT  " + countPictureProbableRight);
            System.out.println("Total  Picture Possible WRONG  " + countPictureProbableWrong);
            System.out.println("Total  Video Possible RIGHT  " + countVideoProbableRight);
            System.out.println("Total  Video Possible WRONG  " + countVideoProbableWrong);

            final long endTime = System.nanoTime();
            final long totalTime = endTime - startTime;
            System.out.println("Total  check  for  " + currentMemories.size() + " memories took: " + Shared.printTotalTimeTaken(totalTime));
        } catch (final Exception e)
        {
            e.printStackTrace();
        }
    }
}