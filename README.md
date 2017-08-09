# CheatDetector
This is a simple cheat detection tool I made for when I was a teaching assistant. It can be used to automatically compare all code files pairwise in a directory to determine if any two are too similar. It currently is just a command line tool that has no GUI. I don't currently plan to add a GUI because I may not be a teaching assistant again. If I am, maybe I'll throw a GUI with this.

# General Capabilities
This program can do a few things:
- Remove whitespace from any text file.
- Remove C-style comments from any text file.
- Compute the raw edit distance between any two text files.
- Compute the edit distance between any two text files after having removed whitespace and comments from them (modified edit distance).
- Automatically pairwise compare all files (.c, .h, .java) in the working directory using the modified edit distance to determine if and two fall under a threshold of similarity, and write a report file at the end.
- Automatically pairwise compare all files (.c, .h, .java) between two directories using the modified edit distance to determine if and two fall under a threshold of similarity, and write a report file at the end.

# Getting Started
This is a command line tool. To simply use this tool without any modification, there is a file CheatDetector.jar in the root directory you can download. Simply run the jar file via a command line interface using a command like "java -jar CheatDetector.jar" to get things going! There will be a comprehensive help message printed out when you run it with no arguments that will explain how to use each argument.
