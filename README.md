# memory_sorter
memory_sorter is a program developed to make it easier to setup and manage photo and videos (memories) in a single repository

## Motivation
Being the tech person of the family, I took it upon myself to consolidate all the memories into one repository and make sure all memories were backed up to external drives. Great grandparents bringing out photos of past family members or childhood photos is something I wanted to continue in our family in this digital age. In consolidating everyones memories into one directory, I saw there were many duplicates. Thus I had a problem, how to remove them but also how to check new memories brought in from phones and cameras to make sure no new duplicates come in?

While I did see free and/or paid software's that do things similar, most seemed to look only at a date and size. I was looking for actual image comparisons, which some file types can be checked by. I also did not fully trust an outside software looking over all these files in detail, not knowing what their code actually does. So I opted to test my coding skill to make this suite as well as keep things "in house" knowing full well what the code actually does.

## Running the suite
There are currently 4 runnable files.

*RepoCheck* - Point this to the directory you have your memories stored and it will check all photo and videos in the folders. Use this to set your clean baseline. This puts a matches.txt on your desktop of matched files.

*DeleteMatches* - Reads the matches.txt file and will DELETE any memories still listed! Again, this deletes them!! The way I currently handle duplicates is remove rows from the matches.txt that I want to KEEP! Then in running this, get rid of any duplicates.

*Import* - This is used once you have a duplicate free repo. This sorts memories from an Import folder into a Passed or Flagged folder to indicate if they were non-duplicate or duplicate respectfully

*DateSortFolder* - This I use on a few folders of memories that I want to attempt to sort them out by date taken/created/modified.

## Performance
My current system is a i5-7600K (4core/4thread) running at 5 Ghz with 32GB RAM (DDR4-3200). I usually allocate 15+ GB to the JVM.
I have ~79,000 current memories in my repo, about 2 TB on a standard 5400 RPM hard drive.

*RepoChecker* - (4threads) Total check for 78337 memories took: 40(m)05(s)/Total process time for 78337 memories took: 01(h)54(m)39(s)

*Import* - (4threads) Total check between 80447 starting memories and 85 staged memories took: 42(m)19(s)

## Credits
Special shout out to drewnoakes for the metadata-extractor project which helped so much in bringing this to creation: https://github.com/drewnoakes/metadata-extractor

To my family for inspiring me to make this to help keep our memories easily shareable and lasting!
