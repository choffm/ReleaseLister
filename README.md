ReleaseLister
=============

ReleaseLister is a useful tool to search, organize and verify MP3 and FLAC 
Scene Releases. Optimized for speed and huge collections, ReleaseLister can even handle Tons 
of GB of releases and quite quickly perform search and SFV verify.

It is written in java and platform independent as such, licensed under The GNU General Public 
Lincense v3.0 (GPL).


Dependencies
============

ReleaseLister depends on two external java libraries, which are not distributed within the 
source, but included in the binary jar file in bin/.

Apache Commons IO >= 2.4(Apache License 2.0): 
https://commons.apache.org/proper/commons-io/download_io.cgi

JAudiotagger >= 2.04 (LGPL 3): http://www.jthink.net/jaudiotagger/ 


Key Features
============

- Recursive search for MP3 and FLAC Scene Releases according to scene rules in specified 
paths
- Filter functions
- Display and sort by size, genre, bitrate, completeness and correctness (by SFV), NFO
- Copy, move, delete selected releases to other locations
- Perform CRC check for selected releases
- Play Releases, open NFO and show releases in file manager
- Autosave and restore database on quit/startup


Upcoming Features
===========

- Provide a command line interface
- Improve UI experience
- Include SQLite support
