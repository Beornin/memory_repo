package processing.load;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.file.FileSystemDirectory;
import obj.Memory;
import obj.UserInput;
import processing.Shared;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;


class MemoryCreateMeta implements Runnable
{

    private final String pattern = "yyyy-MM-dd";
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    private final File file;
    private final List<Memory> memories;
    private final UserInput userIo;

    MemoryCreateMeta(final UserInput uio, final File fileIn, final List<Memory> memoriesIn)
    {
        userIo = uio;
        file = fileIn;
        memories = memoriesIn;
    }

    private static boolean isVideo(final File file)
    {
        for (final String x : Shared.VIDEO_EXTENSIONS)
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
        for (final String x : Shared.PICTURE_EXTENSIONS)
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
        for (final String x : Shared.PICTURE_RAW_EXTENSIONS)
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
                    final Metadata metadata = ImageMetadataReader.readMetadata(file);
                    memory.setMetadata(metadata);
                    try
                    {
                        final ExifSubIFDDirectory exifSubIFDDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
                        memory.setDate(simpleDateFormat.format(exifSubIFDDirectory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL)) + "-ORI");
                        memory.setMetaDataLoaded(true);
                    } catch (final NullPointerException npe)
                    {
                        final FileSystemDirectory fileSystemDirectory = metadata.getFirstDirectoryOfType(FileSystemDirectory.class);
                        memory.setDate(simpleDateFormat.format(fileSystemDirectory.getDate(FileSystemDirectory.TAG_FILE_MODIFIED_DATE)) + "-MOD");
                    }
                } catch (final Exception ioe)
                {
                    try
                    {
                        final BufferedImage image = ImageIO.read(memory.getFile());
                        memory.setWidth(image.getWidth());
                        memory.setHeight(image.getHeight());
                        if (memory.getFile().exists())
                        {
                            memory.setFirstRgb(Arrays.copyOfRange(Shared.returnPixelVal(memory.getFile()), 0, 3));
                        }
                    } catch (final IOException | NullPointerException ipe)
                    {
                        System.out.println("Error loading memory: " + file.getName());
                    }
                }

                this.memories.add(memory);
                if (this.memories.size() % 1000 == 0)
                {
                    System.out.println("Current memories loaded: " + this.memories.size());
                }
            }
            else if (isVideo(file))
            {
                memory = new Memory();
                try
                {
                    final Metadata metadata = ImageMetadataReader.readMetadata(file);
                    memory.setMetadata(metadata);
                    memory.setMetaDataLoaded(true);
                    try
                    {
                        final ExifSubIFDDirectory exifSubIFDDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
                        memory.setDate(simpleDateFormat.format(exifSubIFDDirectory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL)) + "-ORI");
                    } catch (final NullPointerException npe)
                    {
                        final FileSystemDirectory fileSystemDirectory = metadata.getFirstDirectoryOfType(FileSystemDirectory.class);
                        memory.setDate(simpleDateFormat.format(fileSystemDirectory.getDate(FileSystemDirectory.TAG_FILE_MODIFIED_DATE)) + "-MOD");
                    }
                } catch (final Exception ignored)
                {

                }
                memory.setName(file.getName());
                memory.setPath(file.getPath());
                memory.setFile(file);
                memory.setSize(file.length());
                memory.setImported(this.userIo.isImported());
                memory.setVideo(true);
                memory.setPicture(false);
                this.memories.add(memory);
                if (this.memories.size() % 1000 == 0)
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