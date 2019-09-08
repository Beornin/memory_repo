package processing.load;

import obj.Memory;
import obj.UserInput;
import processing.Shared;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

class MemoryCreate implements Runnable
{
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
                    System.out.println("Error loading memory: " + file.getPath());
                    ioe.printStackTrace();
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