package processing;

import io.Mover;
import obj.Memory;

import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

class DetermineMatch
{
    public static boolean isProbablePictureMatch(final Memory test1, final Memory test2)
    {
        //if we are doing picture matches, and neither is already matched, and they are not the same object
        //and if the width and height match, and if the first few bytes match, then it's probable they are identical
        return (!test1.isMatched() && !test2.isMatched() && test1 != test2 &&
                test1.isPicture() && test2.isPicture() &&
                test1.getWidth() == test2.getWidth() &&
                test1.getHeight() == test2.getHeight() &&
                Arrays.equals(test1.getFirstBytes(), test2.getFirstBytes()) || isProbablePictureMatchRAW(test1, test2));
    }

    private static boolean isProbablePictureMatchRAW(final Memory test1, final Memory test2)
    {
        return !test1.isMatched() && !test2.isMatched() && test1 != test2 &&
                test1.isPicture() && test2.isPicture() &&
                test1.getWidth() == test2.getWidth() &&
                test1.getHeight() == test2.getHeight() &&
                test1.getMetadata() != null &&
                test2.getMetadata() != null &&
                test1.getDate().equals(test2.getDate());
    }

    public static boolean isDuplicatePictureMatch(final byte[] tempByte, final File test2File)
    {
        return tempByte != null && Arrays.equals(tempByte, Shared.returnPixelVal(test2File));
    }

    public static boolean isDuplicatePictureMatchRAW(final Memory test1, final Memory test2)
    {
        boolean same = true;
        if (test1.getMetadata() != null && test2.getMetadata() != null)
        {
            while (test1.getMetadata().getDirectories().iterator().hasNext())
            {
                if (!test2.getMetadata().getDirectories().iterator().hasNext() ||
                        !test1.getMetadata().getDirectories().iterator().next().equals(test2.getMetadata().getDirectories().iterator().next()))
                {
                    same = false;
                    break;
                }
            }
        }
        else
        {
            same = false;
        }
        return same;
    }

    public static boolean isPossibleVideoMatch(final Memory test1, final Memory test2)
    {
        return !test1.isMatched() && !test2.isMatched() &&
                test1.isVideo() && test2.isVideo() && test1 != test2 && test1.getSize() == test2.getSize();
    }

    public static boolean isDuplicateVideo(final Path filea, final Path fileb) throws IOException
    {
        final long size = Files.size(filea) / 20;
        final int mapspan = 4 * 1024 * 1024;

        MappedByteBuffer mbb;
        MappedByteBuffer mba;
        final FileChannel chanb;
        final FileChannel chana;
        try
        {
            chana = (FileChannel) Files.newByteChannel(filea);
            chanb = (FileChannel) Files.newByteChannel(fileb);

            for (long position = 0; position < size; position += mapspan)
            {
                mba = mapChannel(chana, position, size);
                mbb = mapChannel(chanb, position, size);

                if (mba.compareTo(mbb) != 0)
                {
                    return false;
                }
            }
        } catch (final Exception ignored)
        {
        }
        return true;
    }

    private static MappedByteBuffer mapChannel(final FileChannel channel, final long position, final long size) throws IOException
    {
        final long end = Math.min(size, position + 4194304);
        final long maplen = (int) (end - position);
        return channel.map(FileChannel.MapMode.READ_ONLY, position, maplen);
    }

    public static void setMatchedItems(final ArrayList<Memory> matches, final Memory staged, final Memory current)
    {
        if (!matches.contains(staged))
        {
            matches.add(staged);
        }
        if (!matches.contains(current))
        {
            matches.add(current);
        }
        if (!staged.isMatched())
        {
            staged.setMatched(true);
        }
        if (!current.isMatched())
        {
            current.setMatched(true);
        }

        //Mover.moveImportFileMatched(staged);
    }
}