package processing;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import obj.Memory;
import obj.UserInputObj;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

class MemoryCreate implements Runnable
{
    private static final String[] PICTURE_EXTENSIONS = new String[]{
            "gif", "png", "bmp", "jpg", "jpeg", "heic"
    };
    private static final String[] PICTURE_RAW_EXTENSIONS = new String[]{
            "raw", "cr2", "tiff", "tif"
    };
    private static final String[] VIDEO_EXTENSIONS = new String[]{
            "mp4", "mov", "mp4", "mov", "avi", "vlc", "wmv"
    };
    private final File file;
    private final List<Memory> memories;
    private final UserInputObj userIo;

    MemoryCreate(final UserInputObj uio, final File fileIn, final List<Memory> memoriesIn)
    {
        userIo = uio;
        file = fileIn;
        memories = memoriesIn;
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

    public void run()
    {
        if (file.isFile() && file.length() > 0)
        {
            final Memory memory;
            if (isPicture(file) || isRaw(file))
            {
                memory = new Memory();
                memory.setName(file.getName());
                memory.setPath(file.getPath());
                memory.setFile(file);
                memory.setSize(file.length());
                memory.setVideo(false);
                memory.setPicture(true);
                memory.setImported(this.userIo.isImported());

                try
                {
                    final BufferedImage image = ImageIO.read(memory.getFile());
                    memory.setWidth(image.getWidth());
                    memory.setHeight(image.getHeight());
                    if (memory.getFile().exists())
                    {
                        memory.setFirstBytes(Arrays.copyOfRange(Shared.returnPixelVal(memory.getFile()), 0, 3));
                    }
                } catch (final Exception ioe)
                {
                    try
                    {
                        final Metadata metadata = ImageMetadataReader.readMetadata(file);
                        memory.setMetadata(metadata);

                        final ExifIFD0Directory exifIFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
                        memory.setWidth(Integer.parseInt(exifIFD0Directory.getString(ExifIFD0Directory.TAG_IMAGE_WIDTH)));
                        memory.setHeight(Integer.parseInt(exifIFD0Directory.getString(ExifIFD0Directory.TAG_IMAGE_HEIGHT)));

                        final ExifSubIFDDirectory exifSubIFDDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
                        memory.setDate(exifSubIFDDirectory.getString(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL));
                        memory.setMetaDataLoaded(true);

                    } catch (final ImageProcessingException | IOException | NullPointerException ipe)
                    {
                        System.out.println("Error loading memory: " + file.getName());
                        //ipe.printStackTrace();
                    }
                }

                this.memories.add(memory);
                if (this.memories.size() % 10000 == 0)
                {
                    System.out.println("Current memories loaded: " + this.memories.size());
                }
            }
            else if (isVideo(file))
            {
                memory = new Memory();
                memory.setName(file.getName());
                memory.setPath(file.getPath());
                memory.setFile(file);
                memory.setSize(file.length());
                memory.setImported(this.userIo.isImported());
                memory.setVideo(true);
                memory.setPicture(false);
                this.memories.add(memory);
                if (this.memories.size() % 10000 == 0)
                {
                    System.out.println("Current memories loaded: " + this.memories.size());
                }
            }
            else
            {
                System.out.println("Not a memory type: " + file.getPath());
            }
        }
    }
}