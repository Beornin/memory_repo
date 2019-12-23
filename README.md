# memory_repo
memory_repo is a program developed to make it easier to setup and manage photo and videos (memories) in a single repository

## Motivation
Being the tech person of the family, I took it upon myself to consolidate all the memories into one repository and make
 sure all memories were backed up to external drives. Great grandparents bringing out photos of past family members or
  childhood photos is something I wanted to continue in our family in this digital age. In consolidating all the memories
  into one directory, I saw there were many duplicates. Thus I had a problem, how to remove them but also how to check
  new memories brought in from phones and cameras to make sure no new duplicates come in?

While I did see free and/or paid software's that do things similar, most seemed to look only at a date and size.
 I was looking for actual image comparisons. I also did not fully trust an
 outside software looking over all these files in detail, not knowing what their code actually does. So I opted to test
 my coding skill to make this suite as well as keep things "in house" knowing full well what the code actually does.

## Running the suite
There are currently 4 runnable files.

*RepoCheck* - Point this to the directory you have your memories stored and it will check all photo and videos in the folders.
 Use this to set your clean baseline. This puts a matches.txt on your desktop of matched files.

*DeleteMatches* - Reads the matches.txt file and will DELETE any memories still listed! Again, this deletes them!! The way
I currently handle duplicates is remove rows from the matches.txt that I want to KEEP! Then in running this, get rid of any duplicates.

*Import* - This is used once you have a duplicate free repo. This sorts memories from an Import folder into a Passed or
Flagged folder to indicate if they were non-duplicate or duplicate respectfully

*DateSortFolder* - This I use on a few folders of memories that I want to attempt to sort them out by date taken/created/modified.

## Performance
My current system is a i5-7600K (4core/4thread) running at 5 Ghz with 32GB RAM (DDR4-3200). I usually allocate 15+ GB to the JVM.
I have ~80,000 current memories in my repo, about 2.2 TB on a standard 5400 RPM hard drive.
There is a cache.beo file placed in the repository which really helps in the loading of memories. Without
this cache present, it can take hours to load all the memories the first time depending on size of repo. Once a cache is created,
the process takes minutes to load memories from the cache!

Example of no cache - Repo Check output:
 took: 02(h)23(m)16(s)0309(ms) to get 80002 files
Total  check  for  80002 memories took: 53(m)24(s)0703(ms)
Total  process time  for  80002 memories took: 03(h)16(m)58(s)0000(ms)
************************
***REPO META DATA***
Total Repo Size: 2.21 TB
Total Repo Memories: 80002
***File Type - Count - Percent of Repo***
TIF - 11641 - 14.55%
MP4 - 1264 - 1.58%
JPG - 31335 - 39.17%
WMV - 4 - 0.00%
CR2 - 32619 - 40.77%
AVI - 17 - 0.02%
MOV - 2692 - 3.36%
HEIC - 20 - 0.02%
PNG - 388 - 0.48%
JPEG - 22 - 0.03%
************************


## Credits
To my family for inspiring me to make this to help keep our memories easily shareable and lasting!
