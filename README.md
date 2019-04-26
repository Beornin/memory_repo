# memory_sorter2
memory_sorter2 allows staging and checking against a current photo/video repository to flag duplicate and allow non-duplicates into the library.
## Motivation
Being the family's tech person, I took it upon myself became the family's photo and video repository manager.
 I soon found there were many duplicates in our current repo. Using memory_sorter1, I was able to
start with a clean baseline, and then using memory_sorter2, I am able to keep most duplicate photos and 
videos from finding their way into our library. While I did see other free and paid software's that do things similar, most seemed to look
 only at a date and size, but I was looking for actual image comparisons, which some file types will be checked by. I also did not fully trust an outside
 software looking over all these files in detail, so I opted to test my coding skill out to make these two apps as well as keep things in house.
## Tech/framework used
Maven to build

drewnoakes/metadata-extractor
## Performance
My current system is a i5-7600K (4core/4thread) running at 4.5Ghz with 16GB RAM. I usually allocate 10+ GB to the JVM.
I have ~68,000 current memories in my repo. During testing, while using 3 threads in the pools,
I would have 3,755 memories loaded from Staging in ~4minutes, and the ~68,000 current memories loaded in ~1hour & 20minutes.
The time to check the 3,755 against the current repo took ~13minutes. So in total, about an hour and a half from start to memories sorted.
## Credits
Special shout out to drewnoakes for the metadata-extractor project which helped so much in bringing this to creation: https://github.com/drewnoakes/metadata-extractor