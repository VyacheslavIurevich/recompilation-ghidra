<img src="Ghidra/Features/Base/src/main/resources/images/GHIDRA_3.png" width="400">

# Ghidra Recompilation
Ghidra Recompilation is a fork of [Ghidra](https://github.com/NationalSecurityAgency/ghidra) that allows decompiled С code to be made recompilable.

Our goals:
- Achieve recompilation.
- Make decompiled C code more readable.

## Install
To install an official pre-built multi-platform Ghidra release:  
* Install [JDK 21 64-bit][jdk]
* Download a Ghidra [release file][releases]
* Extract the Ghidra release file
* Launch Ghidra: `./ghidraRun` (or `ghidraRun.bat` for Windows)

For additional information and troubleshooting tips about installing and running a Ghidra release, 
please refer to `docs/InstallationGuide.html` which can be found in your extracted Ghidra release 
directory. 

## Build

To create the latest development build for your platform from this source repository:

##### Install build tools:
* [JDK 21 64-bit][jdk]
* [Gradle 8.5+][gradle] (or provided Gradle wrapper if Internet connection is available)
* [Python3][python3] (version 3.9 to 3.12) with bundled pip
* make, gcc, and g++ (Linux/macOS-only)
* [Microsoft Visual Studio][vs] 2017+ or [Microsoft C++ Build Tools][vcbuildtools] with the
  following components installed (Windows-only):
  - MSVC
  - Windows SDK
  - C++ ATL

##### Download and extract the source:
[Download from GitHub][master]
```
unzip ghidra-master
cd ghidra-master
```
**NOTE:** Instead of downloading the compressed source, you may instead want to clone the GitHub 
repository: `git clone https://github.com/VyacheslavIurevich/recompilation-ghidra.git`

##### Download additional build dependencies into source repository:
**NOTE:** If an Internet connection is available and you did not install Gradle, the following 
`gradle` commands may be replaced with `./gradle(.bat)`.
```
gradle -I gradle/support/fetchDependencies.gradle
```

##### Create development build: 
```
gradle buildGhidra
```
The compressed development build will be located at `build/dist/`.

For more detailed information on building Ghidra, please read the [Developer Guide][devguide].

[devguide]: DevGuide.md
[releases]: https://github.com/VyacheslavIurevich/recompilation-ghidra/releases
[jdk]: https://adoptium.net/temurin/releases
[gradle]: https://gradle.org/releases/
[python3]: https://www.python.org/downloads/
[vs]: https://visualstudio.microsoft.com/vs/community/
[vcbuildtools]: https://visualstudio.microsoft.com/visual-cpp-build-tools/
[master]: https://github.com/VyacheslavIurevich/recompilation-ghidra/archive/refs/heads/master.zip
