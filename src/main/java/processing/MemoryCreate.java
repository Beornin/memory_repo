package processing;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import obj.Memory;
import obj.UserInput;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
    private final UserInput userIo;

    MemoryCreate(final UserInput uio, final File fileIn, final List<Memory> memoriesIn)
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
                        final int[] rgb = new int[3];
                        rgb[0] = image.getRGB(0, 0);
                        rgb[1] = image.getRGB(image.getWidth() / 2, image.getHeight() / 2);
                        rgb[2] = image.getRGB(image.getWidth() - 1, image.getHeight() - 1);
                        memory.setFirstRgb(rgb);
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
                        ipe.printStackTrace();
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