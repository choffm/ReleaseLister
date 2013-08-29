ReleaseLister
=============

ReleaseLister is a useful tool to search and organize MP3 Scene Releases. It is written in 
java and platform independent as such, licensed The GNU General Public Lincense v3.0 (GPL).


Dependencies
============

ReleaseLister depends on two external java libraries, which are not distributed within this 
project:

Apache Commons IO >= 2.4(Apache License 2.0): 
https://commons.apache.org/proper/commons-io/download_io.cgi

JAudiotagger >= 2.04 (LGPL 3): http://www.jthink.net/jaudiotagger/ 


Key Features
============

- Recursive search for MP3 Releases according to scene rules in specified paths
- Search and filter specific releases
- Display and sort by genre, bitrate, completeness and correctness (by SFV), size, NFO
- Copy, move, delete selected releases to other locations
- Perform CRC check for selected releases
- Play Releases and show releases in file manager
- Autosave and restore database on quit/startup


Upcoming Features
===========

- Provide a command line interface
- Implementation of FLAC Release support
