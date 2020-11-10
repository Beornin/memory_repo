package processing.load;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.ExifThumbnailDirectory;
import com.drew.metadata.file.FileSystemDirectory;
import obj.Memory;
import obj.UserInput;
import processing.Shared;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

class MemoryCreate implements Runnable
{
    private final String pattern = "yyyy-MM-dd";
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
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
                    System.out.println("Error loading memory create for : " + file.getPath() + " attempting metadata...");
                    //ioe.printStackTrace();
                    try
                    {
                        final Metadata metadata = ImageMetadataReader.readMetadata(file);
                        memory.setMetadata(metadata);
                        memory.setMetaDataLoaded(true);
                        /*for (Directory directory : metadata.getDirectories())
                        {
                            for (Tag tag : directory.getTags())
                            {
                                System.out.format("[%s] - %s = %s",
                                        directory.getName(), tag.getTagName(), tag.getDescription());
                            }
                            if (directory.hasErrors())
                            {
                                for (String error : directory.getErrors())
                                {
                                    System.err.format("ERROR: %s", error);
                                }
                            }
                        }*/
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
                    } catch (final Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }

                this.memories.add(memory);
                if (this.memories.size() % 10000 == 0)
                {
                    System.out.println("Current memories loaded: " + this.memories.size());
                }
            } else if (isVideo(file))
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
            } else
            {
                System.out.println("Not a memory type: " + file.getPath());
            }
        }
    }
}