package processing;

import com.drew.metadata.Directory;
import com.drew.metadata.Tag;
import obj.Memory;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class handles determining if two memories are the same
 */
class DetermineMatch
{
    /**
     * This determines if it is probable if two picture memories match
     *
     * @param test1 First Memory
     * @param test2 Second Memory
     * @return true if probable match
     */
    public static boolean isProbablePictureMatch(final Memory test1, final Memory test2)
    {
        return !test1.isMatched() && !test2.isMatched() && !test1.equals(test2) &&
                test1.isPicture() && test2.isPicture() &&
                test1.getWidth() == test2.getWidth() &&
                test1.getHeight() == test2.getHeight() &&
                test1.getSize() == test2.getSize() &&
                Arrays.equals(test1.getFirstBytes(), test2.getFirstBytes()) || isProbablePictureMatchRAW(test1, test2);
    }

    /**
     * This determines if it is probable if two picture memories match
     *
     * @param test1 First Memory
     * @param test2 Second Memory
     * @return true if probable match
     */
    private static boolean isProbablePictureMatchRAW(final Memory test1, final Memory test2)
    {
        return !test1.isMatched() && !test2.isMatched() && !test1.equals(test2) &&
                test1.isPicture() && test2.isPicture() &&
                test1.getWidth() == test2.getWidth() &&
                test1.getHeight() == test2.getHeight() &&
                test1.getMetadata() != null &&
                test2.getMetadata() != null &&
                test1.getSize() == test2.getSize() &&
                test1.getDate().equals(test2.getDate());
    }

    /**
     * This determines if two picture memories match by doing a pixel value test
     *
     * @param tempByte bytes of memory 1
     * @param test2    Second Memory
     * @return true if duplicate
     */
    public static boolean isDuplicatePictureMatch(final byte[] tempByte, final File test2)
    {
        return tempByte != null && Arrays.equals(tempByte, Shared.returnPixelVal(test2));
    }

    /**
     * This determines if two picture memories are the same for RAW types by comparing each metadata element
     *
     * @param test1 First Memory
     * @param test2 Second Memory
     * @return true if a match
     */
    public static boolean isDuplicatePictureMatchRAW(final Memory test1, final Memory test2)
    {
        boolean same = true;

        try
        {
            if (test1.getMetadata() != null && test2.getMetadata() != null && test1.getMetadata().getDirectoryCount() == test2.getMetadata().getDirectoryCount())
            {
                final List<String> valuesTest1 = new ArrayList<>(test1.getMetadata().getDirectoryCount());

                for (final Directory directory : test1.getMetadata().getDirectories())
                {
                    for (final Tag tag : directory.getTags())
                    {
                        valuesTest1.add(tag.getDescription());
                    }
                }

                int position = 0;
                for (final Directory directory : test2.getMetadata().getDirectories())
                {
                    for (final Tag tag : directory.getTags())
                    {
                        if (StringUtils.equals(valuesTest1.get(position), tag.getDescription()))
                        {
                            position++;
                        }
                        else
                        {
                            same = false;
                            break;
                        }
                    }
                }
            }
            else
            {
                same = false;
            }
        } catch (final Exception e)
        {
            e.printStackTrace();
            same = false;
        }
        return same;
    }

    /**
     * This determines if the two memories passed in could be a match
     *
     * @param test1 First Memory
     * @param test2 Second Memory
     * @return true if probable match
     */
    public static boolean isPossibleVideoMatch(final Memory test1, final Memory test2)
    {
        return !test1.isMatched() && !test2.isMatched() &&
                test1.isVideo() && test2.isVideo() && test1 != test2 && test1.getSize() == test2.getSize();
    }

    /**
     * This determines if two video types memories are a match
     *
     * @param memoryOne First Memory
     * @param memoryTwo Second Memory
     * @return true if a match
     * @throws IOException thrown if error occurs
     */
    public static boolean isDuplicateVideo(final Path memoryOne, final Path memoryTwo) throws IOException
    {
        final long size = Files.size(memoryOne) / 20;
        final int mapspan = 4 * 1024 * 1024;

        MappedByteBuffer mbb;
        MappedByteBuffer mba;
        final FileChannel chanb;
        final FileChannel chana;
        try
        {
            chana = (FileChannel) Files.newByteChannel(memoryOne);
            chanb = (FileChannel) Files.newByteChannel(memoryTwo);

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

    /**
     * This returnes a certain portion of a video for checking duplicates
     *
     * @param channel  The channel for a memory
     * @param position where to start
     * @param size     how much to grab
     * @return the next byteBuffer
     * @throws IOException if error occurs
     */
    private static MappedByteBuffer mapChannel(final FileChannel channel, final long position, final long size) throws IOException
    {
        final long end = Math.min(size, position + 4194304);
        final long maplen = (int) (end - position);
        return channel.map(FileChannel.MapMode.READ_ONLY, position, maplen);
    }

    /**
     * This sets the two matched memories up for reporting and moving later
     *
     * @param matches All matches for the memory
     * @param staged  The memory from Stage folder
     * @param current The current memory
     */
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
    }
}