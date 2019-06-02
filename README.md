# LinuxPathTraveral

This application is an emulation of a terminal. 

### Commands supported: 
- ls 
- rm
- cd
- pwd
- mkdir 
- sessionClear => To clear the session and restore logical file system state to root
- showFs => Shows the current structure of the logical file system 


**NOTE:** ls,rm,cd are all supported for both relative and absolute paths.
Also ls and cd support  '..' as an argument 



### Code Structure: 
     Classes : 
     - Terminal (driver class) 
     - File System
     - Command 
     - File
     - linuxPathTraversalUtils (utility class)
    


**We don't  ‘actually’ create/remove real directories (using OS functions).The application simply keeps a logical track of directories in a running session.**

