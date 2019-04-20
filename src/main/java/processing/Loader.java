package processing;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import obj.TestObj;
import obj.UserInputObj;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Loader
{
    private static final String[] PICTURE_EXTENSIONS = new String[]{
            "gif", "png", "bmp", "jpg", "jpeg", "heic"
    };
    private static final String[] PICTURE_RAW_EXTENSIONS = new String[]{
            "raw", "cr2", "tiff"
    };
    private static final String[] VIDEO_EXTENSIONS = new String[]{
            "mp4", "mov", "mp4", "mov", "avi", "vlc", "wmv"
    };

    public static ArrayList<TestObj> gatherCurrentFiles(final UserInputObj uio)
    {
        final ArrayList<TestObj> possibleMatches = new ArrayList<>(0);
        final long startTime1 = System.nanoTime();

        System.out.println("Getting current files...");

        Loader.gatherFiles(uio, possibleMatches);

        final long endTime1 = System.nanoTime();
        final long totalTime1 = endTime1 - startTime1;
        System.out.println(" took: " + Shared.printTotalTimeTaken(totalTime1) + " to get " + possibleMatches.size() + " files");


        return possibleMatches;
    }

    public static ArrayList<TestObj> gatherNewFiles(final UserInputObj uio)
    {
        final ArrayList<TestObj> possibleMatches = new ArrayList<>(0);
        final long startTime1 = System.nanoTime();

        System.out.print("Getting STAGE files...");

        Loader.gatherFiles(uio, possibleMatches);

        final long endTime1 = System.nanoTime();
        final long totalTime1 = endTime1 - startTime1;
        System.out.println(" took: " + Shared.printTotalTimeTaken(totalTime1) + " to get " + possibleMatches.size() + " files");


        return possibleMatches;
    }

    private static void gatherFiles(final UserInputObj uio, final ArrayList<TestObj> possibleMatches)
    {
        listFiles(uio, uio.getStartingFolder(), possibleMatches);
    }

    private static void listFiles(final UserInputObj userInputObj, final File directory, final ArrayList<TestObj> pics)
    {
        // Get all the files from a directory.
        final File[] fList = directory.listFiles();
        TestObj pic;
        if (fList != null && fList.length > 0)
        {
            for (final File file : fList)
            {
                if (file.isFile() && file.length() > 0)
                {
                    if (isPicture(file) || isRaw(file))
                    {
                        pic = new TestObj();
                        pic.setName(file.getName());
                        pic.setPath(file.getPath());
                        pic.setFile(file);
                        pic.setSize(file.length());
                        pic.setVideo(false);
                        pic.setPicture(true);
                        pic.setImported(userInputObj.isImported());

                        try
                        {
                            final BufferedImage image = ImageIO.read(pic.getFile());
                            pic.setWidth(image.getWidth());
                            pic.setHeight(image.getHeight());
                            pic.setFirstBytes(Arrays.copyOfRange(Shared.returnPixelVal(pic.getFile()), 0, 3));
                        } catch (final Exception ioe)
                        {
                            //System.out.println(file.getPath() + " trying Meta");
                            try
                            {
                                final Metadata metadata = ImageMetadataReader.readMetadata(file);
                                pic.setMetadata(metadata);

                                final ExifIFD0Directory exifIFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
                                pic.setWidth(Integer.valueOf(exifIFD0Directory.getString(ExifIFD0Directory.TAG_IMAGE_WIDTH)));
                                pic.setHeight(Integer.valueOf(exifIFD0Directory.getString(ExifIFD0Directory.TAG_IMAGE_HEIGHT)));

                                final ExifSubIFDDirectory exifSubIFDDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
                                pic.setDate(exifSubIFDDirectory.getString(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL));
                                pic.setExposure(exifSubIFDDirectory.getString(ExifSubIFDDirectory.TAG_EXPOSURE_TIME));

                            } catch (final ImageProcessingException | IOException | NullPointerException ipe)
                            {
                                System.out.println("Error: " + file.getName());
                                ipe.printStackTrace();
                            }
                        }

                        pics.add(pic);
                        if (pics.size() % 5000 == 0)
                        {
                            System.out.println("Files processed: " + pics.size());
                        }
                    }
                    else if (isVideo(file))
                    {
                        pic = new TestObj();
                        pic.setName(file.getName());
                        pic.setPath(file.getPath());
                        pic.setFile(file);
                        pic.setSize(file.length());
                        pic.setImported(userInputObj.isImported());
                        pic.setVideo(true);
                        pic.setPicture(false);
                        pics.add(pic);
                        if (pics.size() % 5000 == 0)
                        {
                            System.out.println("Files processed: " + pics.size());
                        }
                    }
                }
                else if (file.isDirectory())
                {
                    listFiles(userInputObj, file, pics);
                }
            }
        }
    }

    private static boolean isVideo(final File file)
    {
        for (final String x : VIDEO_EXTENSIONS)
        {
            if (file.getName().toLowerCase().endsWith(x))
            {
                return true;
            }
        }
        return false;
    }

    private static boolean isPicture(final File file)
    {
        for (final String x : PICTURE_EXTENSIONS)
        {
            if (file.getName().toLowerCase().endsWith(x))
            {
                return true;
            }
        }
        return false;
    }

    private static boolean isRaw(final File file)
    {
        for (final String x : PICTURE_RAW_EXTENSIONS)
        {
            if (file.getName().toLowerCase().endsWith(x))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Write all extracted values to stdout.
     */
    private static void print(final Metadata metadata, final String method)
    {
        System.out.println();
        System.out.println("-------------------------------------------------");
        System.out.print(' ');
        System.out.print(method);
        System.out.println("-------------------------------------------------");
        System.out.println();
        //
        // A Metadata object contains multiple Directory objects
        //
        for (final Directory directory : metadata.getDirectories())
        {
            //
            // Each Directory stores values in Tag objects
            //
            for (final Tag tag : directory.getTags())
            {
                System.out.println(tag);
            }
            //
            // Each Directory may also contain error messages
            //
            for (final String error : directory.getErrors())
            {
                System.err.println("ERROR: " + error);
            }
        }
    }
}